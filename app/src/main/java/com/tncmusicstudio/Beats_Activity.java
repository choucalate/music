package com.tncmusicstudio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.model.ButtonMap;
import com.model.RecNotes;
import com.model.RecManager;
import com.model.TupleStringInt;
import com.tncmusicstudio.R;

public class Beats_Activity extends SherlockFragment{
	boolean check = true;
	// boolean saving = true;

	Menu mymenu;
	private SPPlayer sp;

	Button beat1, beat2, beat3, beat4, beat5, beat6;
	Button[] mybeats = {};
	public static final String PREFS = "beat_prefs";
	public static final String beat_key = "mybeats";

	SharedPreferences settings;
	SharedPreferences.Editor edit;
	public static final String DEFAULT_STRING_ARRAY = "000000000000";
	static String myset = "";

	// Recording vars
	boolean recStart = false;
	private ArrayList<RecNotes> myRec;
	private Calendar startTime;
	// private int key;
	// saving
	private RecManager rm;
	private String origin = "_BEATS";
	private HashMap<Integer, ButtonMap> beats2Key;
	ButtonMap bm1, bm2, bm3, bm4, bm5, bm6;
	View rootview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rm = new RecManager(getActivity());
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		rootview = inflater.inflate(R.layout.activity_beats_, container, false);
		//setContentView(R.layout.activity_beats_);
		getActivity().setTitle("One Shots");
		setUpSound();

		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		settings = getActivity().getSharedPreferences(PREFS, 0);
		edit = settings.edit();
		/* then it's been set up, otherwise set it up */
		// myset = settings.getString(beat_key, DEFAULT_STRING_ARRAY);
		// if (myset.length() != 12) {
		// myset = DEFAULT_STRING_ARRAY;
		// edit.putString(beat_key, DEFAULT_STRING_ARRAY);
		// edit.commit();
		// }
		beats2Key = new HashMap<Integer, ButtonMap>();

		/**
		 * Creating all buttons instances
		 * */
		beat1 = (Button) rootview.findViewById(R.id.button1);
		beat2 = (Button) rootview.findViewById(R.id.button2);
		beat3 = (Button) rootview.findViewById(R.id.button3);
		beat4 = (Button) rootview.findViewById(R.id.button4);
		beat5 = (Button) rootview.findViewById(R.id.button5);
		beat6 = (Button) rootview.findViewById(R.id.button6);

		/* tag indicates that it's blue */
		resetTags();
		Button[] beatcopy = { beat1, beat2, beat3, beat4, beat5, beat6 };
		mybeats = beatcopy;
		// setBeatColors();

		/*
		 * For saving the beats- this is how it will work On action down, if
		 * saved is on && it's blue (the tag is true) -> then add it to the
		 * String and then commit else if saved is on && it's orange (the tag is
		 * true) -> then remove it from the String and then commit
		 */

		bm1 = new ButtonMap(beat1, 0, 6);
		bm2 = new ButtonMap(beat2, 1, 7);
		bm3 = new ButtonMap(beat3, 2, 8);
		bm4 = new ButtonMap(beat4, 3, 9);
		bm5 = new ButtonMap(beat5, 4, 10);
		bm6 = new ButtonMap(beat6, 5, 11);

		setUpButton(bm1);
		setUpButton(bm2);
		setUpButton(bm3);
		setUpButton(bm4);
		setUpButton(bm5);
		setUpButton(bm6);

