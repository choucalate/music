package com.tncmusicstudio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.PresetReverb;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import com.model.ButtonMap;
import com.model.RecManager;
import com.model.RecNotes;

/*
 * How to solidify player?
 * 1) delete looping true, and make my own looping so that when I start, I set a timer to seek back to 0 at a certain duration
 //todo- make 6 timers, and purge hwne each finished, and reinstate
 */

public class Mic_Test extends SherlockFragment{
	boolean mStartRecording = true;
	boolean mStartPlaying = true;

	private MediaPlayer mPlayer1 = null, mPlayer2 = null, mPlayer3, mPlayer4,
			mPlayer5, mPlayer6;
	private MediaRecorder mRecorder1 = null, mRecorder2 = null, mRecorder3,
			mRecorder4, mRecorder5, mRecorder6;
	private MediaPlayer[] mpList = { mPlayer1, mPlayer2, mPlayer3, mPlayer4,
			mPlayer5, mPlayer6 };
	private MediaRecorder[] mrList = { mRecorder1, mRecorder2, mRecorder3,
			mRecorder4, mRecorder5, mRecorder6 };

	private ArrayList<RecNotes> jam1 = null, jam2 = null, jam3 = null,
			jam4 = null, jam5 = null, jam6 = null;
	// private ArrayList<ArrayList<RecNotes>> list = b;

	private static final String LOG_TAG = "AudioRecordTests";
	private static final String FILE_NAME_UNMOD = "/audiorecordtest";
	private static final String FILE_NAME_EXT = ".mp4";

	private FilenameFilter fnf;
	private Button beat1, beat2, beat3, beat4, beat5, beat6;
	private int[] states;
	private Boolean[] isBeat = { false, false, false, false, false, false };

	private static String mFileName = null;
	Menu mymenu;
	private HashMap<Integer, ButtonMap> loop2Key;
	private HashMap<String, ArrayList<RecNotes>> rn;
	private RecManager rm;
	final Context mctx = getActivity();
	private SPPlayer sp;
	public static String[] beatArrss = { "beat1", "beat2", "beat3", "beat4",
			"beat5", "beat6", "clap", "snare", "oneshot3", "oneshot4",
			"oneshot5", "oneshot6" };

	private HashMap<Integer, ArrayList<RecNotes>> listJams;
	private int offset = 0;
	private ArrayList<RecNotes> myRec;
	private int numb;
	// private static SPPlayer soundPool;
	private Timer time;
	private Timer[] timeArr;
	private Boolean pauseJam;
	View rootview;

	public Mic_Test() {
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/audiorecordtest.3gp";

		fnf = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.contains(FILE_NAME_EXT))
					return true;
				return false;
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

//		setTitle("Mic Check one two");
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		rootview = inflater.inflate(R.layout.activity_mic, container, false);
		setHasOptionsMenu(true);
		rm = new RecManager(mctx);
		listJams = new HashMap<Integer, ArrayList<RecNotes>>();
		// listJams.put(0, jam1);
		// listJams.put(1, jam2);
		// listJams.put(2, jam3);
		// listJams.put(3, jam4);
		// listJams.put(4, jam5);
		// listJams.put(5, jam6);
		/**
		 * Creating all buttons instances
		 * */
		beat1 = (Button) rootview.findViewById(R.id.rec1);
		beat2 = (Button) rootview.findViewById(R.id.rec2);
		beat3 = (Button) rootview.findViewById(R.id.rec3);
		beat4 = (Button) rootview.findViewById(R.id.rec4);
		beat5 = (Button) rootview.findViewById(R.id.rec5);
		beat6 = (Button) rootview.findViewById(R.id.rec6);

		states = new int[6];

		timeArr = new Timer[6];
		for (int i = 0; i < timeArr.length; i++) {
			timeArr[i] = new Timer();
		}

		// isBeat = new Boolean[6];
		loop2Key = new HashMap<Integer, ButtonMap>();
		/* tag indicates that it's blue */
		// resetTags();
		if (beat1 == null) {
			System.out.println("beat 1 is null");
		}
		setRecListeners(beat1, 0);
		setRecListeners(beat2, 1);
		setRecListeners(beat3, 2);
		setRecListeners(beat4, 3);
		setRecListeners(beat5, 4);
		setRecListeners(beat6, 5);

