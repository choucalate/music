package com.midisheetmusic;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import com.model.NotePlay;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

public class TutorialMSActivity extends Activity {
	String[] values = new String[] { "Level 0: Keyboard Note Training! ",
			"Level 1: Major Scales", "Level 2: Learning Chords",
			"Level 3: Actual Songs" };
	public static final String MidiDataID = "MidiDataID";
	public static final String MidiTitleID = "MidiTitleID";
	public static final int settingsRequestCode = 1;

	private MidiPlayer player; /* The play/stop/rewind toolbar */
	private Piano piano; /* The piano at the top */
	private SheetMusic sheet; /* The sheet music */
	private LinearLayout layout; /* THe layout */
	private MidiFile midifile; /* The midi file to play */
	private MidiOptions options; /* The options for sheet music and sound */
	private long midiCRC; /* CRC of the midi bytes */
	private LevelActivity tutorialActivityLevel;

	private LevelActivity mTuts;/* to get the string */
	private String pos;

	/* for note playing */
	private SPPlayer sp;
	private boolean loaded = false;

	/* button to guide image falling process */
	Button noteFall;
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
	Animator animation1, animation2;
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

	int numClicks; // count numclicks to the buttons

	static int callBack = 0;

	RelativeLayout rl;
	AnimatorSet set1, set2, set3, set4;

	private NotePlay[] np, littlelamb, twinkle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		popUpCount = 0;
		ivArray = new ImageView[2];

		/********/
		Bundle bundle = getIntent().getExtras();
		String tutLevel = bundle.getString(LevelActivity.mSelected);
		/********/

		ClefSymbol.LoadImages(this);
		TimeSigSymbol.LoadImages(this);
		MidiPlayer.LoadImages(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setUpSound();
		setUpAnimation();
		try {
			// if (tutLevel.equals(values[0]))
			createViewOnlyPiano();
			// else
			// createView();
		} catch (Exception ex) {
			Log.e("animation", "fail");
		}
	}

	private void setUpAnimation() {
		anim1 = AnimationUtils.loadAnimation(this, R.animator.falling);
		anim1.setFillAfter(false);
		anim2 = AnimationUtils.loadAnimation(this, R.animator.falling);
		anim2.setFillAfter(false);
		anim3 = AnimationUtils.loadAnimation(this, R.animator.falling);
		anim3.setFillAfter(false);
		anim4 = AnimationUtils.loadAnimation(this, R.animator.falling);
		anim4.setFillAfter(false);
		anim5 = AnimationUtils.loadAnimation(this, R.animator.falling);
		anim5.setFillAfter(false);

	}

	private void setUpSound() {
		// Log.e("SP", "FAILED ON ONCREATE");
		AssetManager am = getAssets();
		// activity only stuff
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		sp = new SPPlayer(am, audioManager);
	}

	class NBNodes {
		String note;
		int duration;

		NBNodes() {

		}
	}

