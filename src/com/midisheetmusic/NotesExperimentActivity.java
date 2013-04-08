package com.midisheetmusic;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.Button;

public class NotesExperimentActivity extends Activity implements OnClickListener {

	private static final int duration = 1;
	private static final int sampleRate = 8000;
	private static final int numSamples = duration * sampleRate;

	// private methods for basic information about notes.
	// Used http://en.wikipedia.org/wiki/Piano_key_frequencies for the
	// frequencies
	private final double cNote = 261.626;
	private final double dFlatNote = 277.183;
	private final double dNote = 293.665;
	private final double eFlatNote = 311.127;
	private final double eNote = 329.628;
	private final double fNote = 349.228;
	private final double gFlatNote = 369.994;
	private final double gNote = 391.995;
	private final double aFlatNote = 415.305;
	private final double aNote = 440.000;
	private final double bFlatNote = 466.164;
	private final double bNote = 493.883;
	private final double c2Note = 523.251;

	private Button buttonC;
	private Button buttonDflat;
	private Button buttonD;
	private Button buttonEflat;
	private Button buttonE;
	private Button buttonF;
	private Button buttonGflat;
	private Button buttonG;
	private Button buttonAflat;
	private Button buttonA;
	private Button buttonBflat;
	private Button buttonB;
	private Button buttonC2;

	private byte[] generateSound(double frequency) {
		double[] soundSample = new double[numSamples];
		for (int i = 0; i < numSamples; i++) {
			soundSample[i] = Math.sin(2 * Math.PI * i
					/ (sampleRate / frequency));
		}

		byte[] sound = new byte[2 * numSamples];
		int idx = 0;
		for (double dVal : soundSample) {
			short val = (short) (dVal * 32767);
			sound[idx++] = (byte) (val & 0x00ff);
			sound[idx++] = (byte) ((val & 0xff00) >>> 8);
		}

		return sound;
	}

	private void playSound(byte[] sound) {
		final byte[] play = sound;
		// Concurrency for music playing
		(new Thread(new Runnable() {
			public void run() {
				AudioTrack audioTrack = new AudioTrack(
						AudioManager.STREAM_MUSIC, 8000,
						AudioFormat.CHANNEL_OUT_DEFAULT,
						AudioFormat.ENCODING_PCM_16BIT, numSamples,
						AudioTrack.MODE_STATIC);
				audioTrack.write(play, 0, numSamples);
				audioTrack.play();
			}
		})).start();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonC = (Button) findViewById(R.id.buttonC);
		buttonDflat = (Button) findViewById(R.id.buttonDflat);
		buttonD = (Button) findViewById(R.id.buttonD);
		buttonEflat = (Button) findViewById(R.id.buttonEflat);
		buttonE = (Button) findViewById(R.id.buttonE);
		buttonF = (Button) findViewById(R.id.buttonF);
		buttonGflat = (Button) findViewById(R.id.buttonGflat);
		buttonG = (Button) findViewById(R.id.buttonG);
		buttonAflat = (Button) findViewById(R.id.buttonAflat);
		buttonA = (Button) findViewById(R.id.buttonA);
		buttonBflat = (Button) findViewById(R.id.buttonBflat);
		buttonB = (Button) findViewById(R.id.buttonB);

		buttonC.setOnClickListener(this);
		buttonDflat.setOnClickListener(this);
		buttonD.setOnClickListener(this);
		buttonEflat.setOnClickListener(this);
		buttonE.setOnClickListener(this);
		buttonF.setOnClickListener(this);
		buttonGflat.setOnClickListener(this);
		buttonG.setOnClickListener(this);
		buttonAflat.setOnClickListener(this);
		buttonA.setOnClickListener(this);
		buttonBflat.setOnClickListener(this);
		buttonB.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO need create a object method that changes the pitch
		if (v == buttonC) {
			playSound(generateSound(cNote));
		}
		if (v == buttonDflat) {
			playSound(generateSound(dNote));
		}
		if (v == buttonD) {
			playSound(generateSound(dFlatNote));
		}
		if (v == buttonEflat) {
			playSound(generateSound(eFlatNote));
		}
		if (v == buttonE) {
			playSound(generateSound(eNote));
		}
		if (v == buttonF) {
			playSound(generateSound(fNote));
		}
		if (v == buttonGflat) {
			playSound(generateSound(gFlatNote));
		}
		if (v == buttonG) {
			playSound(generateSound(gNote));
		}
		if (v == buttonAflat) {
			playSound(generateSound(aFlatNote));
		}
		if (v == buttonA) {
			playSound(generateSound(aNote));
		}
		if (v == buttonBflat) {
			playSound(generateSound(bFlatNote));
		}
		if (v == buttonB) {
			playSound(generateSound(bNote));
		}
		if (v == buttonC2) {
			playSound(generateSound(c2Note));
		}

	}

}