package com.tncmusicstudio;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
//import android.view.Menu;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.*;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.tncmusicstudio.R;
import com.model.NotePlay;

public class TutorialMSActivity extends SherlockFragment {
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

	/* for note playing */
	private SPPlayer sp;
	private boolean loaded = false;
	private Menu mymenu;

	/* button to guide image falling process */
	Button playNote, backTut, nextTut, restartTut;
	static MenuItem[] mybuts = new MenuItem[4];
	private static boolean unlock = true;
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
	private String tut;
	private int tutLevel;
	private NotePlay[] cmajor1, cmajor2, dmajor1, dmajor2, gmajor1, gmajor2,
			amajor1, amajor2, emajor1, emajor2, fsharp_major1, fsharp_major2,
			littlelamb1, littlelamb2, littlelamb3, twinkle1, twinkle2,
			twinkle3, twinkle4, biebs1, biebs2, biebs3, biebs4, biebs5, dflat1,
			dflat2, aflat1, aflat2, eflat1, eflat2, bflat1, bflat2, fmajor1,
			fmajor2, bmajor1, bmajor2, thriftshop1, thriftshop2, thriftshop3,
			thriftshop4, thriftshop5;
	View rootview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		//super.onCreate(savedInstanceState);
		popUpCount = 0;
		ivArray = new ImageView[2];

		/********
		Bundle bundle = getActivity().getIntent().getExtras();
		tut = bundle.getString(LevelActivity.mSelected);
		Log.i("tutlevel", "tutlvl" + tut);
		if(tut != null) {
			tut = tut.substring(6, tut.indexOf(":"));
			Log.i("tutlevel", "new tutlvl" + tut);
			tutLevel = Integer.parseInt(tut);
			Log.i("tutlevel", "parsed tutlvl" + tut);
		}
		/********/

//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getActivity().setTitle("Piano Tutorials");
		setHasOptionsMenu(true);
		rootview = inflater.inflate(R.layout.simplelayout, container, false);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		try {
			setPiano = new pianoAsync();
			setPiano.execute();

			// else
			// createView();
		} catch (Exception ex) {
			Log.e("animation", "fail");
		}
		return rootview;
	}

	private void setUpSound() {
		// Log.e("SP", "FAILED ON ONCREATE");
		AssetManager am = getActivity().getAssets();
		// activity only stuff
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		sp = new SPPlayer(am, audioManager);
	}

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

		layout = (LinearLayout)rootview.findViewById(R.id.linearlayout1);//new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
//		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
		/* for the button play */
//		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//
//		/* for the button next */
//		LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//		/* for the button restart */
//		LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//
//		/* for the button back */
//		LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(
//				LinearLayout.LayoutParams.MATCH_PARENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//		LinearLayout rl = new LinearLayout(this);
//		rl.setOrientation(LinearLayout.HORIZONTAL);
		// layoutParams3.leftMargin = 100;
		// layoutParams3.topMargin = 100;
		// layoutParams3.setMargins(10, 10, 10, 10);
		/*
		 * For the button to start that tutorial
		 */
//		playNote = new Button(this);
//		playNote.setText("Play!");
//		playNote.setLayoutParams(layoutParams2);

		/*
		 * For the button to play the next tutorial
		 */
//		nextTut = new Button(this);
//		nextTut.setText("Next!");
//		nextTut.setLayoutParams(layoutParams3);

		/*
		 * For the button to play the back tutorial
		 */
//		backTut = new Button(this);
//		backTut.setText("Previous");
//		backTut.setLayoutParams(layoutParams4);

		/*
		 * For the button to play the restart tutorial
		 */
//		restartTut = new Button(this);
//		restartTut.setText("Restart!");
//		restartTut.setLayoutParams(layoutParams5);

		// rl.addView(playNote);
		// rl.addView(nextTut);
		// rl.addView(backTut);
		// rl.addView(restartTut);

