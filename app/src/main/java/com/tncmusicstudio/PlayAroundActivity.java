package com.tncmusicstudio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import com.model.NotePlay;
import com.model.RecManager;
import com.model.RecNotes;
import com.model.TupleStringInt;

public class PlayAroundActivity extends SherlockFragment {
	String[] values = new String[] { "Level 0: Keyboard Note Training! ",
			"Level 1: Major Scales", "Level 2: Learning Chords",
			"Level 3: Actual Songs" };
	public static final String MidiDataID = "MidiDataID";
	public static final String MidiTitleID = "MidiTitleID";
	public static final int settingsRequestCode = 1;

	private Piano piano; /* The piano at the top */
	private LinearLayout layout; /* THe layout */
	private long midiCRC; /* CRC of the midi bytes */
	private LevelActivity tutorialActivityLevel;

	private LevelActivity mTuts;/* to get the string */
	private String pos;
	private boolean stop = false;
	/* for note playing */
	private SPPlayer sp;
	private boolean loaded = false;

	private String origin = "_PIANO";
	/* To be able to collapse items with beats */
	Menu mymenu;

	/* button to guide image falling process */
	Button playNote, backTut, nextTut, restartTut;
	Spinner beats;
	Spinner save_list;
	/**
	 * "note" + "key" + "1|2 position" +
	 * "1st primary or 2nd secondary imageview"
	 */
	ImageView noteC1A, noteC1B, noteD1A, noteD1B, noteE1A, noteE1B, noteF1A,
			noteF1B, noteG1A, noteG1B, noteA1A, noteA1B, noteB1A, noteB1B,
			noteC2A, noteC2B;
	ImageView[] ivArray;
	/* layouts initialized */

	/* init thread */
	// PianoThread pthread;
	/* animation stuff */
	Animation animC1;
	Animation animC2;
	Animation animD1;
	Animation animD2;
	Animation animE1;
	Animation animE2;
	Animation animF1;
	Animation animF2;
	Animation anim1;
	Animation anim2, anim3, anim4, anim5, anim6, anim7;
	int i = 0;// random
	int c1Count = 0;
	int d1Count = 0;

	// private noteFallTask nFallAnim;
	private LinearLayout popupLayout; /* layout for pop-up window */
	int popUpCount;
	boolean click = true;
	boolean touchable = true;
	/* for popup windows */
	LayoutParams params;
	PopupWindow popUp;
	TextView textview;

	int index = 0;
	int tArr[] = {};

	int numClicks = 0; // count numclicks to the buttons

	static int callBack = 0;

	RelativeLayout rl;

	private pianoAsync setPiano;
	ProgressDialog dialog;

//	private NotePlay[] cmajor1, cmajor2, littlelamb1, littlelamb2, littlelamb3,
//			twinkle1, twinkle2, twinkle3, twinkle4, biebs1, biebs2, biebs3,
//			biebs4, biebs5;

	/* recording file */
	private final static String filename = "Rec.txt";
	// ArrayList<ArrayList<RecNotes>> rn = null;
	// HashMap implementation
	HashMap<String, ArrayList<RecNotes>> rn = null;

	final Context mctx = getActivity();
	RecManager rm;
	private int beatstate = 0;
	SharedPreferences settings;
	SharedPreferences.Editor edit;
	String myset;
	private HashMap<Integer, TupleStringInt> note2Key;

	View rootview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rm = new RecManager(getActivity());
		//super.onCreate(savedInstanceState);
		popUpCount = 0;
		ivArray = new ImageView[2];
		setHasOptionsMenu(true);
		rootview = inflater.inflate(R.layout.simplelayout, container, false);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getActivity().setTitle("Music Studio");
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		settings = getActivity().getSharedPreferences(Beats_Activity.PREFS, 0);
		edit = settings.edit();
		/* then it's been set up, otherwise set it up */
		myset = settings.getString(Beats_Activity.beat_key,
				Beats_Activity.DEFAULT_STRING_ARRAY);
		setUpHash();
		if (myset.length() != 12) {
			myset = Beats_Activity.DEFAULT_STRING_ARRAY;
			edit.putString(Beats_Activity.beat_key,
					Beats_Activity.DEFAULT_STRING_ARRAY);
			edit.commit();
		}

