package com.jiebao.scanlib;

import com.ex.lib.R;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

public class ScanHintManager {

	private static final long VIBRATE_DURATION = 200;

	// 常量
	private final float BEEP_VOLUME = 0.3f;

	// 变量
	private boolean playBeep = false;
	private boolean vibrate = false;

	// 控制�?
	private Context mContext;
	private MediaPlayer mMediaPlayer;
	private Vibrator mVibrator;

	public ScanHintManager(Context context, boolean playBeep, boolean vibrate) {
		super();
		this.mContext = context;
		this.playBeep = playBeep;
		this.vibrate = vibrate;

		initial();
	}

	public boolean isPlayBeep() {
		return playBeep;
	}

	public void setPlayBeep(boolean playBeep) {
		this.playBeep = playBeep;
	}

	public boolean isVibrate() {
		return vibrate;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	private void initial() {
		// 判断mMediaPlayer是否已生成，有则先释放，再创�?
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mMediaPlayer = new MediaPlayer();

		/* 监听播放是否完成 */

		AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(
				R.raw.chongfu);
		try {
			mMediaPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getDeclaredLength());
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
			mMediaPlayer.prepare();
			mMediaPlayer.setLooping(false);
			afd.close();
		} catch (Exception e) {
		}

		// initialVibrator
		mVibrator = (Vibrator) mContext
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void play() {
		if (playBeep) {
			mMediaPlayer.start();
		}
		if (vibrate) {
			mVibrator.vibrate(VIBRATE_DURATION);
		}

	}

}
