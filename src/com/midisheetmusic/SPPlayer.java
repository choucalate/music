package com.midisheetmusic;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import android.widget.Toast;

public class SPPlayer {
	private boolean loaded = false;
	SoundPool soundPool;
	AudioManager audio;
	private int priority;
	private String lastNote;
	long justP = 0, newP = 0;
	// beat1.ogg
	private String[] oggArr = { "piano_A4_sharp.ogg", "piano_A4.ogg",
			"piano_B4.ogg", "piano_C4_sharp.ogg", "piano_D4_sharp.ogg",
			"piano_D4.ogg", "piano_E4.ogg", "piano_F4_sharp.ogg",
			"piano_F4.ogg", "piano_G4_sharp.ogg", "piano_G4.ogg",
			"piano_middle_C4.ogg", "piano_A3_sharp.ogg", "piano_A3.ogg",
			"piano_B3.ogg", "piano_C3_sharp.ogg", "piano_D3_sharp.ogg",
			"piano_D3.ogg", "piano_E3.ogg", "piano_F3_sharp.ogg",
			"piano_F3.ogg", "piano_G3_sharp.ogg", "piano_G3.ogg",
			"piano_C3.ogg" };
	private String[] beatsArr = { "Beat1.ogg", "Beat2.ogg", "Beat3.ogg" };
	private int a4S = 0, a4 = 0, b4 = 0, c4S = 0, d4S = 0, d4 = 0, e4 = 0,
			f4S = 0, f4 = 0, g4S = 0, g4 = 0, c4 = 0;
	private int a3S = 0, a3 = 0, b3 = 0, c3S = 0, d3S = 0, d3 = 0, e3 = 0,
			f3S = 0, f3 = 0, g3S = 0, g3 = 0, c3 = 0;
	private int[] soundID_Arr = { a4S, a4, b4, c4S, d4S, d4, e4, f4S, f4, g4S,
			g4, c4, a3S, a3, b3, c3S, d3S, d3, e3, f3S, f3, g3S, g3, c3 };
	private String keya4s = "keya4s", keya4 = "keya4", keyb4 = "keyb4";
	private String keyc4s = "keyc4s", keyd4s = "keyd4s", keyd4 = "keyd4";
	private String keye4 = "keye4", keyf4s = "keyf4s", keyf4 = "keyf4";
	private String keyg4s = "keyg4s", keyg4 = "keyg4", keyc4 = "keyc4";
	private String keya3s = "keya3s", keya3 = "keya3", keyb3 = "keyb3";
	private String keyc3s = "keyc3s", keyd3s = "keyd3s", keyd3 = "keyd3";
	private String keye3 = "keye3", keyf3s = "keyf3s", keyf3 = "keyf3";
	private String keyg3s = "keyg3s", keyg3 = "keyg3", keyc3 = "keyc3";
	private String[] keyArr = { keya4s, keya4, keyb4, keyc4s, keyd4s, keyd4,
			keye4, keyf4s, keyf4, keyg4s, keyg4, keyc4, keya3s, keya3, keyb3,
			keyc3s, keyd3s, keyd3, keye3, keyf3s, keyf3, keyg3s, keyg3, keyc3 };
	private String[] beatLoopsArr = { "beat1", "beat2", "beat3" };
	private Map<String, Integer> mapKeys = new HashMap<String, Integer>();
	private Map<String, Integer> mapPriority = new HashMap<String, Integer>();

	public SPPlayer() {

	}

	public SPPlayer(AssetManager Asset, AudioManager theAud) {
		// Log.e("SP", "Ran this many times");
		this.audio = theAud;
		AssetManager am = Asset;
		soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0); // maybechangetoasync
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				// Log.e("SP", "LOADED HERE asdf");
				loaded = true;
			}
		});
		AssetFileDescriptor pianoa4S = null, pianoa4 = null, pianob4 = null, pianoc4S = null, pianod4S = null, pianod4 = null, pianoe4 = null, pianof4S = null, pianof4 = null, pianog4S = null, pianog4 = null, pianoc4 = null;
		AssetFileDescriptor pianoa3S = null, pianoa3 = null, pianob3 = null, pianoc3S = null, pianod3S = null, pianod3 = null, pianoe3 = null, pianof3S = null, pianof3 = null, pianog3S = null, pianog3 = null, pianoc3 = null;
		AssetFileDescriptor[] piano = { pianoa4S, pianoa4, pianob4, pianoc4S,
				pianod4S, pianod4, pianoe4, pianof4S, pianof4, pianog4S,
				pianog4, pianoc4, pianoa3S, pianoa3, pianob3, pianoc3S,
				pianod3S, pianod3, pianoe3, pianof3S, pianof3, pianog3S,
				pianog3, pianoc3 };
		AssetFileDescriptor beat1 = null, beat2 = null, beat3 = null; 
		AssetFileDescriptor[] beats = { beat1, beat2, beat3 };
		try {
			for (int i = 0; i < piano.length; i++) {

				piano[i] = am.openFd(oggArr[i]); // put these strings
													// into an array
				priority = i + 1;
				soundID_Arr[i] = soundPool.load(piano[i], priority);
				// conveniently map the values in
				mapKeys.put(keyArr[i], soundID_Arr[i]);
				mapPriority.put(keyArr[i], priority);
			}
			
			for (int i = 0; i < beats.length; i++) {
				beats[i] = am.openFd(beatsArr[i]);
				priority = i + 1;
				mapKeys.put(beatLoopsArr[i], soundPool.load(beats[i], priority));
				mapPriority.put(beatLoopsArr[i], priority);
			}
			
		} catch (Exception ex) {
			Log.e("SP", "FAILED TO LOAD");
		}
		// set up aud manager
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void playNote(String note, int priority) {
		if (note == "")
			return;
		// Log.i("the note is", note);
		// needa lastnote & delta for each note
		long delta = Math.abs(justP - System.currentTimeMillis());
		// Log.i("SP", "delta time: " + delta);
		if (lastNote != note) {
			try {
				float actualVolume = (float) audio
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				float maxVolume = (float) audio
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				float volume = actualVolume / maxVolume;
				// Is the sound loaded already?
				// Log.i("SP", "GOES HERE " + loaded);

				if (mapKeys.get(note) != null) {
					int success = soundPool.play(mapKeys.get(note), volume,
							volume, mapPriority.get(note), 0, 1f);
					if (success == 0)
						Log.e("SP", "WE'VE FAILED");
					lastNote = note;
				} else
					Log.e("SP", "wrong note : " + note);
				justP = System.currentTimeMillis();
				// Log.i("SP", "after Play Time " + justP);
			} catch (Exception ex) {
				Log.e("SP", "PROBLEM");
			}
		} else {
			justP = System.currentTimeMillis();
			Log.e("SP", "delta not passed : " + delta);
		}
	}

	public void stopNote(int note) {
		soundPool.stop(note);
	}

	public void release() {
		soundPool.release();
	}
}