		try {
			setPiano = new pianoAsync();
			setPiano.execute();

			// else
			// createView();
		} catch (Exception ex) {
			Log.e("animation", "fail");
		}


        // now set up the key listeners
//        rootview.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                Log.e("KEY", "SOMETHING HITSOMETHING HITSOMETHING HITSOMETHING HITSOMETHING HITSOMETHING HITSOMETHING HIT");
//                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    //do something here
//                    onKeyDown(keyCode, event);
//                    return true;
//                } else if (event.getAction() == KeyEvent.ACTION_UP) {
//                    onKeyUp(keyCode, event);
//                    return true;
//                }
//                return false;
//            }
//        });
		return rootview;
	}

	private void setUpAnimation() {
		anim1 = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
		anim1.setFillAfter(false);
		anim2 = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);//R.animator.falling);
		anim2.setFillAfter(false);
		anim3 = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);//R.animator.falling);
		anim3.setFillAfter(false);
		anim4 = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);//R.animator.falling);
		anim4.setFillAfter(false);
		anim5 = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);//R.animator.falling);
		anim5.setFillAfter(false);

	}

	private void setUpSound() {
		// Log.e("SP", "FAILED ON ONCREATE");
		AssetManager am = getActivity().getAssets();
		// activity only stuff
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		sp = new SPPlayer(am, audioManager);
	}

	class NBNodes {
		String note;
		int duration;

		NBNodes() {

		}
	}

	/* Create the MidiPlayer and Piano views */
	//

	public static int getCallBack() {
		return callBack;
	}

	public static void callBack(int value) {
		callBack = value;
	}

	private void createViewOnlyPiano() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		// Point size = new Point();
		int width = display.getWidth();
		int height = display.getHeight();
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		layout = (LinearLayout)rootview.findViewById(R.id.linearlayout1);//new LinearLayout(getActivity());
		//layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
		/* for the button record */
		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);

		/* for the button replay */
		LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		/* for the button beat1 */
		LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);

		/* for the button beat2 */
		LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);

		/* for the spinner beat set */
		LinearLayout.LayoutParams layoutParams6 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);

		/* for the spinner context menu */
		LinearLayout.LayoutParams layoutParams7 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		LinearLayout rl = new LinearLayout(getActivity());
		rl.setOrientation(LinearLayout.HORIZONTAL);

//		backTut = new Button(this);
//		backTut.setText("Beat1");
//		backTut.setLayoutParams(layoutParams4);

		/*
		 * For the button to play the restart tutorial
		 */
