package com.jiebao.scanlib;

import java.io.IOException;




import com.ex.lib.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public final class BeepManager {

	private static final String TAG = BeepManager.class.getSimpleName();

	private static final float BEEP_VOLUME = 0.3f;

	private final Activity activity;
	private MediaPlayer mediaPlayer;
	private boolean playBeep = true;
	private boolean vibrate = false;

	public BeepManager(Activity activity) {
		this.activity = activity;
		initMediaPlayer();
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

	public void turnOffV() {
		mediaPlayer.setVolume(0, 0);
	}

	public void turnOnV() {
		mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	void initMediaPlayer() {
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mediaPlayer = buildMediaPlayer(activity);
	}

	public void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {

			mediaPlayer.start();
		}
		// if (vibrate) {
		// Vibrator vibrator = (Vibrator) activity
		// .getSystemService(Context.VIBRATOR_SERVICE);
		// vibrator.vibrate(VIBRATE_DURATION);
		// }
	}

	public void release() {
		if (mediaPlayer != null)
			mediaPlayer.stop();
		mediaPlayer.release();
		mediaPlayer = null;
	}

	private MediaPlayer buildMediaPlayer(Context activity) {

		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					public void onCompletion(MediaPlayer player) {
						player.seekTo(0);
					}
				});

		AssetFileDescriptor file = activity.getResources().openRawResourceFd(
				R.raw.beep);
		try {
			mediaPlayer.setDataSource(file.getFileDescriptor(),
					file.getStartOffset(), file.getLength());
			file.close();
			mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
			mediaPlayer.prepare();
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			mediaPlayer = null;
		}
		return mediaPlayer;
	}

}