		setUpKeyListeners();
		myRec = new ArrayList<RecNotes>();
		return rootview;
	}

	private void setUpKeyListeners() {
		Log.e("keylisten", "IM SETTING THIS UP NAOOOO!");
		// first 3 beats
		beats2Key.put(KeyEvent.KEYCODE_U, bm1);
		beats2Key.put(KeyEvent.KEYCODE_I, bm2);
		beats2Key.put(KeyEvent.KEYCODE_O, bm3);

		// bottom 3 beats
		beats2Key.put(KeyEvent.KEYCODE_J, bm4);
		beats2Key.put(KeyEvent.KEYCODE_K, bm5);
		beats2Key.put(KeyEvent.KEYCODE_L, bm6);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("key", "down: " + keyCode);
		ButtonMap result = null;
		if ((result = beats2Key.get(keyCode)) != null) {
			Log.i("key", "valid result: " + result);
			if (mymenu.getItem(1).getTitle().equals("One Shots")) {
				if (!commits(result.getB(), result.getNum2()))
					return false;
				playBeat(result.getNum2());
				if (recStart)
					recording(result.getNum2());

			} else {
				if (!commits(result.getB(), result.getNum1()))
					return false;
				playBeat(result.getNum1());
				if (recStart)
					recording(result.getNum1());
			}
			/* if it's blue */
			if (result.getB().getTag().equals(true)) {
				setBeatColor(result.getB(), true);
				/* tag orange */
				result.getB().setTag(false);
			} else {
				setBeatColor(result.getB(), false);
				/* tag blue */
				result.getB().setTag(true);
			}
			return true;
		}
		// play the sound based on the hashmap from keyCode to note

		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.i("key", "down: " + keyCode);
		ButtonMap result = null;
		if ((result = beats2Key.get(keyCode)) != null) {
			Log.i("key", "valid result: " + result);
			setBeatColor(result.getB(), false);
			result.getB().setTag(true);
			return true;
		}
		// play the sound based on the hashmap from keyCode to note

		return false;
	}

	private void setUpButton(final ButtonMap bm) {
		final Button b = bm.getB();
		final int num1 = bm.getNum1();
		final int num2 = bm.getNum2();
		b.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mymenu.getItem(1).getTitle().equals("One Shots")) {
						if (!commits(b, num2))
							return false;
						playBeat(num2);
						if (recStart)
							recording(num2);

					} else {
						if (!commits(b, num1))
							return false;
						playBeat(num1);
						if (recStart)
							recording(num1);
					}
					/* if it's blue */
					if (b.getTag().equals(true)) {
						setBeatColor(b, true);
						/* tag orange */
						b.setTag(false);
					} else {
						setBeatColor(b, false);
						/* tag blue */
						b.setTag(true);
					}
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					// if (!saving) {
					setBeatColor(b, false);
					b.setTag(true);
					// } else {
					//
					// }
				}
				return true;
			}
		});
	}

	public boolean commits(Button b, int num) {
		// ******buggy
		// if (saving) {
		// if (countNum() >= 4 && b.getTag().equals(true)) {
		// Toast.makeText(
		// this,
		// "You've already saved 4 beats, Unselect one to save another",
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }
		// // if saving and it's blue then add it to the string
		// // and commit it
		// if (b.getTag().equals(true)) {
		// myset = setString(myset, num, true);
		// edit.putString(beat_key, myset);
		// edit.commit();
		// } else {
		// // if saving and it's orange then take the beat
		// // from the string and commit
		// myset = setString(myset, num, false);
		// edit.putString(beat_key, myset);
		// edit.commit();
		// }
		// }
		return true;
	}

	public static int countNum() {
		int num = 0;
		// for (int i = 0; i < myset.length(); i++) {
		// if (myset.charAt(i) == '1')
		// num++;
		// }
		return num;
	}

	public String setString(String str, int index, boolean set) {
		// char[] myArr = str.toCharArray();
		// myArr[index] = (set) ? '1' : '0';
		String acc = "";
		// for (int i = 0; i < myArr.length; i++) {
		// acc += myArr[i];
		// }
		// System.out.println("acc: " + acc + " set: " + set + " index: " +
		// index);
		return acc;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.beats_, menu);
