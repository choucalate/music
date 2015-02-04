package com.testmusic;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.tncmusicstudio.TutorialMSActivity;

public class PianoThread extends Thread {
	private static final String TAG = PianoThread.class.getSimpleName();

	private SurfaceHolder surfaceHolder;
	private TutorialMSActivity gamePanel;
	private boolean running;
	private ImageView[] ivArr;
	private Animation animation;

	public void setRunning(boolean running) {
		this.running = running;
	}

	public PianoThread(ImageView[] arr, Animation anim) {
		super();
		ivArr = new ImageView[arr.length];
		for (int i = 0; i < arr.length; i++)
			ivArr[i] = arr[i];
		animation = anim;
		/*
		 * this.surfaceHolder = surfaceHolder; this.gamePanel = gamePanel;
		 */
	}

	public PianoThread() {
		super();
		Log.e("pianoThread", "COMES INTO THREAD!");

	}

	@Override
	public void run() {
		long tickCount = 0L;
		Log.d(TAG, "Starting game loop");

		while (running) {
			tickCount++;
			// long count = AnimationUtils.currentAnimationTimeMillis();
			if (tickCount % 5000 == 0) {
				if (tickCount % 10000 == 0) {
					// gamePanel.noteFall();
					ivArr[0].startAnimation(animation);
					Log.i("animation", "count 2000");
				} else {
					Log.i("animation", "count 1000");
					// ivArr[1].startAnimation(animation);
				}
			}
			// update game state
			// render state to the screen
			// Log.i(TAG, "Game loop while : " + tickCount + " times vs. " +
			// AnimationUtils.currentAnimationTimeMillis());

		}
		Log.d(TAG, "Game loop executed " + tickCount + " times");
	}

}