//		restartTut = new Button(this);
//		restartTut.setText("Beat2");
//		restartTut.setLayoutParams(layoutParams5);

		/* spinners- beats has beat sets, save_list is the context menu */
		beats = new Spinner(getActivity());
		// save_list = new Spinner(this);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayList<String> beatset_arr = new ArrayList<String>();
		beatset_arr.add("Beat 1");
		beatset_arr.add("Beat 2");

		beats.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				Log.i("spinner", "in beats with pos: " + pos);
				beatstate = pos;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		ArrayAdapter<String> beatArrayAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, beatset_arr);
		// ArrayAdapter<String> contextArrayAdapter = new ArrayAdapter<String>(
		// this, android.R.layout.simple_spinner_dropdown_item,
		// context_arr);

		beats.setAdapter(beatArrayAdapter);
		beats.setLayoutParams(layoutParams6);

		// save_list.setAdapter(contextArrayAdapter);
		// save_list.setLayoutParams(layoutParams7);

		// rl.addView(playNote);
		// rl.addView(nextTut);
		// rl.addView(backTut);
		// rl.addView(restartTut);

		// rl.addView(beats);
		// rl.addView(save_list);

		layout.addView(rl, layoutParams);

		popupLayout = new LinearLayout(getActivity());
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		textview = new TextView(getActivity());
		textview.setText("Hi this is a sample text for popup window");
		popUp = new PopupWindow(getActivity());

		final String[] blinkNotes = { "3N", "4N", "5N", "6N", "2#" };
		final int[] playAscending = { 0, 1, 2, 3, 4, 5, 6, 7 };
		final int[] playDescending = { 7, 6, 5, 4, 3, 2, 1, 0 };

		/*
		 * final NotePlay[] playAscending = { 0, 1, 2, 3, 4, 5, 6, 7 }; final
		 * NotePlay[] playDescending = { 7, 6, 5, 4, 3, 2, 1, 0 };
		 */
		//initSongs();

		/**
		 * Black {C#, D#, F#, G#, A#, C#, D#, F#, G#, A#} White {C, D, E, F, G,
		 * A, B, C, D, E, F, G, A, B} piano.tutorialNote([# of note][sharp # or
		 * N]); to stop, setTutUnShade = -1
		 * 
		 * array (play, next, previous restart)
		 */

		// OnClickListener myPlay0 = new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// /* onClickHandler(0); */
		// piano.startRec();
		// }
		// };
		// OnClickListener myPlay1 = new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // onClickHandler(1);
		// piano.playBack(1);
		// Log.i("PlayAround", "OLD PLAY BUTTON PRESSED");
		// }
		// };
		/* using beats 0, 1, 3, 4 */
		OnClickListener myPlay2 = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (beatstate == 0) {
					piano.playBeat(0);
				} else {
					piano.playBeat(3);
				}
				// mctx.openOptionsMenu();
				// onClickHandler(2);
				// piano.playBeat(0);
			}
		};
		OnClickListener myPlay3 = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// onClickHandler(3);
				if (beatstate == 0) {
					piano.playBeat(1);
				} else {
					piano.playBeat(4);
				}
			}
		};

		// playNote.setOnClickListener(myPlay0);
		// nextTut.setOnClickListener(myPlay1);
//		backTut.setOnClickListener(myPlay2);
//		restartTut.setOnClickListener(myPlay3);

		popupLayout.addView(textview, params);
		popUp.setContentView(popupLayout);

		// tableLayout.addView(tableRow);
		// layout.addView(tableLayout);
		/** DONE ***/
		int[] marginVal = new int[4];
		marginVal[0] = (int) (width / 200);
		marginVal[1] = (int) (height / 10);
		int right = 0;
		int left = 0;
		marginVal[2] = right;
		marginVal[3] = left;
		layoutParams.setMargins(0, 0, 0, 0);

		RelativeLayout rlPiano = new RelativeLayout(getActivity());
		RelativeLayout.LayoutParams lpPiano = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		piano = new Piano(getActivity(), marginVal, sp, true);

		/*** SET BUTTON WIDTH AND HEIGHT FROM PIANO KEY WIDTH AND HEIGHTS **/
		// noteOr.setMinimumWidth(piano.getKeyWidths("white")); //change to
		// public static later
		/*** PIANO.FIREDISPLAY(NOTEKEY) ***/
		rlPiano.addView(piano, lpPiano);
		layout.addView(rlPiano);
		Bitmap bmImg = decodeSampledBitmapFromResource(getResources(),
				R.drawable.pianobckgd2, 768, 469);
		BitmapDrawable background = new BitmapDrawable(bmImg);
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			layout.setBackgroundDrawable(background);
		} else {
			layout.setBackground(background);
		}
		//rootview.setsetContentView(layout);<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		// player.SetPiano(piano);
		layout.requestLayout();
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		super.onCreateOptionsMenu(menu);
//		getSupportMenuInflater().inflate(R.menu.activity_playaround, menu);
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activity_playaround, menu);
//		getSupportActionBar().setBackgroundDrawable(
//				new ColorDrawable(Color.rgb(223, 160, 23)));
		mymenu = menu;

        settings = getActivity().getSharedPreferences(Beats_Activity.PREFS, 0);
		/* then it's been set up, otherwise set it up */
        myset = settings.getString(Beats_Activity.beat_key,
                Beats_Activity.DEFAULT_STRING_ARRAY);

		int num = 0;
		for (int i = 0; i < myset.length(); i++) {
			if (myset.charAt(i) == '1')
				num++;
		}
		for (int i = 0; i < num; i++) {
			mymenu.getItem(i + 2).setVisible(true);
		}
		for (int i = num; i < 4; i++) {
			mymenu.getItem(i + 2).setVisible(false);
		}