//		getSupportActionBar().setBackgroundDrawable(
//				new ColorDrawable(Color.rgb(223, 160, 23)));
		mymenu = menu;
		//return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*if (16908332 == item.getItemId())

		{
			Intent i = new Intent(this, AndroidDashboardDesignActivity.class);
			startActivity(i);
			return true;
		} else if (item.getItemId() == R.id.piano_icon) {
			Intent i = new Intent(this, PlayAroundActivity.class);
			startActivity(i);
			return true;
		} else*/
		if (item.getItemId() == R.id.loop_icon) {
			Toast.makeText(getActivity(), "Recording", Toast.LENGTH_SHORT).show();
			startRec();
			return true;
		} else if (item.getItemId() == R.id.toggle) {

			// System.out.println("String: " + myset);
			if (check) {
				check = false;
				item.setTitle(R.string.toggleBeats);
				getActivity().setTitle("Beats");
				// if(!saving)
				setDefaultColors();
				// else setBeatColors();

			} else {
				check = true;
				// if(!saving)

				// else setBeatColors();
				item.setTitle(R.string.toggleShots);
				getActivity().setTitle("One Shots");
				setDefaultColors();

			}
			return true;
		} else if (item.getItemId() == R.id.menu_sub) {
			// if (saving) {
			// change to beats
			item.setTitle(R.string.mode_play);
			// item.getItemId(R.id.loop_icon).setVisible(false);
			// ((Menu) item).getItem(1).setVisible(false);
			Toast.makeText(getActivity(), "Start playing some beats!",
					Toast.LENGTH_SHORT).show();
			// saving = false;
			setDefaultColors();
			option_save();


			// } else {
			// item.setTitle(R.string.mode_save);
			// Toast.makeText(this, "Start saving some beats",
			// Toast.LENGTH_SHORT).show();
			//
			// saving = true;
			// setBeatColors();
			// }
			return true;
		} else if (item.getItemId() == R.id.save_rec)
			return option_save();

		else if (item.getItemId() == R.id.list_rec) {
//			Intent goListRec = new Intent(this, SoonToBe.class);
//			startActivity(goListRec);
			return true;
		} else
			return false;
	}

	private void setBeatColors() {
		// for (int i = 0; i < mybeats.length; i++) {
		// int index = i;
		// if (check)
		// index += 6;
		// System.out.println("index: " + index + "myset: " + myset);
		// if (myset.charAt(index) == '0') {
		// mybeats[i].setBackgroundResource(R.drawable.blue5);
		// mybeats[i].setTag(true);
		// }
		// else {
		// mybeats[i].setBackgroundResource(R.drawable.orange);
		// mybeats[i].setTag(false);
		// }
		// }
	}

	private void setDefaultColors() {
		for (int i = 0; i < mybeats.length; i++) {
			setBeatColor(mybeats[i], false);
		}
	}

	private void setBeatColor(Button b, boolean on) {
		if (on)
			b.setBackgroundResource(R.drawable.neonorange);
		else
			b.setBackgroundResource(R.drawable.default_btn);
	}

	private void setUpSound() {
		// Log.e("SP", "FAILED ON ONCREATE");
		AssetManager am = getActivity().getAssets();
		// activity only stuff
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		this.sp = new SPPlayer(am, audioManager);
	}

	public static String[] beatArrss = { "beat1", "beat2", "beat3", "beat4",
			"beat5", "beat6", "clap", "snare", "oneshot3", "oneshot4",
			"oneshot5", "oneshot6" };

	public void playBeat(int i) {
		sp.playNote(beatArrss[i], 1);
	}

	public void resetTags() {
		beat1.setTag(true);
		beat2.setTag(true);
		beat3.setTag(true);
		beat4.setTag(true);
		beat5.setTag(true);
		beat6.setTag(true);
	}

	// Beat Recording

	public void startRec() {
		recStart = !recStart;

		if (recStart) {
			myRec.clear(); /* clear the old one */
			startTime = Calendar.getInstance();
			Log.i("startRec", "startTime " + startTime);
		}
	}

	public void recording(int key) {
		if (myRec.size() != 0) {
			Log.i("recording", "the recorded beat " + beatArrss[key]);
			long st = ((Calendar.getInstance().getTimeInMillis() - startTime
					.getTimeInMillis()) - myRec.get(myRec.size() - 1)
					.getCurrTime());
			/*
			 * Log.i("recstart", "time diff from last: " + st + " diff? : " +
			 * key.equals(myRec.get(myRec.size() - 1) .getNoteToPlay()));
			 */
			if (/*
				 * !key.equals(myRec.get(myRec.size() - 1).getNoteToPlay()) ||
				 */(st > 100)) {
				Log.i("recstart", "going to start recording for note: " + key
						+ " lastnote: "
						+ myRec.get(myRec.size() - 1).getNoteToPlay()
						+ " with st: " + st);
				myRec.add(new RecNotes((Calendar.getInstance()
						.getTimeInMillis() - startTime.getTimeInMillis()), key));
			}
		} else {
			Log.i("recstart", "going to start recording for note: " + key);
			myRec.add(new RecNotes(
					(Calendar.getInstance().getTimeInMillis() - startTime
							.getTimeInMillis()), key));
		}
	}

	// saving
	private boolean option_save() {
		if (myRec.size() == 0) {
			Toast.makeText(getActivity(), "no recording found", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		Log.i("option", "saving rec option");
		Toast.makeText(getActivity(), "saving your recording", Toast.LENGTH_SHORT)
				.show();
		rm.saveRec(myRec, origin);
		return true;
	}
}
