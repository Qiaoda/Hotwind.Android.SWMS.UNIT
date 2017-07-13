package com.ex.lib.ext.barcode;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ex.lib.R;
import com.ex.lib.ext.barcode.camera.CameraManager;
import com.ex.lib.ext.barcode.control.AmbientLightManager;
import com.ex.lib.ext.barcode.control.BeepManager;
import com.ex.lib.ext.barcode.decode.CaptureActivityHandler;
import com.ex.lib.ext.barcode.decode.FinishListener;
import com.ex.lib.ext.barcode.decode.InactivityTimer;
import com.ex.lib.ext.barcode.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

/**
 * @ClassName: ExCaptureActivity
 * @Description: 二维码扫描页面
 * @author Aloneter
 * @date 2014-10-31 上午11:01:54
 * @Version 1.0
 * 
 */
public final class ExCaptureActivity extends Activity implements SurfaceHolder.Callback {

	public static final String EX_QUER_DECODE_RESULT = "decodeString";

	private ImageView btn_back;
	private ImageView btn_torch;
	private boolean isTorchOn = false;
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private Result savedResultToShow;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType, ?> decodeHints;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private BeepManager beepManager;
	private AmbientLightManager ambientLightManager;

	public ViewfinderView getViewfinderView() {

		return viewfinderView;
	}

	public Handler getHandler() {

		return handler;
	}

	public CameraManager getCameraManager() {

		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.ex_activity_capture);

		btn_back = (ImageView) findViewById(R.id.capture_back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ExCaptureActivity.this.finish();
			}
		});
		btn_torch = (ImageView) findViewById(R.id.capture_flash);
		btn_torch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 开关灯
				if (isTorchOn) {
					isTorchOn = false;
					// btn_torch.setText("开灯");
					cameraManager.setTorch(false);
				} else {
					isTorchOn = true;
					// btn_torch.setText("关灯");
					cameraManager.setTorch(true);
				}
			}
		});

		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		beepManager = new BeepManager(this);
		ambientLightManager = new AmbientLightManager(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);

		handler = null;
		resetStatusView();

		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		beepManager.updatePrefs();
		ambientLightManager.start(cameraManager);

		inactivityTimer.onResume();

		decodeFormats = null;
		characterSet = null;
	}

	@Override
	protected void onPause() {

		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}

		inactivityTimer.onPause();
		ambientLightManager.stop();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}

		super.onPause();
	}

	@Override
	protected void onDestroy() {

		inactivityTimer.shutdown();
		viewfinderView.recycleLineDrawable();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA:// 拦截相机键
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {

		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {

		inactivityTimer.onActivity();
		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(100);

		String msg = rawResult.getText();
		if (msg == null || "".equals(msg)) {
			msg = "无法识别";
		}

		setResult(Activity.RESULT_OK, new Intent().putExtra(ExCaptureActivity.EX_QUER_DECODE_RESULT, msg));

		finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {

		if (surfaceHolder == null) {

			return;
		}
		if (cameraManager.isOpen()) {

			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
			}

			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("警告");
		builder.setMessage("抱歉，相机出现问题，您可能需要重启设备");
		builder.setPositiveButton("确定", new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {

		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}

		resetStatusView();
	}

	private void resetStatusView() {

		viewfinderView.setVisibility(View.VISIBLE);
	}

	public void drawViewfinder() {

		viewfinderView.drawViewfinder();
	}

}