//		return true;
	}

	public void playBeat(int i) {
		sp.playNote(Beats_Activity.beatArrss[i], 1);
	}

	public int playNumBeat(String str, int i) {
		int found = 0;
		for (int j = 0; j < str.length(); j++) {
			if (str.charAt(j) == '1') {
				if (i == found) {
					playBeat(j);
					return j;
				}
				found++;
			}
		}
		return -1;
	}

	private void setUpHash() {
		note2Key = new HashMap<Integer, TupleStringInt>();
		// white keys
		note2Key.put(KeyEvent.KEYCODE_TAB, new TupleStringInt(Piano.white[0],
				SPPlayer.keyc3));
		note2Key.put(KeyEvent.KEYCODE_Q, new TupleStringInt(Piano.white[1],
				SPPlayer.keyd3));
		note2Key.put(KeyEvent.KEYCODE_W, new TupleStringInt(Piano.white[2],
				SPPlayer.keye3));
		note2Key.put(KeyEvent.KEYCODE_E, new TupleStringInt(Piano.white[3],
				SPPlayer.keyf3));
		note2Key.put(KeyEvent.KEYCODE_R, new TupleStringInt(Piano.white[4],
				SPPlayer.keyg3));
		note2Key.put(KeyEvent.KEYCODE_T, new TupleStringInt(Piano.white[5],
				SPPlayer.keya3));
		note2Key.put(KeyEvent.KEYCODE_Y, new TupleStringInt(Piano.white[6],
				SPPlayer.keyb3));
		note2Key.put(KeyEvent.KEYCODE_U, new TupleStringInt(Piano.white[7],
				SPPlayer.keyc4));
		note2Key.put(KeyEvent.KEYCODE_I, new TupleStringInt(Piano.white[8],
				SPPlayer.keyd4));
		note2Key.put(KeyEvent.KEYCODE_O, new TupleStringInt(Piano.white[9],
				SPPlayer.keye4));
		note2Key.put(KeyEvent.KEYCODE_P, new TupleStringInt(Piano.white[10],
				SPPlayer.keyf4));
		note2Key.put(KeyEvent.KEYCODE_LEFT_BRACKET, new TupleStringInt(
				Piano.white[11], SPPlayer.keyg4));
		note2Key.put(KeyEvent.KEYCODE_RIGHT_BRACKET, new TupleStringInt(
				Piano.white[12], SPPlayer.keya4));
		note2Key.put(KeyEvent.KEYCODE_BACKSLASH, new TupleStringInt(
				Piano.white[13], SPPlayer.keyb4));

		// black keys
		note2Key.put(KeyEvent.KEYCODE_1, new TupleStringInt(Piano.black[0], SPPlayer.keyc3s));
		note2Key.put(KeyEvent.KEYCODE_2, new TupleStringInt(Piano.black[1], SPPlayer.keyd3s));
		note2Key.put(KeyEvent.KEYCODE_4, new TupleStringInt(Piano.black[2], SPPlayer.keyf3s));
		note2Key.put(KeyEvent.KEYCODE_5, new TupleStringInt(Piano.black[3], SPPlayer.keyg3s));
		note2Key.put(KeyEvent.KEYCODE_6, new TupleStringInt(Piano.black[4], SPPlayer.keya3s));
		note2Key.put(KeyEvent.KEYCODE_8, new TupleStringInt(Piano.black[5], SPPlayer.keyc4s));
		note2Key.put(KeyEvent.KEYCODE_9, new TupleStringInt(Piano.black[6], SPPlayer.keyd4s));
		note2Key.put(KeyEvent.KEYCODE_MINUS, new TupleStringInt(Piano.black[7],
				SPPlayer.keyf4s));
		note2Key.put(KeyEvent.KEYCODE_EQUALS, new TupleStringInt(Piano.black[8],
				SPPlayer.keyg4s));
		note2Key.put(KeyEvent.KEYCODE_DEL, new TupleStringInt(Piano.black[9],
				SPPlayer.keya4s));
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("key", "down: " + keyCode);
		TupleStringInt result = null;
		if ((result = note2Key.get(keyCode)) != null) {
			Log.i("key", "valid result: " + result);

			sp.playNote(result.getStr(), 1);
			piano.shadeExclusive(result.getInt());
			return true;
		}
		// play the sound based on the hashmap from keyCode to note

		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.i("key", "press: " + keyCode);
		TupleStringInt result = null;
		if ((result = note2Key.get(keyCode)) != null) {
			Log.i("key", "valid result: " + result);

			piano.unShade(result.getInt());
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int beat = -1;
		switch (item.getItemId()) {

		case R.id.play_icon: {
			piano.playBack(1);
			return true;
		}
		case R.id.record_icon: {
			stop = !stop; // perhaps use to change the icon from record to pause
							// and viceversa?
			if(!stop)
				piano.startRec();
			if (stop)
				Toast.makeText(getActivity(), "Recording Jam", Toast.LENGTH_SHORT)
						.show();
			else
				Toast.makeText(getActivity(), "Stop Recording", Toast.LENGTH_SHORT)
						.show();
			return true;
		}
		case R.id.beat1: {
			beat = playNumBeat(myset, 0);
			if (piano.getRecStart() && beat != -1)
				piano.addToRec(new RecNotes((Calendar.getInstance()
						.getTimeInMillis() - piano.getStartTime()
						.getTimeInMillis()), beat));

			return true;
		}
		case R.id.beat2: {
			beat = playNumBeat(myset, 1);
			if (piano.getRecStart() && beat != -1)
				piano.addToRec(new RecNotes((Calendar.getInstance()
						.getTimeInMillis() - piano.getStartTime()
						.getTimeInMillis()), beat));

			return true;
		}
		case R.id.beat3: {
			beat = playNumBeat(myset, 2);
			if (piano.getRecStart() && beat != -1)
				piano.addToRec(new RecNotes((Calendar.getInstance()
						.getTimeInMillis() - piano.getStartTime()
						.getTimeInMillis()), beat));

			return true;
		}
		case R.id.beat4: {
			beat = playNumBeat(myset, 3);

			if (piano.getRecStart() && beat != -1)
				piano.addToRec(new RecNotes((Calendar.getInstance()
						.getTimeInMillis() - piano.getStartTime()
						.getTimeInMillis()), beat));
			return true;
		}

		case R.id.submenu2: {
			return option_save(rm);
		}

		case R.id.submenu4: {
			return option_list(rm);
		}

		case R.id.save_rec:
			return option_save(rm);
		case R.id.list_rec:
			return option_list(rm);
		}
		return false;
	}

	private boolean option_save(RecManager rm) {
		if (piano.getMyRec().size() == 0) {
			Toast.makeText(getActivity(), "no recording found", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		Log.i("option", "saving rec option");
		Toast.makeText(getActivity(), "saving your recording", Toast.LENGTH_SHORT)
				.show();
		rm.saveRec(piano.getMyRec(), origin);
		return true;
	}

	private boolean option_list(RecManager rm) {

		// call recordListActivity

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
		builder.setTitle("Play Your Recordings");

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
				piano.setMyRec(rn.get(elem));
				Toast.makeText(mctx, "Hit Play!", Toast.LENGTH_SHORT).show();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sp.release();

	}

	public void notePlayed(String note) {
		//
	}

	private Context retContext() {
		return getActivity();
	} // necessary??

	public class pianoAsync extends AsyncTask<Void, Void, Boolean> {
		boolean running = true;

		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = ProgressDialog.show(retContext(), "Loading...",
					"Go Town and Country!", true);
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			Log.e("noteFallTask", "BackExecute");
			try {
				setUpSound();
				setUpAnimation();

				return true;
			} catch (Exception ex) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			// finish?
			// TODO Auto-generated method
			Log.e("noteFallTask", "PostExecute");
			createViewOnlyPiano();
			dialog.dismiss();
			// Log.e("noteFallTask", "PostExecute");
		}

		@Override
		protected void onCancelled() {
			Log.e("noteFallTask", "CANCELED");
		}

//		private void setRunning(boolean val) {
//			running = val;
//		}
	}
}