		setUpKeyListeners();
		setUpSound();
		return rootview;
	}

	private void setUpKeyListeners() {
		Log.e("keylisten", "IM SETTING THIS UP NAOOOO!");
		// first 3 beats
		loop2Key.put(KeyEvent.KEYCODE_U, new ButtonMap(beat1, false));
		loop2Key.put(KeyEvent.KEYCODE_I, new ButtonMap(beat2, false));
		loop2Key.put(KeyEvent.KEYCODE_O, new ButtonMap(beat3, false));

		// bottom 3 beats
		loop2Key.put(KeyEvent.KEYCODE_J, new ButtonMap(beat4, false));
		loop2Key.put(KeyEvent.KEYCODE_K, new ButtonMap(beat5, false));
		loop2Key.put(KeyEvent.KEYCODE_L, new ButtonMap(beat6, false));

		// long clicks
		// first 3 beats
		loop2Key.put(KeyEvent.KEYCODE_Q, new ButtonMap(beat1, true));
		loop2Key.put(KeyEvent.KEYCODE_W, new ButtonMap(beat2, true));
		loop2Key.put(KeyEvent.KEYCODE_E, new ButtonMap(beat3, true));

		// bottom 3 beats
		loop2Key.put(KeyEvent.KEYCODE_A, new ButtonMap(beat4, true));
		loop2Key.put(KeyEvent.KEYCODE_S, new ButtonMap(beat5, true));
		loop2Key.put(KeyEvent.KEYCODE_D, new ButtonMap(beat6, true));
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("key", "down: " + keyCode);
		ButtonMap result = null;
		if ((result = loop2Key.get(keyCode)) != null) {
			Log.i("key", "valid result: " + result);
			if (result.getClick()) {
				result.getB().performLongClick();
			} else
				result.getB().performClick();
			return true;
		}
		// play the sound based on the hashmap from keyCode to note

		return false;
	}

	void setRecListeners(final Button b, final int num) {
		if (b == null) {
			System.out.println("asdfasdfasdfasdf");
			return;
		}
		b.setBackgroundResource(R.drawable.default_btn);

		b.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// I need either a toggle or a tag setter
				mFileName = getFileName(num);
				System.out
						.println("mFile: " + mFileName + " state: "
								+ states[num] + " mrList[0] : "
								+ (mrList[num] == null));
				switch (states[num]) {
				case 0:
					startRecording(b, num);
					break;
				case 1:
					b.setBackgroundResource(R.drawable.neonblue);

					System.out.println("is it null ? : "
							+ (mrList[num] == null));
					if (isBeat[num]) {
						Log.i("isBeat", "case 1" + isBeat[num]);
						myRec = listJams.get(num);
						playBack(1);
					} else if (mrList[num] != null) {
						stopRecording(num);
						startPlaying(num);
					}

					break;
				case 2:
					b.setBackgroundResource(R.drawable.neongreen);
					if (isBeat[num]) {
						Log.i("Pause Button", "PAUSE, PAUSE" + num + pauseJam);
						pauseJam = true;
					} else
						pausePlaying(num);
					break;
				default:
					b.setBackgroundResource(R.drawable.neonblue);
					if (!isBeat[num]) {
						resumePlaying(num);
					} else {
						myRec = listJams.get(num);
						playBack(1);
					}
					states[num] -= 2;
					break;
				}
				System.out.println("finish? : " + (mrList[0] == null));

				states[num]++;

			}

		});
		b.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// <<<<<<< HEAD
				// mFileName = getFileName(num);
				//
				// System.out.println("long click- loading previous : " + num);
				// // set the state as 2, which is the paused neon green state
				// if (searchFiles(num)) {
				// states[num] = 2;
				// b.setBackgroundResource(R.drawable.neonblue);
				// startPlaying(num);
				// } else {
				// Toast.makeText(getApplicationContext(),
				// "sorry, no file name found preloaded",
				// Toast.LENGTH_SHORT).show();
				// =======
				System.out.println("long click");

				if (states[num] == 0) {
					Log.i("long click", "loading jam in " + num);

					showListRecs(rm, num);
					b.setBackgroundResource(R.drawable.neongreen);
					// myRec = listJams.get(num);
					// playBack(1);
					// startRecording(num);
				}
				// TODO Auto-generated method stub
				return true;
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activity_mic, menu);
//		getSupportActionBar().setBackgroundDrawable(
//				new ColorDrawable(Color.rgb(9, 59, 99)));
		mymenu = menu;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.mic_icon) {

			onRecord(mStartRecording);
			mStartRecording = !mStartRecording;
			return true;
		} else if (item.getItemId() == R.id.play_icon) {
			onPlay(mStartPlaying);
			mStartPlaying = !mStartPlaying;
			return true;
		} else if (item.getItemId() == R.id.save_icon) {
			// gonna try looping
			// mPlayer.start();

			File[] myfiles = Environment.getExternalStorageDirectory()
					.listFiles(fnf);
			if (myfiles != null) {
				for (File i : myfiles) {
					System.out.println(" i : " + i.getName());
				}
			}
			System.out.println(" current file: " + mFileName);
			return true;
		} else
			return false;
	}

	private boolean searchFiles(int num) {
		File[] myfiles = Environment.getExternalStorageDirectory().listFiles(
				fnf);
		if (myfiles != null) {

			for (File i : myfiles) {
				System.out.println(" get FileName: " + getFileName(num)
						+ " i: " + i.getName());
				if (getFileName(num).contains(i.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private void onRecord(boolean start) {
		if (start) {
			// startRecording();
		} else {
			// stopRecording();
		}
	}

	private void onPlay(boolean start) {
		if (start) {
			// startPlaying();
		} else {
			// stopPlaying();
		}
	}

	private void stopAll() {
		for (int i = 0; i < mpList.length; i++) {
			if (mpList[i] != null) {
				stopPlaying(i);
			}
		}
	}

	private void startPlaying(final int num) {
		final MediaPlayer player = new MediaPlayer();
		final MediaPlayer player2 = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);

		System.out.println("starting to play: " + num + " with file: "
				+ mFileName);

		mpList[num] = player;

		PresetReverb mReverb = new PresetReverb(0, 0);// <<<<<<<<<<<<<
		mReverb.setPreset(PresetReverb.PRESET_SMALLROOM);
		mReverb.setEnabled(true);

		try {
			// File file = new File(mFileName);
			// FileInputStream inputStream = new FileInputStream(file);
			// cutting off 200 b/c there's latency

			// player.setDataSource(inputStream.getFD(), 0,
			// player.getDuration()- 200);
			// inputStream.close();
			player.setDataSource(mFileName);
			player.attachAuxEffect(mReverb.getId());
			player.setAuxEffectSendLevel(3.0f);
			player2.setDataSource(mFileName);
			player2.attachAuxEffect(mReverb.getId());
			player2.setAuxEffectSendLevel(3.0f);

			player.prepare();
			player2.prepare();
			// player.setLooping(true);

			player.start();
			timeArr[num].scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (player.isPlaying())
						player2.start();
					else
						player.start();
					System.out.println("repeat!");
				}
			}, player.getDuration() - 200, player.getDuration() - 200);
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void pausePlaying(int num) {
		MediaPlayer player = mpList[num];
		player.pause();

		timeArr[num].cancel();
		timeArr[num].purge();
	}

	private void resumePlaying(int num) {
		timeArr[num] = new Timer();
		final MediaPlayer player = mpList[num], player2 = new MediaPlayer();
		try {
			player2.setDataSource(mFileName);

			player2.prepare();

			timeArr[num].scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (player.isPlaying()) {
						player2.seekTo(0);
						player2.start();
					} else {
						player.seekTo(0);
						player.start();
					}
					System.out.println("repeat!");
				}
			}, 0, player.getDuration() - 200);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// player.seekTo(0);
		// player.start();
	}

	private void stopPlaying(int num) {
		MediaPlayer player = mpList[num];
		player.stop();
		player.release();
		player = null;
	}

	private void startRecording(Button b, int num) {
		MediaRecorder recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// recorder.setAudioChannels(2);
		recorder.setAudioEncodingBitRate(12200);
		recorder.setAudioSamplingRate(8000);
		recorder.setOutputFile(mFileName);
		try {
			recorder.prepare();
			Thread.sleep(0);
		} catch (IOException e) {
			System.out.println("failed in ioexcept");
			Log.e(LOG_TAG, "prepare() failed");
		} catch (InterruptedException ex) {
			Log.e(LOG_TAG, "interrupted sleep");
		}

		mrList[num] = recorder;
		System.out.println("starting to record: " + (recorder == null)
				+ " mrlist: " + (mrList[num] == null));

		recorder.start();

		try {
			Thread.sleep(400);

			b.setBackgroundResource(R.drawable.neonorange);

			Toast.makeText(getActivity(), "start recording!",
					Toast.LENGTH_SHORT).show();
		} catch (InterruptedException ex) {

		}
	}

	private void stopRecording(int num) {
		MediaRecorder recorder = mrList[num];
		System.out.println("finishing record: " + (recorder == null)
				+ " filename: " + mFileName);

		recorder.stop();
		recorder.release();
		recorder = null;
	}

	@Override
	public void onPause() {
		super.onPause();
		stopAll();
		if (mRecorder1 != null) {
			mRecorder1.release();
			mRecorder1 = null;
		}

		if (mPlayer1 != null) {
			mPlayer1.release();
			mPlayer1 = null;
		}
	}

	//@Override
	//public void onDestroy() {


	//	super.onDestroy();

	//}

	@Override
	public void onStop() {
		super.onStop();

		stopAll();
	}

	private String getFileName(int i) {
		String str = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		str += FILE_NAME_UNMOD + i + FILE_NAME_EXT;
		return str;
	}

	private boolean showListRecs(RecManager rm, int num) {

		// call recordListActivity
		this.numb = num;
		rn = rm.loadRec();
		CharSequence[] items = new CharSequence[rn.size()];
		Set<String> rnKeys = rn.keySet();

		// for (int i = 0; i < items.length; i++) {
		// items[i] = "Recording " + i;
		// }
		int i = 0;
		for (String st : rnKeys) {
			items[i] = st;
			i++;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Load Your Recordings");

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				Set<String> re = rn.keySet();
				int i = 0;
				String elem = "";
				for (String st : re) {
					if (i == item) {
						elem = st;
						break;
					}
					i++;
				}
				Log.i("showListRecs", "elem in list = " + elem);
				Boolean f = setMyRec(rn.get(elem));
				Log.i("showListRecs", "elem " + elem + "is set?" + f);
				Toast.makeText(mctx, "Jam Loaded!", Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}

	private boolean setMyRec(ArrayList<RecNotes> song) {
		// for(int i = 0; i < states.length; i++){
		int i = numb;
		if (states[i] == 0) {
			Log.i("setMyRec", "setting recording in " + i);
			listJams.remove(i);
			Log.i("setMyRec", "adding to list");
			listJams.put(i, song);
			isBeat[i] = true;
			states[i]++;
			// break;
		}
		// }
		return true;
	}

	private void setUpSound() {
		// Log.e("SP", "FAILED ON ONCREATE");
		AssetManager am = getActivity().getAssets();
		// activity only stuff
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		this.sp = new SPPlayer(am, audioManager);
	}

	public void playBack(int speed) {

		time = new Timer();
		Calendar mycal = Calendar.getInstance();
		Calendar copy = Calendar.getInstance();

		Log.i("recstart", "size: " + myRec.size());

		for (int i = 0; i < myRec.size(); i++) {
			// int i = 0; ############## Loop code, won't work probably
			// while(true){
			// if(!pauseJam){
			// if(i == myRec.size())
			// i = 0;
			mycal = copy;
			if (i == 0) {
				mycal.add(Calendar.MILLISECOND, (int) (myRec.get(i)
						.getCurrTime() + offset));
			} else {
				mycal.add(Calendar.MILLISECOND, (int) (myRec.get(i)
						.getCurrTime() + offset - myRec.get(i - 1)
						.getCurrTime()));
			}
			Log.i("recstart",
					"going to play it at: "
							+ (mycal.getTimeInMillis() - copy.getTimeInMillis())
							+ " with the offset: "
							+ (int) myRec.get(i).getCurrTime());
			final int index = i;
			time.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stubx`
					if (!myRec.get(index).isBeat()) {

						Log.i("recstart",
								"about to play note: "
										+ myRec.get(index).getNoteToPlay());
						sp.playNote(myRec.get(index).getNoteToPlay(), 1);

					} else {
						// is beat
						playBeat(myRec.get(index).getBeat());
						Log.i("recstart", "is a beat! playing beat: "
								+ myRec.get(index).getBeat());
					}
				}

			}, mycal.getTime());
			// i++;
			// }
		}
	}

	public void playBeat(int i) {
		sp.playNote(beatArrss[i], 1);
	}
}