	private void animatev2() {
		class TranslateAnim extends TranslateAnimation {

			public TranslateAnim(float fromXDelta, float toXDelta,
					float fromYDelta, float toYDelta) {
				super(fromXDelta, toXDelta, fromYDelta, toYDelta);
				// TODO Auto-generated constructor stub
			}

			private long mElapsedAtPause = 0;
			private boolean mPaused = false;

			@Override
			public boolean getTransformation(long currentTime,
					Transformation outTransformation) {
				if (mPaused && mElapsedAtPause == 0) {
					mElapsedAtPause = currentTime - getStartTime();
				}
				if (mPaused)
					setStartTime(currentTime - mElapsedAtPause);
				return super.getTransformation(currentTime, outTransformation);
			}

			public void pause() {
				mElapsedAtPause = 0;
				mPaused = true;
			}

			public void resume() {
				mPaused = false;
			}
		}
		try {
			AnimationSet set = new AnimationSet(true);
			set.setInterpolator(new LinearInterpolator());
			final AnimationSet set2 = new AnimationSet(true);
			set.setInterpolator(new LinearInterpolator());
			AnimationSet set3 = new AnimationSet(true);
			set.setInterpolator(new LinearInterpolator());
			AnimationSet set4 = new AnimationSet(true);
			set.setInterpolator(new LinearInterpolator());

			TranslateAnim ta1 = new TranslateAnim(0, 0, -140, 300);
			ta1.setDuration(2000);
			TranslateAnim ta2 = new TranslateAnim(0, 0, -140, 300);
			ta2.setDuration(2000);
			ta2.setStartOffset(500);
			TranslateAnim ta3 = new TranslateAnim(0, 0, -140, 300);
			ta3.setDuration(2000);
			ta3.setStartOffset(500);
			TranslateAnim ta4 = new TranslateAnim(0, 0, -140, 300);
			ta4.setDuration(2000);
			ta4.setStartOffset(500);

			set.addAnimation(ta1);
			set2.addAnimation(ta2);
			set3.addAnimation(ta3);
			set4.addAnimation(ta4);
			// set.setFillAfter(false);
			// set2.setFillAfter(false);
			// set3.setFillAfter(false);
			// set4.setFillAfter(false);
			set.setRepeatCount(Animation.INFINITE);
			set2.setRepeatCount(Animation.INFINITE);
			set3.setRepeatCount(Animation.INFINITE);
			set4.setRepeatCount(Animation.INFINITE);
			set.setRepeatMode(Animation.RESTART);
			set2.setRepeatMode(Animation.RESTART);
			set3.setRepeatMode(Animation.RESTART);
			set4.setRepeatMode(Animation.RESTART);

			set.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					set2.startNow();
					Log.i("animation", "END");
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					Log.i("animation", "repeat");
				}

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					Log.i("animation", "start");
				}

			});
			noteC1A.startAnimation(set);
			noteD1A.startAnimation(set2);
			noteE1A.startAnimation(set3);
			noteF1A.startAnimation(set4);
			ObjectAnimator oa = ObjectAnimator.ofFloat(noteG1A, "translationY",
					0, 90);
			oa.start();

		} catch (Exception ex) {
			Log.e("error", ex.toString());
		}
		// AnimatorSet s= new AnimatorSet();
		// ObjectAnimator a = ObjectAnimator.ofFloat(noteC1A, "scaleX", 0f, 1f);
	}

	private void animatev3() {
		set1 = new AnimatorSet();
		set1.setInterpolator(new LinearInterpolator());
		set2 = new AnimatorSet();
		set2.setInterpolator(new LinearInterpolator());
		set3 = new AnimatorSet();
		set3.setInterpolator(new LinearInterpolator());
		set4 = new AnimatorSet();
		set4.setInterpolator(new LinearInterpolator());
		// set interpolator?
		ValueAnimator ta1 = ObjectAnimator.ofFloat(noteB1A, "translationY",
				-140, 300);
		ta1.setDuration(2500);
		// ta.setRepeatCount(ValueAnimator.INFINITE);
		// ta1.setRepeatMode(ValueAnimator.RESTART);

		ValueAnimator ta2 = ObjectAnimator.ofFloat(noteA1A, "translationY",
				-140, 300);
		ta2.setDuration(2500);
		// ta.setRepeatCount(ValueAnimator.INFINITE);
		// ta2.setRepeatMode(ValueAnimator.RESTART);

		final ValueAnimator ta3 = ObjectAnimator.ofFloat(noteG1A,
				"translationY", -140, 300);
		ta3.setDuration(2500);

		// ta.setRepeatCount(ValueAnimator.INFINITE);
		// ta3.setRepeatMode(ValueAnimator.RESTART);
		set1.play(ta1);
		set1.play(ta2).after(500);
		set1.play(ta3).after(1000);

		set1.start();

		set2.play(ta2);
		set2.play(ta1).after(500);
		set1.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				Log.i("ta3", "delay: " + ta3.getStartDelay());
				set2.setStartDelay(ta3.getStartDelay());
				set2.start();
			}

			@Override
			public void onAnimationEnd(Animator animation) {

			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

		});
	}

	public void animate() {
		/**
		 * Data structure encoding into SQL Lite DB param 1- tempo param 2-
		 * ArrayList of NBNodes NBNodes contain 1 - full note, 2 - half 4-
		 * quarter 8- 8th ... etc. and the note for each
		 * 
		 * Ex: Mary had a little lamb
		 * [B/4][A/4][G/4][A/4][B/4][B/4][B/2][A/4][A/
		 * 4][A/2][B/4][D/4][D/2][B/4][A/4][G/4][A/4][B/4][B/4]
		 * [B/4][B/4][A/4][A/4][B/4][A/4][G/1] Tempo scales the startoffset
		 */

		ImageView[] noteArr = { noteB1A, noteA1A, noteG1A, noteA1A, noteB1B };
		anim1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// anim1.reset();
				// anim1.setStartOffset(1000);
				// anim1.startNow();
				// anim2.setStartOffset(1000);
				// noteBl.startAnimation(anim2);
				Log.e("animlist", "end1" + anim2.getStartOffset());
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				Log.e("animlist", "repeat1");
			}

			@Override
			public void onAnimationStart(Animation animation) {
				Log.e("animlist", "start1");
				anim2.setStartOffset(200);
				noteA1A.startAnimation(anim2);
			}
		});
		anim2.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				// anim1.reset();
				// anim1.setStartOffset(1000);
				// anim1.startNow();
				// anim2.setStartOffset(1000);
				// noteBl.startAnimation(anim2);
				Log.e("animlist", "end2" + anim2.getStartOffset());
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				Log.e("animlist", "repeat1");
			}

			@Override
			public void onAnimationStart(Animation animation) {
				Log.e("animlist", "start2");
				if (!anim1.hasEnded())
					Log.e("animlist", "ANIM1 HAS NOTENDED YET");
				anim3.setStartOffset(500);
				noteG1A.startAnimation(anim3);
			}
		});
		anim3.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				anim1.setStartOffset(200);
				noteA1A.startAnimation(anim1);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// anim1.setStartOffset(2000);
				// noteC1B.startAnimation(anim1);

			}

		});
		// AnimatorSet s = new AnimatorSet();
		// s.play(anim1).before(anim2);
		// s.play(anim2).before(anim3);
		// noteB1A.startAnimation(anim1);
		// anim1.setStartOffset(1000);
		// aSet.addAnimation(anim1);
		// anim1.setRepeatCount(Animation.INFINITE);
		// anim1.setRepeatMode(Animation.RESTART);
		// anim2.setRepeatCount(Animation.INFINITE);
		// anim2.setRepeatMode(Animation.RESTART);

		// noteBl.startAnimation(anim1);
		// noteOr2.startAnimation(anim2);
		// noteBl2.startAnimation(anim2);
		// noteOr3.startAnimation(anim3);
		// noteBl3.startAnimation(anim3);

	}

	/* Create the MidiPlayer and Piano views */
	private void createView() {

		Display display = getWindowManager().getDefaultDisplay();
		// Point size = new Point();
		int width = display.getWidth();
		int height = display.getHeight();
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
		/* for the button */
		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		rl = new RelativeLayout(this);

		/* note 1 */
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		/* note 2 */
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		/* note 3 */
		RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		/* note 4 */
		RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		/* note 5 */
		RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		/* note 6 */
		RelativeLayout.LayoutParams lp6 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		/* note 6 */
		RelativeLayout.LayoutParams lp7 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		noteFall = new Button(this);
		noteFall.setText("fall!");
		noteFall.setLayoutParams(layoutParams2);
		rl.addView(noteFall);

		noteC1A = new ImageView(this);
		noteD1A = new ImageView(this);
		noteE1A = new ImageView(this);
		noteF1A = new ImageView(this);
		noteG1A = new ImageView(this);
		noteA1A = new ImageView(this);
		noteB1A = new ImageView(this);
		noteC1B = new ImageView(this);
		noteD1B = new ImageView(this);
		noteE1B = new ImageView(this);
		noteF1B = new ImageView(this);
		noteG1B = new ImageView(this);
		noteA1B = new ImageView(this);
		noteB1B = new ImageView(this);
		noteC1A.setImageResource(R.drawable.orange_note);
		noteD1A.setImageResource(R.drawable.blue_note);
		noteE1A.setImageResource(R.drawable.orange_note);
		noteF1A.setImageResource(R.drawable.blue_note);
		noteG1A.setImageResource(R.drawable.orange_note);
		noteA1A.setImageResource(R.drawable.blue_note);
		noteB1A.setImageResource(R.drawable.orange_note);
		noteC1B.setImageResource(R.drawable.orange_note);
		noteD1B.setImageResource(R.drawable.blue_note);
		noteE1B.setImageResource(R.drawable.orange_note);
		noteF1B.setImageResource(R.drawable.blue_note);
		noteG1B.setImageResource(R.drawable.orange_note);
		noteA1B.setImageResource(R.drawable.blue_note);
		noteB1B.setImageResource(R.drawable.orange_note);

		// noteOr.setVisibility(View.INVISIBLE);
		// noteOr2.setVisibility(View.INVISIBLE);

		/**
		 * Margins- 35- C3 115- D3 195 275 355 435 515
		 */
		lp.setMargins(35, 0, 0, 300);
		lp2.setMargins(110, 0, 0, 300);
		lp3.setMargins(185, 0, 0, 300);
		lp4.setMargins(225, 0, 0, 300);
		lp5.setMargins(305, 0, 0, 300);
		lp6.setMargins(385, 0, 0, 300);
		lp7.setMargins(430, 0, 0, 300);
		rl.addView(noteC1A, lp);
		rl.addView(noteD1A, lp2);
		rl.addView(noteE1A, lp3);
		rl.addView(noteF1A, lp4);
		rl.addView(noteG1A, lp5);
		rl.addView(noteA1A, lp6);
		rl.addView(noteB1A, lp7);
		rl.addView(noteC1B, lp);
		rl.addView(noteD1B, lp2);
		rl.addView(noteE1B, lp3);
		rl.addView(noteF1B, lp4);
		rl.addView(noteG1B, lp5);
		rl.addView(noteA1B, lp6);
		rl.addView(noteB1B, lp7);

		// noteOr.setVisibility(View.INVISIBLE);
		// noteOr2.setVisibility(View.INVISIBLE);

		layout.addView(rl, layoutParams);

		popupLayout = new LinearLayout(this);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		textview = new TextView(this);
		textview.setText("Hi this is a sample text for popup window");
		popUp = new PopupWindow(this);

		/**
		 * Black {C#, D#, F#, G#, A#, C#, D#, F#, G#, A#} White {C, D, E, F, G,
		 * A, B, C, D, E, F, G, A, B} piano.tutorialNote([# of note][sharp # or
		 * N]); to stop, setTutUnShade = -1
		 * 
		 */
		noteFall.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				numClicks++;

				// if(numClicks%2 == 0 && numClicks != 0)
				// {
				// Log.i("numClicks", "num: " + numClicks);
				// piano.toggleShade();
				// }
				// piano.tutorialNote("3#");
				animatev3();
				// if (click) {
				// Log.i("Click", "BUTTON IS CLICKED");
				// popUp.showAtLocation(layout, Gravity.LEFT, 8, 4);
				// popUp.update(50, 50, 300, 100);
				// click = false;
				// } else {
				// popUp.dismiss();
				// click = true;
				// }
			}
		});
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

		RelativeLayout rlPiano = new RelativeLayout(this);
		RelativeLayout.LayoutParams lpPiano = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		piano = new Piano(this, marginVal, sp, false);
		/*** SET BUTTON WIDTH AND HEIGHT FROM PIANO KEY WIDTH AND HEIGHTS **/
		// noteOr.setMinimumWidth(piano.getKeyWidths("white")); //change to
		// public static later
		/*** PIANO.FIREDISPLAY(NOTEKEY) ***/
		rlPiano.addView(piano, lpPiano);
		layout.addView(rlPiano);

		setContentView(layout);
		// player.SetPiano(piano);
		layout.requestLayout();
		/**
		 * LASTLY: MAKE A BUTTON SO THAT WHEN WE PRESS IT, IT WILL USE VIEWFLIP
		 * TO CHANGE TO THE SAME PIANO VIEW BUT SCROLLED UP ONE OCTAVE
		 */
	}

	public static int getCallBack() {
		return callBack;
	}

	public static void callBack(int value) {
		callBack = value;
	}

	private void createViewOnlyPiano() {
		Display display = getWindowManager().getDefaultDisplay();
		// Point size = new Point();
		int width = display.getWidth();
		int height = display.getHeight();
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
		/* for the button */
		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout rl = new RelativeLayout(this);

		noteFall = new Button(this);
		noteFall.setText("Start Tutorial!");
		noteFall.setLayoutParams(layoutParams2);
		rl.addView(noteFall);
		layout.addView(rl, layoutParams);

		popupLayout = new LinearLayout(this);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		textview = new TextView(this);
		textview.setText("Hi this is a sample text for popup window");
		popUp = new PopupWindow(this);

		final String[] blinkNotes = { "3N", "4N", "5N", "6N", "2#" };
		final int[] playAscending = { 0, 1, 2, 3, 4, 5, 6, 7 };
		final int[] playDescending = { 7, 6, 5, 4, 3, 2, 1, 0 };

		/*
		 * final NotePlay[] playAscending = { 0, 1, 2, 3, 4, 5, 6, 7 }; final
		 * NotePlay[] playDescending = { 7, 6, 5, 4, 3, 2, 1, 0 };
		 */

		/****** Cmajor scale ******/
		int size = 8;
		np = new NotePlay[size];
		final int[] npNote = { 0, 1, 2, 3, 4, 5, 6, 7 };
		final int[] npDur = { 2, 2, 2, 2, 2, 2, 2, 2 };
		for (int i = 0; i < np.length; i++)
			np[i] = new NotePlay(npDur[i], npNote[i]);

		/********** Mary had a little lamb **************/

		final int[] llNote = {6, 5, 4, 5, 6, 6, 6, 5, 5, 5, 6, 8, 8,
				6, 5, 4, 5, 6, 6, 6, 6, 5, 5, 6, 5, 4 };
		final int[] llDur = {1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 1, 2,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4 };
		int lambsize = llNote.length;
		littlelamb = new NotePlay[lambsize];
		for (int i = 0; i < littlelamb.length; i++)
			littlelamb[i] = new NotePlay(llDur[i], llNote[i]);
		/**
		 * Black {C#, D#, F#, G#, A#, C#, D#, F#, G#, A#} White {C, D, E, F, G,
		 * A, B, C, D, E, F, G, A, B} piano.tutorialNote([# of note][sharp # or
		 * N]); to stop, setTutUnShade = -1
		 * 
		 */
		noteFall.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				numClicks++;
				switch (numClicks - 1) {
				case 0:
					piano.playSong(littlelamb);
					break;
				case 1:
					piano.tutorialNote(playDescending);
					break;
				case 2:
					piano.playSong(np);
					break;
				case 3:
					// piano.tutorialNote(blinkNotes[3]);
					break;
				case 5:
					numClicks = 0;
					break;
				default:
					piano.toggleShade();
				}

			}
		});
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

		RelativeLayout rlPiano = new RelativeLayout(this);
		RelativeLayout.LayoutParams lpPiano = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		piano = new Piano(this, marginVal, sp, true);
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
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
		    layout.setBackgroundDrawable(background);
		} else {
		    layout.setBackground(background);
		}
		setContentView(layout);
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

	//
	// public void playNote(String note) {
	// sp.playNote();
	// }

	/** Create the SheetMusic view with the given options */
	// private void createSheetMusic(/* MidiOptions options */) {
	// // if (!options.showPiano) {
	// // piano.setVisibility(View.GONE);
	// // } else {
	// // piano.setVisibility(View.VISIBLE);
	// // }
	// sheet = new SheetMusic(this);
	// // sheet.initDefault(options);
	// // sheet.setPlayer(player);
	// layout.addView(sheet);
	// // piano.SetMidiFile(midifile, options, player);
	// // piano.SetShadeColors(options.shade1Color, options.shade2Color);
	// layout.requestLayout();
	// sheet.callOnDraw();
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_tutorial_ms, menu);
		return true;
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

	public class setupPiano extends AsyncTask<Void, Void, Boolean> {
		boolean running = false;

		@Override
		protected Boolean doInBackground(Void... arg0) {
			Log.e("noteFallTask", "BackExecute");
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			// finish?
			// TODO Auto-generated method stub
			Log.e("noteFallTask", "PostExecute");
			long ticks = 0L;

			noteC1A.startAnimation(animC1);

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
}