//		layout.addView(rl, layoutParams);

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
		initSongs();

		/**
		 * Black {C#, D#, F#, G#, A#, C#, D#, F#, G#, A#} White {C, D, E, F, G,
		 * A, B, C, D, E, F, G, A, B} piano.tutorialNote([# of note][sharp # or
		 * N]); to stop, setTutUnShade = -1
		 * 
		 * array (play, next, previous restart)
		 */

		OnClickListener myPlay0 = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickHandler(0);
			}
		};
		OnClickListener myPlay1 = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickHandler(1);
			}
		};
		OnClickListener myPlay2 = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickHandler(2);
			}
		};
		OnClickListener myPlay3 = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickHandler(3);
				ArrayList<Integer> logshade = piano.retLogShade();
				for (int i = 0; i < logshade.size(); i++) {
					Log.e("logshade", "the notes i: " + i + " at notes: "
							+ logshade.get(i));
				}
			}
		};

//		playNote.setOnClickListener(myPlay0);
//		nextTut.setOnClickListener(myPlay1);
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
		//layoutParams.setMargins(0, 0, 0, 0);

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
				R.drawable.pianobckgd1, 768, 469);
		BitmapDrawable background = new BitmapDrawable(bmImg);
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			layout.setBackgroundDrawable(background);
		} else {
			layout.setBackground(background);
		}
		//setContentView(layout);
		// player.SetPiano(piano);
		layout.requestLayout();
	}

	/**
	 * Black keys range from 0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5 C#
	 * D# F# G# A# C# D# F# G# A#
	 */
	private void initSongs() {
		/************* C-major scale init *******************/
		int size = 8;
		cmajor1 = new NotePlay[size];
		cmajor2 = new NotePlay[size];
		final double[] cNote1 = { 0, 1, 2, 3, 4, 5, 6, 7 };
		final int[] cDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] cNote2 = { 7, 6, 5, 4, 3, 2, 1, 0 };
		final int[] cDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < cmajor1.length; i++)
			cmajor1[i] = new NotePlay(cDur1[i], cNote1[i]);
		for (int i = 0; i < cmajor2.length; i++)
			cmajor2[i] = new NotePlay(cDur2[i], cNote2[i]);

		/************* D-major scale init *******************/
		int d_size = 8;
		dmajor1 = new NotePlay[d_size];
		dmajor2 = new NotePlay[d_size];
		final double[] dNote1 = { 1, 2, 2.5, 4, 5, 6, 5.5, 8 };
		final int[] dDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] dNote2 = { 8, 5.5, 6, 5, 4, 2.5, 2, 1 };
		final int[] dDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < dmajor1.length; i++)
			dmajor1[i] = new NotePlay(dDur1[i], dNote1[i]);
		for (int i = 0; i < dmajor2.length; i++)
			dmajor2[i] = new NotePlay(dDur2[i], dNote2[i]);

		/************* G-major scale init *******************/
		int g_size = 8;
		gmajor1 = new NotePlay[g_size];
		gmajor2 = new NotePlay[g_size];
		final double[] gNote1 = { 4, 5, 6, 7, 8, 9, 7.5, 11 };
		final int[] gDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] gNote2 = { 11, 7.5, 9, 8, 7, 6, 5, 4 };
		final int[] gDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < gmajor1.length; i++)
			gmajor1[i] = new NotePlay(gDur1[i], gNote1[i]);
		for (int i = 0; i < gmajor2.length; i++)
			gmajor2[i] = new NotePlay(gDur2[i], gNote2[i]);

		/************* A-major scale init *******************/
		int a_size = 8;
		amajor1 = new NotePlay[a_size];
		amajor2 = new NotePlay[a_size];
		final double[] aNote1 = { 5, 6, 5.5, 8, 9, 7.5, 8.5, 12 };
		final int[] aDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] aNote2 = { 12, 8.5, 7.5, 9, 8, 5.5, 6, 5 };
		final int[] aDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < amajor1.length; i++)
			amajor1[i] = new NotePlay(aDur1[i], aNote1[i]);
		for (int i = 0; i < amajor2.length; i++)
			amajor2[i] = new NotePlay(aDur2[i], aNote2[i]);

		/************* E-major scale init *******************/
		int e_size = 8;
		emajor1 = new NotePlay[e_size];
		emajor2 = new NotePlay[e_size];
		final double[] eNote1 = { 2, 2.5, 3.5, 5, 6, 5.5, 6.5, 9 };
		final int[] eDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] eNote2 = { 9, 6.5, 5.5, 6, 5, 3.5, 2.5, 2 };
		final int[] eDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < emajor1.length; i++)
			emajor1[i] = new NotePlay(eDur1[i], eNote1[i]);
		for (int i = 0; i < emajor2.length; i++)
			emajor2[i] = new NotePlay(eDur2[i], eNote2[i]);

		/************* F#-major scale init *******************/
		int fsm_size = 8;
		fsharp_major1 = new NotePlay[fsm_size];
		fsharp_major2 = new NotePlay[fsm_size];
		final double[] fsmNote1 = { 2.5, 3.5, 4.5, 6, 5.5, 6.5, 10, 7.5 };
		final int[] fsmDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] fsmNote2 = { 7.5, 10, 6.5, 5.5, 6, 4.5, 3.5, 2.5 };
		final int[] fsmDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < fsharp_major1.length; i++)
			fsharp_major1[i] = new NotePlay(fsmDur1[i], fsmNote1[i]);
		for (int i = 0; i < fsharp_major2.length; i++)
			fsharp_major2[i] = new NotePlay(fsmDur2[i], fsmNote2[i]);

		/************* D Flat-major scale init *******************/
		int dfm_size = 8;
		dflat1 = new NotePlay[dfm_size];
		dflat2 = new NotePlay[dfm_size];
		final double[] dfmNote1 = { 0.5, 1.5, 3, 2.5, 3.5, 4.5, 7, 5.5 };
		final int[] dfmDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] dfmNote2 = { 5.5, 7, 4.5, 3.5, 2.5, 3, 1.5, 0.5 };
		final int[] dfmDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < dflat1.length; i++)
			dflat1[i] = new NotePlay(dfmDur1[i], dfmNote1[i]);
		for (int i = 0; i < dflat2.length; i++)
			dflat2[i] = new NotePlay(dfmDur2[i], dfmNote2[i]);

		/************* A Flat-major scale init *******************/
		int afm_size = 8;
		aflat1 = new NotePlay[afm_size];
		aflat2 = new NotePlay[afm_size];
		final double[] afmNote1 = { 3.5, 4.5, 7, 5.5, 6.5, 10, 11, 8.5 };
		final int[] afmDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] afmNote2 = { 8.5, 11, 10, 6.5, 5.5, 7, 4.5, 3.5 };
		final int[] afmDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < aflat1.length; i++)
			aflat1[i] = new NotePlay(afmDur1[i], afmNote1[i]);
		for (int i = 0; i < aflat2.length; i++)
			aflat2[i] = new NotePlay(afmDur2[i], afmNote2[i]);

		/************* E Flat-major scale init *******************/
		int efm_size = 8;
		eflat1 = new NotePlay[efm_size];
		eflat2 = new NotePlay[efm_size];
		final double[] efmNote1 = { 1.5, 3, 4, 3.5, 4.5, 7, 8, 6.5 };
		final int[] efmDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] efmNote2 = { 6.5, 8, 7, 4.5, 3.5, 4, 3, 1.5 };
		final int[] efmDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < eflat1.length; i++)
			eflat1[i] = new NotePlay(efmDur1[i], efmNote1[i]);
		for (int i = 0; i < eflat2.length; i++)
			eflat2[i] = new NotePlay(efmDur2[i], efmNote2[i]);

		/************* B Flat-major scale init *******************/
		int bfm_size = 8;
		bflat1 = new NotePlay[bfm_size];
		bflat2 = new NotePlay[bfm_size];
		final double[] bfmNote1 = { 4.5, 7, 8, 6.5, 10, 11, 12, 9.5 };
		final int[] bfmDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] bfmNote2 = { 9.5, 12, 11, 10, 6.5, 8, 7, 4.5 };
		final int[] bfmDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < bflat1.length; i++)
			bflat1[i] = new NotePlay(bfmDur1[i], bfmNote1[i]);
		for (int i = 0; i < bflat2.length; i++)
			bflat2[i] = new NotePlay(bfmDur2[i], bfmNote2[i]);

		/************* F-major scale init *******************/
		int f_size = 8;
		fmajor1 = new NotePlay[f_size];
		fmajor2 = new NotePlay[f_size];
		final double[] fNote1 = { 3, 4, 5, 4.5, 7, 8, 9, 10 };
		final int[] fDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] fNote2 = { 10, 9, 8, 7, 4.5, 5, 4, 3 };
		final int[] fDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < fmajor1.length; i++)
			fmajor1[i] = new NotePlay(fDur1[i], fNote1[i]);
		for (int i = 0; i < fmajor2.length; i++)
			fmajor2[i] = new NotePlay(fDur2[i], fNote2[i]);

		/************* B-major scale init *******************/
		int b_size = 8;
		bmajor1 = new NotePlay[b_size];
		bmajor2 = new NotePlay[b_size];
		final double[] bNote1 = { 6, 5.5, 6.5, 9, 7.5, 8.5, 9.5, 13 };
		final int[] bDur1 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		final double[] bNote2 = { 13, 9.5, 8.5, 7.5, 9, 6.5, 5.5, 6 };
		final int[] bDur2 = { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };

		for (int i = 0; i < bmajor1.length; i++)
			bmajor1[i] = new NotePlay(bDur1[i], bNote1[i]);
		for (int i = 0; i < bmajor2.length; i++)
			bmajor2[i] = new NotePlay(bDur2[i], bNote2[i]);

		/********** Mary had a little lamb **************/

		final int[] llNote1 = { 6, 5, 4, 5, 6, 6, 6, 5, 5, 5, 6, 8, 8 };
		final int[] llDur1 = { 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 2 };

		final int[] llNote2 = { 6, 5, 4, 5, 6, 6, 6 };
		final int[] llDur2 = { 1, 1, 1, 1, 1, 1, 2 };

		final int[] llNote3 = { 5, 5, 6, 5, 4 };
		final int[] llDur3 = { 1, 1, 1, 1, 4 };

		int lambsize1 = llNote1.length;
		int lambsize2 = llNote2.length;
		int lambsize3 = llNote3.length;
		littlelamb1 = new NotePlay[lambsize1];
		littlelamb2 = new NotePlay[lambsize2];
		littlelamb3 = new NotePlay[lambsize3];

		for (int i = 0; i < littlelamb1.length; i++)
			littlelamb1[i] = new NotePlay(llDur1[i], llNote1[i]);
		for (int i = 0; i < littlelamb2.length; i++)
			littlelamb2[i] = new NotePlay(llDur2[i], llNote2[i]);
		for (int i = 0; i < littlelamb3.length; i++)
			littlelamb3[i] = new NotePlay(llDur3[i], llNote3[i]);

		/*********** Twinkle Twinkle Little Stars **************/

		final int[] ttNote1 = { 0, 0, 4, 4, 5, 5, 4 };
		final int[] ttDur1 = { 1, 1, 1, 1, 1, 1, 2 };

		final int[] ttNote2 = { 3, 3, 2, 2, 1, 1, 0 };
		final int[] ttDur2 = { 1, 1, 1, 1, 1, 1, 2 };

		final int[] ttNote3 = { 4, 4, 3, 3, 2, 2, 1, 4, 4, 3, 3, 2, 2, 1 };
		final int[] ttDur3 = { 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2 };

		final int[] ttNote4 = { 3, 3, 2, 2, 1, 4, 0 };
		final int[] ttDur4 = { 1, 1, 1, 1, 1, 1, 2 };

		int ttsize1 = ttNote1.length;
		int ttsize2 = ttNote2.length;
		int ttsize3 = ttNote3.length;
		int ttsize4 = ttNote4.length;
		twinkle1 = new NotePlay[ttsize1];
		twinkle2 = new NotePlay[ttsize2];
		twinkle3 = new NotePlay[ttsize3];
		twinkle4 = new NotePlay[ttsize4];

		for (int i = 0; i < ttsize1; i++)
			twinkle1[i] = new NotePlay(ttDur1[i], ttNote1[i]);
		for (int i = 0; i < ttsize2; i++)
			twinkle2[i] = new NotePlay(ttDur2[i], ttNote2[i]);
		for (int i = 0; i < ttsize3; i++)
			twinkle3[i] = new NotePlay(ttDur3[i], ttNote3[i]);
		for (int i = 0; i < ttsize4; i++)
			twinkle4[i] = new NotePlay(ttDur4[i], ttNote4[i]);

		/************** Justin Biebs *************/

		final int[] jbNote1 = { 9, 9, 9, 9, 8, 8, 7, 8, 8, 8, 7, 7, 9, 9, 9, 9,
				8, 8, 7, 8, 8, 8, 7, 7 };
		final int[] jbDur1 = { 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1,
				1, 1, 1, 2, 2, 2, 1, 2 };

		final int[] jbNote2 = { 9, 9, 9, 9, 8, 8, 7, 8, 8, 8, 7, 7, 9, 9, 9, 9,
				8, 9, 9, 9, 9, 8 };
		final int[] jbDur2 = { 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 2, 1, 1, 1, 1,
				2, 1, 1, 1, 1, 2 };

		final int[] jbNote3 = { 7, 7, 7, 11, 11, 11, 10, 9, 10, 9 };
		final int[] jbDur3 = { 1, 1, 1, 2, 2, 2, 1, 1, 1, 4 };

		final int[] jbNote4 = { 7, 7, 11, 10, 9, 8, 7 };
		final int[] jbDur4 = { 1, 1, 2, 2, 2, 2, 6 };

		final int[] jbNote5 = { 7, 7, 11, 10, 9, 8, 7, 8, 9, 10, 9 };
		final int[] jbDur5 = { 1, 1, 2, 2, 2, 2, 3, 1, 1, 1, 4 };

		int jbsize1 = jbNote1.length;
		int jbsize2 = jbNote2.length;
		int jbsize3 = jbNote3.length;
		int jbsize4 = jbNote4.length;
		int jbsize5 = jbNote5.length;
		biebs1 = new NotePlay[jbsize1];
		biebs2 = new NotePlay[jbsize2];
		biebs3 = new NotePlay[jbsize3];
		biebs4 = new NotePlay[jbsize4];
		biebs5 = new NotePlay[jbsize5];

		for (int i = 0; i < jbsize1; i++)
			biebs1[i] = new NotePlay(jbDur1[i], jbNote1[i]);
		for (int i = 0; i < jbsize2; i++)
			biebs2[i] = new NotePlay(jbDur2[i], jbNote2[i]);
		for (int i = 0; i < jbsize3; i++)
			biebs3[i] = new NotePlay(jbDur3[i], jbNote3[i]);
		for (int i = 0; i < jbsize4; i++)
			biebs4[i] = new NotePlay(jbDur4[i], jbNote4[i]);
		for (int i = 0; i < jbsize5; i++)
			biebs5[i] = new NotePlay(jbDur5[i], jbNote5[i]);

		/*********** Thrift Shop **************/
		final double[] tsNote1 = { 3.5, 3.5, 3.5, 1.5, 0.5, 3.5, 6.5, 6, 3.5 };
		final int[] tsDur1 = { 2, 1, 1, 2, 2, 3, 1, 2, 2 };
		final double[] tsNote2 = { 3.5, 6.5, 6.5, 5.5, 6, 3.5 };
		final int[] tsDur2 = { 2, 1, 1, 2, 2, 4 };
		final double[] tsNote3 = { 3.5, 6, 6.5, 6.5, 6.5, 6.5, 5.5, 6, 6.5, 5.5 };
		final int[] tsDur3 = { 2, 2, 1, 1, 1, 1, 2, 2, 2, 6 };
		final double[] tsNote4 = { 6.5, 6.5, 5.5, 6, 6.5, 6.5, 6.5, 6.5, 5.5, 6 };
		final int[] tsDur4 = { 2, 2, 2, 2, 1, 1, 1, 1, 2, 2 };
		final double[] tsNote5 = { 6.5, 6.5, 5.5, 6, 3.5, 3.5 };
		final int[] tsDur5 = { 2, 2, 2, 2, 2, 6 };

		int tssize1 = tsNote1.length;
		int tssize2 = tsNote2.length;
		int tssize3 = tsNote3.length;
		int tssize4 = tsNote4.length;
		int tssize5 = tsNote5.length;
		thriftshop1 = new NotePlay[tssize1];
		thriftshop2 = new NotePlay[tssize2];
		thriftshop3 = new NotePlay[tssize3];
		thriftshop4 = new NotePlay[tssize4];
		thriftshop5 = new NotePlay[tssize5];
		for (int i = 0; i < tssize1; i++)
			thriftshop1[i] = new NotePlay(tsDur1[i], tsNote1[i]);
		for (int i = 0; i < tssize2; i++)
			thriftshop2[i] = new NotePlay(tsDur2[i], tsNote2[i]);
		for (int i = 0; i < tssize3; i++)
			thriftshop3[i] = new NotePlay(tsDur3[i], tsNote3[i]);
		for (int i = 0; i < tssize4; i++)
			thriftshop4[i] = new NotePlay(tsDur4[i], tsNote4[i]);
		for (int i = 0; i < tssize5; i++)
			thriftshop5[i] = new NotePlay(tsDur5[i], tsNote5[i]);

	}

	protected void onClickHandler(int button) {
		// TODO Auto-generated method stub
		unlock = false;
		System.err.println("within onclickhandler: " + button);
		if (button == 0) {
			Log.i("asdf", "playnote");
			initSongs();
			// No increment
		} else if (button == 1) {
			Log.i("asdf", "nextTut");
			// initSongs();
			numClicks++;
		} else if (button == 2) {
			Log.i("asdf", "backTut");
			initSongs();
			numClicks--;
		} else if (button == 3) {
			Log.i("asdf", "restartTut");
			initSongs();
			// initnotes
			numClicks = 0;
		} else {
			Log.e("OOB", "button out of bounds");
			numClicks = 0;
		}

		// Level 1 - C Major Scale
		if (tutLevel == 1) {
			switch (numClicks) {
			case 0:
				piano.playSong(cmajor1, 150);
				break;
			case 1:
				piano.playSong(cmajor2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 5 - D Major Scale
		if (tutLevel == 5) {
			switch (numClicks) {
			case 0:
				piano.playSong(dmajor1, 150);
				break;
			case 1:
				piano.playSong(dmajor2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 6 - G Major Scale
		if (tutLevel == 6) {
			switch (numClicks) {
			case 0:
				piano.playSong(gmajor1, 150);
				break;
			case 1:
				piano.playSong(gmajor2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 7 - A Major Scale
		if (tutLevel == 7) {
			switch (numClicks) {
			case 0:
				piano.playSong(amajor1, 150);
				break;
			case 1:
				piano.playSong(amajor2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 8 - E Major Scale
		if (tutLevel == 8) {
			switch (numClicks) {
			case 0:
				piano.playSong(emajor1, 150);
				break;
			case 1:
				piano.playSong(emajor2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 9 - F# Major Scale
		if (tutLevel == 9) {
			switch (numClicks) {
			case 0:
				piano.playSong(fsharp_major1, 150);
				break;
			case 1:
				piano.playSong(fsharp_major2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 10 - D Flat Major Scale
		if (tutLevel == 10) {
			switch (numClicks) {
			case 0:
				piano.playSong(dflat1, 150);
				break;
			case 1:
				piano.playSong(dflat2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 11 - A Flat Major Scale
		if (tutLevel == 11) {
			switch (numClicks) {
			case 0:
				piano.playSong(aflat1, 150);
				break;
			case 1:
				piano.playSong(aflat2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 12 - E Flat Major Scale
		if (tutLevel == 12) {
			switch (numClicks) {
			case 0:
				piano.playSong(eflat1, 150);
				break;
			case 1:
				piano.playSong(eflat2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 13 - B Flat Major Scale
		if (tutLevel == 13) {
			switch (numClicks) {
			case 0:
				piano.playSong(bflat1, 150);
				break;
			case 1:
				piano.playSong(bflat2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 14 - F-Major Scale
		if (tutLevel == 14) {
			switch (numClicks) {
			case 0:
				piano.playSong(fmajor1, 150);
				break;
			case 1:
				piano.playSong(fmajor2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 15 - B-Major Scale
		if (tutLevel == 15) {
			switch (numClicks) {
			case 0:
				piano.playSong(bmajor1, 150);
				break;
			case 1:
				piano.playSong(bmajor2, 150);
				break;
			case 2:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 2 - Twinkle Twinkle
		if (tutLevel == 2) {
			switch (numClicks) {
			case 0:
				piano.playSong(twinkle1, 150);
				break;
			case 1:
				piano.playSong(twinkle2, 150);
				break;
			case 2:
				piano.playSong(twinkle3, 150);
				break;
			case 3:
				piano.playSong(twinkle1, 150);
				break;
			case 4:
				piano.playSong(twinkle4, 150);
				break;
			case 5:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 3 - Mary had a Little Lamb
		if (tutLevel == 3) {
			switch (numClicks) {
			case 0:
				piano.playSong(littlelamb1, 125);
				break;
			case 1:
				piano.playSong(littlelamb2, 125);
				break;
			case 2:
				piano.playSong(littlelamb3, 125);
				break;
			case 3:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 4 - Justin Biebs
		if (tutLevel == 4) {
			switch (numClicks) {
			case 0:
				piano.playSong(biebs1, 130);
				break;
			case 1:
				piano.playSong(biebs2, 130);
				break;
			case 2:
				piano.playSong(biebs3, 130);
				break;
			case 3:
				piano.playSong(biebs4, 130);
				break;
			case 4:
				piano.playSong(biebs5, 130);
				break;
			case 5:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}

		// Level 16 - Thrift Shop
		int thriftspeed = 100;
		if (tutLevel == 16) {
			switch (numClicks) {
			case 0:
				piano.playSong(thriftshop1, thriftspeed);
				break;
			case 1:
				piano.playSong(thriftshop2, thriftspeed);
				break;
			case 2:
				piano.playSong(thriftshop3, thriftspeed);
				break;
			case 3:
				piano.playSong(thriftshop4, thriftspeed);
				break;
			case 4:
				piano.playSong(thriftshop5, thriftspeed);
				break;
			case 5:
				numClicks = 0;
				break;
			default:
				piano.toggleShade();
			}
		}
		// unlockAll();
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

	/** Show an error dialog with the given message */
	private void showDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(message);
		builder.setTitle("Piano Tutorial");
		builder.setCancelable(false);
		builder.setPositiveButton("Finished",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});
		builder.setNegativeButton("Restart",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activity_tutorial_ms, menu);
//		getSupportActionBar().setBackgroundDrawable(
//				new ColorDrawable(Color.rgb(223, 160, 23)));
		mymenu = menu;

		for (int i = 0; i < mybuts.length; i++) {
			System.err.println("TITLE: " + i + " is: "
					+ mymenu.getItem(i).getTitle());
			mybuts[i] = mymenu.getItem(i);
		}
	}

	private void lockOthers(int button) {
		for (int i = 0; i < mybuts.length; i++) {
			// if (i != button)
			mybuts[i].setEnabled(false);
		}
		System.err.println("set all to false");
	}

	public static void unlockAll() {
		for (int i = 0; i < mybuts.length; i++) {
			mybuts[i].setEnabled(true);
		}
		System.err.println("set all to true");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (!unlock)
			return false;
		switch (item.getItemId()) {

		case R.id.play_icon: {
			Log.i("asdf", "playnote");
			onClickHandler(0);

			System.err.println("In here");
			break;
		}

		case R.id.next_icon: {
			Log.i("asdf", "nextTut");

			onClickHandler(1);
			// lockOthers(1);
			break;
			// numClicks++;
		}

		case R.id.replay_icon: {
			Log.i("asdf", "backTut");
			onClickHandler(2);
			// lockOthers(2);

			break;
			// numClicks--;
		}
		case R.id.previous_icon: {
			Log.i("asdf", "restartTut");
			// initSongs();
			onClickHandler(3);
			// lockOthers(3);

			break;
			// numClicks = 0;
		}
		case R.id.menu_settings: {
//			Intent i = new Intent(this, SettingsActivity.class);
//			startActivity(i);
			break;
		}

		default: {

			Log.e("OOB", "button out of boundsssss");
			numClicks = 0;
		}
			return false;

		}
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sp.release();
		// try {
		// pthread.join(500);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// Log.e("pthread", "unable to join");
		// }
		// pthread.setRunning(false);

	}

	public void notePlayed(String note) {
		//
	}

	private Context retContext() {
		return getActivity();
	}

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
				// setUpAnimation();

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

		private void setRunning(boolean val) {
			running = val;
		}
	}

	public static void setUnlock(boolean b) {
		// TODO Auto-generated method stub
		unlock = true;
	}
}