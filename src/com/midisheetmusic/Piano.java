/*
 * Copyright (c) 2009-2011 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */

package com.midisheetmusic;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.model.NotePlay;

/**
 * @class Piano
 * 
 *        The Piano Control is the panel at the top that displays the piano, and
 *        highlights the piano notes during playback. The main methods are:
 * 
 *        SetMidiFile() - Set the Midi file to use for shading. The Midi file is
 *        needed to determine which notes to shade.
 * 
 *        ShadeNotes() - Shade notes on the piano that occur at a given pulse
 *        time.
 * 
 */
public class Piano extends SurfaceView implements SurfaceHolder.Callback {
	public static final int KeysPerOctave = 7;
	public static final int MaxOctave = 2;

	private static int WhiteKeyWidth;
	/** Width of a single white key */
	private static int WhiteKeyHeight;
	/** Height of a single white key */
	private static int BlackKeyWidth;
	/** Width of a single black key */
	private static int BlackKeyHeight;
	/** Height of a single black key */
	private static int margin;
	/** The top/left margin to the piano (0) */
	private static int BlackBorder;
	/** The width of the black border around the keys */

	private static int[] blackKeyOffsets;
	/** The x pixles of the black keys */

	/* The colors for drawing black/gray lines */
	private int gray1, gray2, gray3, shade1, shade2;

	private boolean useTwoColors;
	/** If true, use two colors for highlighting */
	private ArrayList<MidiNote> notes;
	/** The Midi notes for shading */
	private int maxShadeDuration;
	/** The maximum duration we'll shade a note for */
	private int showNoteLetters;
	/** Display the letter for each piano note */
	private Paint paint;
	/** The paint options for drawing */
	private boolean surfaceReady;
	/** True if we can draw on the surface */
	private Bitmap bufferBitmap;
	/** The bitmap for double-buffering */
	private Canvas bufferCanvas;
	/** The canvas for double-buffering */
	private MidiPlayer player;

	/** Used to find margin */
	private int[] margin_val;

	/***
	 * If true- set piano to only piano height and set blackheighty to 200 if
	 * false -otherwise
	 * **/
	private boolean pianoType;

	private SPPlayer soundPool, mySP;
	private int lastShaded = 0;

	private int blackHeightY = 150;
	ArrayList<Integer> toUnShade = new ArrayList<Integer>();
	ArrayList<Integer> xyPointer = new ArrayList<Integer>();
	ArrayList<Integer> logTouch = new ArrayList<Integer>();

	final int[] black = { 25, 27, 30, 32, 34, 37, 39, 42, 44, 46 };
	final int[] white = { 24, 26, 28, 29, 31, 33, 35, 36, 38, 40, 41, 43, 45,
			47 };
	// // put some of these initializing in the ctors later
	// HashMap<Integer, Integer> xy1 = new HashMap<Integer, Integer>();
	// HashMap<Integer, Integer> xy2 = new HashMap<Integer, Integer>();
	// HashMap<Integer, Integer> xy3 = new HashMap<Integer, Integer>();
	private int tutorialShade;

	private boolean tutUnShade;
	private int blinkShade;
	private Timer time, songTime;
	private int numBlinks = 0;
	private boolean wantTutCall;

	/** Create a new Piano. */
	public Piano(Context context) {
		super(context);

		WhiteKeyWidth = 0;
		blackKeyOffsets = null;
		paint = new Paint();
		paint.setAntiAlias(false);
		paint.setTextSize(9.0f);
		gray1 = Color.rgb(16, 16, 16);
		gray2 = Color.rgb(90, 90, 90);
		gray3 = Color.rgb(200, 200, 200);
		shade1 = Color.rgb(210, 205, 220);
		shade2 = Color.rgb(150, 200, 220);
		showNoteLetters = MidiOptions.NoteNameNone;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
	}

	public Piano(Context context, int[] valMargin, SPPlayer sp, boolean type) {
		super(context);
		xyPointer.add(0);
		xyPointer.add(0);
		xyPointer.add(0);
		WhiteKeyWidth = 0;
		blackKeyOffsets = null;
		paint = new Paint();
		paint.setAntiAlias(false);
		paint.setTextSize(9.0f);
		gray1 = Color.rgb(16, 16, 16);
		gray2 = Color.rgb(90, 90, 90);
		gray3 = Color.rgb(200, 200, 200);
		shade1 = Color.rgb(210, 205, 220);
		shade2 = Color.rgb(150, 200, 220);
		showNoteLetters = MidiOptions.NoteNameNone;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		this.margin_val = valMargin;
		this.soundPool = sp;
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
		tutorialShade = Color.rgb(0, 191, 255);
		tutUnShade = true;
		this.pianoType = type;
		if (pianoType)
			wantTutCall = true;
	}

	public Piano(Context context, AttributeSet attrs) {
		super(context, attrs);
		WhiteKeyWidth = 0;
		blackKeyOffsets = null;
		paint = new Paint();
		paint.setTextSize(10.0f);
		paint.setAntiAlias(false);
		gray1 = Color.rgb(16, 16, 16);
		gray2 = Color.rgb(90, 90, 90);
		gray3 = Color.rgb(200, 200, 200);
		shade1 = Color.rgb(210, 205, 220);
		shade2 = Color.rgb(150, 200, 220);
		showNoteLetters = MidiOptions.NoteNameNone;

		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
	}

	/** Get the preferreed width/height, given the screen width/height */
	public static Point getPreferredSize(int screenwidth, int screenheight) {
		int keywidth = (int) (screenwidth / (2.0 + KeysPerOctave * MaxOctave));
		if (keywidth % 2 != 0) {
			keywidth--;
		}
		// int margin = keywidth/2;
		int margin = 0;
		int border = keywidth / 2;

		Point result = new Point();
		result.x = margin * 2 + border * 2 + keywidth * KeysPerOctave
				* MaxOctave;
		result.y = margin * 2 + border * 3 + WhiteKeyHeight;
		return result;
	}

	/** Set the measured width and height */
	@Override
	protected void onMeasure(int widthspec, int heightspec) {
		int screenwidth = MeasureSpec.getSize(widthspec);
		int screenheight = MeasureSpec.getSize(heightspec);
		// (int) (screenwidth / (2.0 + KeysPerOctave * MaxOctave));
		WhiteKeyWidth = (int) (screenwidth / (0.8 + KeysPerOctave * MaxOctave)); // orig
		// 2.0
		if (WhiteKeyWidth % 2 != 0)
			WhiteKeyWidth--;

		// margin = WhiteKeyWidth/2;
		margin = 0;
		BlackBorder = WhiteKeyWidth / 2;
		if (pianoType) {
			WhiteKeyHeight = (int) (WhiteKeyWidth * 5.2);
			blackHeightY = (WhiteKeyHeight * 11 / 18);
		} else
			WhiteKeyHeight = (int) (WhiteKeyWidth * 3.5);
		BlackKeyWidth = (int) (WhiteKeyWidth * .65);
		BlackKeyHeight = WhiteKeyHeight * 11 / 18;

		blackKeyOffsets = new int[] { WhiteKeyWidth - BlackKeyWidth / 2 - 1,
				WhiteKeyWidth + BlackKeyWidth / 2 - 1,
				2 * WhiteKeyWidth - BlackKeyWidth / 2,
				2 * WhiteKeyWidth + BlackKeyWidth / 2,
				4 * WhiteKeyWidth - BlackKeyWidth / 2 - 1,
				4 * WhiteKeyWidth + BlackKeyWidth / 2 - 1,
				5 * WhiteKeyWidth - BlackKeyWidth / 2,
				5 * WhiteKeyWidth + BlackKeyWidth / 2,
				6 * WhiteKeyWidth - BlackKeyWidth / 2,
				6 * WhiteKeyWidth + BlackKeyWidth / 2 };

		int width = margin * 2 + BlackBorder * 2 + WhiteKeyWidth
				* KeysPerOctave * MaxOctave;
		int height = margin * 2 + (int) (BlackBorder * 1.5) + WhiteKeyHeight;
		setMeasuredDimension(width, height); // PLAY AROUND WITH THIS
		bufferBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bufferCanvas = new Canvas(bufferBitmap);
		this.invalidate();
		draw();
	}

	public int getKeyWidths(String Key) {
		if (Key.equals("white")) {
			return WhiteKeyWidth;
		} else if (Key.equals("black")) {
			return BlackKeyWidth;
		}
		Log.e("returning widths", "invalid : " + Key);
		return 0;
	}

	@Override
	protected void onSizeChanged(int newwidth, int newheight, int oldwidth,
			int oldheight) {
		super.onSizeChanged(newwidth, newheight, oldwidth, oldheight);
	}

	/**
	 * Set the MidiFile to use. Save the list of midi notes. Each midi note
	 * includes the note Number and StartTime (in pulses), so we know which
	 * notes to shade given the current pulse time.
	 */
	public void SetMidiFile(MidiFile midifile, MidiOptions options,
			MidiPlayer player) {
		if (midifile == null) {
			notes = null;
			useTwoColors = false;
			return;
		}
		this.player = player;
		ArrayList<MidiTrack> tracks = midifile.ChangeMidiNotes(options);
		MidiTrack track = MidiFile.CombineToSingleTrack(tracks);
		notes = track.getNotes();

		maxShadeDuration = midifile.getTime().getQuarter() * 2;

		/*
		 * We want to know which track the note came from. Use the 'channel'
		 * field to store the track.
		 */
		for (int tracknum = 0; tracknum < tracks.size(); tracknum++) {
			for (MidiNote note : tracks.get(tracknum).getNotes()) {
				note.setChannel(tracknum);
			}
		}

		/*
		 * When we have exactly two tracks, we assume this is a piano song, and
		 * we use different colors for highlighting the left hand and right hand
		 * notes.
		 */
		useTwoColors = false;
		if (tracks.size() == 2) {
			useTwoColors = true;
		}

		showNoteLetters = options.showNoteLetters;
		this.invalidate();
	}

	/** Set the colors to use for shading */
	public void SetShadeColors(int c1, int c2) {
		shade1 = c1;
		shade2 = c2;
	}

	/** Draw the outline of a 12-note (7 white note) piano octave */
	private void DrawOctaveOutline(Canvas canvas) {
		int right = WhiteKeyWidth * KeysPerOctave;

		// Draw the bounding rectangle, from C to B
		paint.setColor(gray1);
		canvas.drawLine(0, 0, 0, WhiteKeyHeight, paint);
		canvas.drawLine(right, 0, right, WhiteKeyHeight, paint);
		// canvas.drawLine(0, 0, right, 0, paint);
		canvas.drawLine(0, WhiteKeyHeight, right, WhiteKeyHeight, paint);
		paint.setColor(gray3);
		canvas.drawLine(right - 1, 0, right - 1, WhiteKeyHeight, paint);
		canvas.drawLine(1, 0, 1, WhiteKeyHeight, paint);

		// Draw the line between E and F
		paint.setColor(gray1);
		canvas.drawLine(3 * WhiteKeyWidth, 0, 3 * WhiteKeyWidth,
				WhiteKeyHeight, paint);
		paint.setColor(gray3);
		canvas.drawLine(3 * WhiteKeyWidth - 1, 0, 3 * WhiteKeyWidth - 1,
				WhiteKeyHeight, paint);
		canvas.drawLine(3 * WhiteKeyWidth + 1, 0, 3 * WhiteKeyWidth + 1,
				WhiteKeyHeight, paint);

		// Draw the sides/bottom of the black keys
		for (int i = 0; i < 10; i += 2) {
			int x1 = blackKeyOffsets[i];
			int x2 = blackKeyOffsets[i + 1];

			paint.setColor(gray1);
			canvas.drawLine(x1, 0, x1, BlackKeyHeight, paint);
			canvas.drawLine(x2, 0, x2, BlackKeyHeight, paint);
			canvas.drawLine(x1, BlackKeyHeight, x2, BlackKeyHeight, paint);
			paint.setColor(gray2);
			canvas.drawLine(x1 - 1, 0, x1 - 1, BlackKeyHeight + 1, paint);
			canvas.drawLine(x2 + 1, 0, x2 + 1, BlackKeyHeight + 1, paint);
			canvas.drawLine(x1 - 1, BlackKeyHeight + 1, x2 + 1,
					BlackKeyHeight + 1, paint);
			paint.setColor(gray3);
			canvas.drawLine(x1 - 2, 0, x1 - 2, BlackKeyHeight + 2, paint);
			canvas.drawLine(x2 + 2, 0, x2 + 2, BlackKeyHeight + 2, paint);
			canvas.drawLine(x1 - 2, BlackKeyHeight + 2, x2 + 2,
					BlackKeyHeight + 2, paint);
		}

		// Draw the bottom-half of the white keys
		for (int i = 1; i < KeysPerOctave; i++) {
			if (i == 3) {
				continue; // we draw the line between E and F above
			}
			paint.setColor(gray1);
			canvas.drawLine(i * WhiteKeyWidth, BlackKeyHeight, i
					* WhiteKeyWidth, WhiteKeyHeight, paint);
			paint.setColor(gray2);
			canvas.drawLine(i * WhiteKeyWidth - 1, BlackKeyHeight + 1, i
					* WhiteKeyWidth - 1, WhiteKeyHeight, paint);
			paint.setColor(gray3);
			canvas.drawLine(i * WhiteKeyWidth + 1, BlackKeyHeight + 1, i
					* WhiteKeyWidth + 1, WhiteKeyHeight, paint);
		}

	}

	/** Draw an outline of the piano for 6 octaves */
	private void DrawOutline(Canvas canvas) {
		for (int octave = 0; octave < MaxOctave; octave++) {
			canvas.translate(octave * WhiteKeyWidth * KeysPerOctave, 0);
			DrawOctaveOutline(canvas);
			canvas.translate(-(octave * WhiteKeyWidth * KeysPerOctave), 0);
		}
	}

	/* Draw the Black keys */
	private void DrawBlackKeys(Canvas canvas) {
		paint.setStyle(Paint.Style.FILL);
		for (int octave = 0; octave < MaxOctave; octave++) {
			canvas.translate(octave * WhiteKeyWidth * KeysPerOctave, 0);
			for (int i = 0; i < 10; i += 2) {
				int x1 = blackKeyOffsets[i];
				int x2 = blackKeyOffsets[i + 1];
				paint.setColor(gray1);
				canvas.drawRect(x1, 0, x1 + BlackKeyWidth, BlackKeyHeight,
						paint);
				paint.setColor(gray2);
				canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
						+ 1 + BlackKeyWidth - 2, BlackKeyHeight
						- BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
			}
			canvas.translate(-(octave * WhiteKeyWidth * KeysPerOctave), 0);
		}
		paint.setStyle(Paint.Style.STROKE);
	}

	/*
	 * Draw the black border area surrounding the piano keys. Also, draw gray
	 * outlines at the bottom of the white keys.
	 */
	private void DrawBlackBorder(Canvas canvas) {
		int PianoWidth = WhiteKeyWidth * KeysPerOctave * MaxOctave;
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(gray1);
		canvas.drawRect(margin, margin, margin + PianoWidth + BlackBorder * 2,
				margin + BlackBorder - 2, paint);
		canvas.drawRect(margin, margin, margin + BlackBorder, margin
				+ WhiteKeyHeight + BlackBorder * 3, paint);
		canvas.drawRect(margin, margin + BlackBorder + WhiteKeyHeight, margin
				+ BlackBorder * 2 + PianoWidth, margin + BlackBorder
				+ WhiteKeyHeight + BlackBorder * 2, paint);
		canvas.drawRect(margin + BlackBorder + PianoWidth, margin, margin
				+ BlackBorder + PianoWidth + BlackBorder, margin
				+ WhiteKeyHeight + BlackBorder * 3, paint);

		paint.setColor(gray2);
		canvas.drawLine(margin + BlackBorder, margin + BlackBorder - 1, margin
				+ BlackBorder + PianoWidth, margin + BlackBorder - 1, paint);

		canvas.translate(margin + BlackBorder, margin + BlackBorder);

		// Draw the gray bottoms of the white keys
		for (int i = 0; i < KeysPerOctave * MaxOctave; i++) {
			canvas.drawRect(i * WhiteKeyWidth + 1, WhiteKeyHeight + 2, i
					* WhiteKeyWidth + 1 + WhiteKeyWidth - 2, WhiteKeyHeight + 2
					+ BlackBorder / 2, paint);
		}
		canvas.translate(-(margin + BlackBorder), -(margin + BlackBorder));
	}

	/** Draw the note letters (A, A#, Bb, etc) underneath each white note */
	private void DrawNoteLetters(Canvas canvas) {
		String[] letters = { "C", "D", "E", "F", "G", "A", "B" };
		String[] numbers = { "1", "3", "5", "6", "8", "10", "12" };
		String[] names;
		if (showNoteLetters == MidiOptions.NoteNameLetter) {
			names = letters;
		} else if (showNoteLetters == MidiOptions.NoteNameFixedNumber) {
			names = numbers;
		} else {
			names = letters;
		}
		canvas.translate(margin + BlackBorder, margin + BlackBorder);
		paint.setColor(Color.WHITE);
		paint.setTextSize(10);
		for (int octave = 0; octave < MaxOctave; octave++) {
			for (int i = 0; i < KeysPerOctave; i++) {
				canvas.drawText(names[i], (octave * KeysPerOctave + i)
						* WhiteKeyWidth + WhiteKeyWidth / 3, WhiteKeyHeight
						+ BlackBorder - 18, paint);
			}
		}
		canvas.translate(-(margin + BlackBorder), -(margin + BlackBorder));
		paint.setColor(Color.BLACK);
	}

	/** Obtain the drawing canvas and call onDraw() */
	void draw() {
		if (!surfaceReady) {
			return;
		}
		SurfaceHolder holder = getHolder();
		Canvas canvas = holder.lockCanvas();
		if (canvas == null) {
			return;
		}
		onDraw(canvas);
		holder.unlockCanvasAndPost(canvas);
	}

	/** Draw the Piano. */
	@Override
	protected void onDraw(Canvas canvas) {
		if (!surfaceReady || bufferBitmap == null) {
			return;
		}
		if (WhiteKeyWidth == 0) {
			return;
		}

		paint.setAntiAlias(false);
		bufferCanvas.translate(margin + BlackBorder, margin + BlackBorder);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		bufferCanvas.drawRect(0, 0, 0 + WhiteKeyWidth * KeysPerOctave
				* MaxOctave, WhiteKeyHeight, paint);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(gray1);
		DrawBlackKeys(bufferCanvas);
		DrawOutline(bufferCanvas);
		bufferCanvas
				.translate(-(margin + BlackBorder), -(margin + BlackBorder));
		DrawBlackBorder(bufferCanvas);
		canvas.drawBitmap(bufferBitmap, 0, 0, paint);
		// if (showNoteLetters != MidiOptions.NoteNameNone) {
		DrawNoteLetters(canvas);
		// }
	}

	/**
	 * Find the MidiNote with the startTime closest to the given time. Return
	 * the index of the note. Use a binary search method.
	 */
	private int FindClosestStartTime(int pulseTime) {
		int left = 0;
		int right = notes.size() - 1;

		while (right - left > 1) {
			int i = (right + left) / 2;
			if (notes.get(left).getStartTime() == pulseTime)
				break;
			else if (notes.get(i).getStartTime() <= pulseTime)
				left = i;
			else
				right = i;
		}
		while (left >= 1
				&& (notes.get(left - 1).getStartTime() == notes.get(left)
						.getStartTime())) {
			left--;
		}
		return left;
	}

	/**
	 * Return the next StartTime that occurs after the MidiNote at offset i,
	 * that is also in the same track/channel.
	 */
	private int NextStartTimeSameTrack(int i) {
		int start = notes.get(i).getStartTime();
		int end = notes.get(i).getEndTime();
		int track = notes.get(i).getChannel();

		while (i < notes.size()) {
			if (notes.get(i).getChannel() != track) {
				i++;
				continue;
			}
			if (notes.get(i).getStartTime() > start) {
				return notes.get(i).getStartTime();
			}
			end = Math.max(end, notes.get(i).getEndTime());
			i++;
		}
		return end;
	}

	/**
	 * Return the next StartTime that occurs after the MidiNote at offset i. If
	 * all the subsequent notes have the same StartTime, then return the largest
	 * EndTime.
	 */
	private int NextStartTime(int i) {
		int start = notes.get(i).getStartTime();
		int end = notes.get(i).getEndTime();

		while (i < notes.size()) {
			if (notes.get(i).getStartTime() > start) {
				return notes.get(i).getStartTime();
			}
			end = Math.max(end, notes.get(i).getEndTime());
			i++;
		}
		return end;
	}

	/**
	 * Find the Midi notes that occur in the current time. Shade those notes on
	 * the piano displayed. Un-shade the those notes played in the previous
	 * time.
	 */
	public void ShadeNotes(int currentPulseTime, int prevPulseTime) {
		if (notes == null || notes.size() == 0 || !surfaceReady
				|| bufferBitmap == null) {
			return;
		}
		SurfaceHolder holder = getHolder();
		Canvas canvas = holder.lockCanvas();
		if (canvas == null) {
			return;
		}

		bufferCanvas.translate(margin + BlackBorder, margin + BlackBorder);

		/*
		 * Loop through the Midi notes. Unshade notes where StartTime <=
		 * prevPulseTime < next StartTime Shade notes where StartTime <=
		 * currentPulseTime < next StartTime
		 */
		int lastShadedIndex = FindClosestStartTime(prevPulseTime
				- maxShadeDuration * 2);
		for (int i = lastShadedIndex; i < notes.size(); i++) {
			int start = notes.get(i).getStartTime();
			int end = notes.get(i).getEndTime();
			int notenumber = notes.get(i).getNumber();
			int nextStart = NextStartTime(i);
			int nextStartTrack = NextStartTimeSameTrack(i);
			end = Math.max(end, nextStartTrack);
			end = Math.min(end, start + maxShadeDuration - 1);

			/* If we've past the previous and current times, we're done. */
			if ((start > prevPulseTime) && (start > currentPulseTime)) {
				break;
			}

			/* If shaded notes are the same, we're done */
			if ((start <= currentPulseTime) && (currentPulseTime < nextStart)
					&& (currentPulseTime < end) && (start <= prevPulseTime)
					&& (prevPulseTime < nextStart) && (prevPulseTime < end)) {
				break;
			}

			/* If the note is in the current time, shade it */
			if ((start <= currentPulseTime) && (currentPulseTime < end)) {
				if (useTwoColors) {
					if (notes.get(i).getChannel() == 1) {
						ShadeOneNote(bufferCanvas, notenumber, shade2);
					} else {
						ShadeOneNote(bufferCanvas, notenumber, shade1);
					}
				} else {
					ShadeOneNote(bufferCanvas, notenumber, shade1);
				}
			}

			/* If the note is in the previous time, un-shade it, draw it white. */
			else if ((start <= prevPulseTime) && (prevPulseTime < end)) {
				int num = notenumber % 12;
				if (num == 1 || num == 3 || num == 6 || num == 8 || num == 10) {
					ShadeOneNote(bufferCanvas, notenumber, gray1);
				} else {
					ShadeOneNote(bufferCanvas, notenumber, Color.WHITE);
				}
			}
		}
		bufferCanvas
				.translate(-(margin + BlackBorder), -(margin + BlackBorder));
		canvas.drawBitmap(bufferBitmap, 0, 0, paint);
		holder.unlockCanvasAndPost(canvas);
	}

	/*
	 * Shade the given note with the given brush. We only draw notes from
	 * notenumber 24 to 96. (Middle-C is 60).
	 */
	private void ShadeOneNote(Canvas canvas, int notenumber, int color) {
		/** IF THE COLOR IS BLACK!!!! CHANGE BACK!!! **/
		// Log.e("Shade", " first here");
		int octave = notenumber / 12;
		int notescale = notenumber % 12;

		octave -= 2;
		if (octave < 0 || octave >= MaxOctave) {
			Log.e("Shade", " octave " + octave + " MaxOctave " + MaxOctave);
			return;
		}
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		canvas.translate(octave * WhiteKeyWidth * KeysPerOctave, 0);
		int x1, x2, x3;

		int bottomHalfHeight = WhiteKeyHeight - (BlackKeyHeight + 3);

		/* notescale goes from 0 to 11, from C to B. */
		switch (notescale) {
		case 0: /* C */
			x1 = 2;
			x2 = blackKeyOffsets[0] - 2;
			canvas.drawRect(x1, 0, x1 + x2 - x1, 0 + BlackKeyHeight + 3, paint);
			canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
					BlackKeyHeight + 3 + bottomHalfHeight, paint);
			break;
		case 1: /* C# */
			x1 = blackKeyOffsets[0];
			x2 = blackKeyOffsets[1];
			canvas.drawRect(x1, 0, x1 + x2 - x1, 0 + BlackKeyHeight, paint);
			if (color == gray1) {
				paint.setColor(gray2);
				canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
						+ 1 + BlackKeyWidth - 2, BlackKeyHeight
						- BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
			}
			break;
		case 2: /* D */
			x1 = WhiteKeyWidth + 2;
			x2 = blackKeyOffsets[1] + 3;
			x3 = blackKeyOffsets[2] - 2;
			canvas.drawRect(x2, 0, x2 + x3 - x2, 0 + BlackKeyHeight + 3, paint);
			canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
					BlackKeyHeight + 3 + bottomHalfHeight, paint);
			break;
		case 3: /* D# */
			x1 = blackKeyOffsets[2];
			x2 = blackKeyOffsets[3];
			canvas.drawRect(x1, 0, x1 + BlackKeyWidth, 0 + BlackKeyHeight,
					paint);
			if (color == gray1) {
				paint.setColor(gray2);
				canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
						+ 1 + BlackKeyWidth - 2, BlackKeyHeight
						- BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
			}
			break;
		case 4: /* E */
			x1 = WhiteKeyWidth * 2 + 2;
			x2 = blackKeyOffsets[3] + 3;
			x3 = WhiteKeyWidth * 3 - 1;
			canvas.drawRect(x2, 0, x2 + x3 - x2, 0 + BlackKeyHeight + 3, paint);
			canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
					BlackKeyHeight + 3 + bottomHalfHeight, paint);
			break;
		case 5: /* F */
			x1 = WhiteKeyWidth * 3 + 2;
			x2 = blackKeyOffsets[4] - 2;
			x3 = WhiteKeyWidth * 4 - 2;
			canvas.drawRect(x1, 0, x1 + x2 - x1, 0 + BlackKeyHeight + 3, paint);
			canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
					BlackKeyHeight + 3 + bottomHalfHeight, paint);
			break;
		case 6: /* F# */
			x1 = blackKeyOffsets[4];
			x2 = blackKeyOffsets[5];
			canvas.drawRect(x1, 0, x1 + BlackKeyWidth, 0 + BlackKeyHeight,
					paint);
			if (color == gray1) {
				paint.setColor(gray2);
				canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
						+ 1 + BlackKeyWidth - 2, BlackKeyHeight
						- BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
			}
			break;
		case 7: /* G */
			x1 = WhiteKeyWidth * 4 + 2;
			x2 = blackKeyOffsets[5] + 3;
			x3 = blackKeyOffsets[6] - 2;
			canvas.drawRect(x2, 0, x2 + x3 - x2, 0 + BlackKeyHeight + 3, paint);
			canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
					BlackKeyHeight + 3 + bottomHalfHeight, paint);
			break;
		case 8: /* G# */
			x1 = blackKeyOffsets[6];
			x2 = blackKeyOffsets[7];
			canvas.drawRect(x1, 0, x1 + BlackKeyWidth, 0 + BlackKeyHeight,
					paint);
			if (color == gray1) {
				paint.setColor(gray2);
				canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
						+ 1 + BlackKeyWidth - 2, BlackKeyHeight
						- BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
			}
			break;
		case 9: /* A */
			x1 = WhiteKeyWidth * 5 + 2;
			x2 = blackKeyOffsets[7] + 3;
			x3 = blackKeyOffsets[8] - 2;
			canvas.drawRect(x2, 0, x2 + x3 - x2, 0 + BlackKeyHeight + 3, paint);
			canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
					BlackKeyHeight + 3 + bottomHalfHeight, paint);
			break;
		case 10: /* A# */
			x1 = blackKeyOffsets[8];
			x2 = blackKeyOffsets[9];
			canvas.drawRect(x1, 0, x1 + BlackKeyWidth, 0 + BlackKeyHeight,
					paint);
			if (color == gray1) {
				paint.setColor(gray2);
				canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
						+ 1 + BlackKeyWidth - 2, BlackKeyHeight
						- BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
			}
			break;
		case 11: /* B */
			x1 = WhiteKeyWidth * 6 + 2;
			x2 = blackKeyOffsets[9] + 3;
			x3 = WhiteKeyWidth * KeysPerOctave - 1;
			canvas.drawRect(x2, 0, x2 + x3 - x2, 0 + BlackKeyHeight + 3, paint);
			canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
					BlackKeyHeight + 3 + bottomHalfHeight, paint);
			break;
		default:
			break;
		}
		canvas.translate(-(octave * WhiteKeyWidth * KeysPerOctave), 0);
	}

	/** TODO ?? */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		draw();
	}

	/** Surface is ready for shading the notes */
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceReady = true;
		// setWillNotDraw(false);
		draw();
	}

	/** Surface has been destroyed */
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceReady = false;
	}

	/**
	 * set to -1 when you want to stop the tutorial note and begin a new one
	 */
	public void toggleShade() {
		tutUnShade = ((tutUnShade == true) ? false : true);
	}

	public int getNumBlinks() {
		return numBlinks;
	}

	int[] noteArr = {};
	NotePlay[] songArr = {};

	int curr = 0;
	int noteToShade = 0;

	/**
	 * for the tutorial note to enter: Black {C#, D#, F#, G#, A#, C#, D#, F#,
	 * G#, A#} White {C, D, E, F, G, A, B, C, D, E, F, G, A, B} encode as [# of
	 * note][sharp # or N]
	 */
	// (NotePlay[] notes)
	// public void tutorialNote(int[] note) {
	// // Log.e("coord", " THE COORD I WANT IS : " + (WhiteKeyWidth * 6 +
	// // (margin_val[0] + WhiteKeyWidth + BlackBorder)));
	// // make it blink
	// if (!surfaceReady || bufferBitmap == null) {
	// Log.e("Shade", "fail");
	// return;
	// }
	//
	// noteArr = note;
	// boolean isBlack; // either black or white
	// /*
	// * if (note == "") return; else { // turn from [note][Sharp or flat][1
	// * or 2] into black[] or white[] // array int letter =
	// * Integer.parseInt(note.charAt(0) + ""); // get the note String sharp =
	// * note.charAt(1) + ""; isBlack = (sharp.equals("N")) ? false : true; if
	// * (isBlack) { Log.i("black", "is balck"); noteToShade = black[letter];
	// * } else { Log.i("black", "is white"); noteToShade = white[letter]; } }
	// */
	// blinkShade = 1; // 1=unshade
	//
	// try {
	// unShade(noteToShade);
	// time.cancel();
	// } catch (Exception ex) {
	// }
	// time = new Timer();
	// if (tutUnShade == false) {
	// unShade(noteToShade);
	// time.cancel();
	// return;
	// }
	// time.scheduleAtFixedRate(new TimerTask() {
	//
	// @Override
	// public void run() {
	//
	// /*
	// * if (TutorialMSActivity.getCallBack() == noteToShade) {
	// * cancel(); unShade(noteToShade); }
	// */
	// // Shade and play note
	// if (blinkShade == 1) {
	// SurfaceHolder holder = getHolder();
	// Canvas canvas = holder.lockCanvas();
	// if (canvas == null) {
	// Log.e("Shade", "fail in timer scheduling");
	// time.cancel();
	// return;
	// }
	// bufferCanvas.translate(margin + BlackBorder, margin
	// + BlackBorder);
	// noteToShade = white[noteArr[curr]]; // noteArr[curr].getNote()
	// ShadeOneNote(bufferCanvas, noteToShade, Color.LTGRAY);
	// String toPlaySound = noteToShadetoPlayConverter(
	// noteToShade, true);
	// soundPool.playNote(toPlaySound, 1);
	// bufferCanvas.translate(-(margin + BlackBorder),
	// -(margin + BlackBorder));
	// canvas.drawBitmap(bufferBitmap, 0, 0, paint);
	// DrawNoteLetters(canvas);
	// holder.unlockCanvasAndPost(canvas);
	// blinkShade = 0;
	// numBlinks++;
	// } else if (blinkShade == 0) {
	// unShade(noteToShade);
	// blinkShade = 1;
	// if (curr >= noteArr.length - 1) {
	// curr = 0;
	// cancel();
	// } else
	// curr++;
	// }
	// if (!tutUnShade) {
	// unShade(noteToShade);
	// blinkShade = 2;
	// return;
	// }
	// }
	//
	// @Override
	// public boolean cancel() {
	// super.cancel();
	// Log.e("timer", "canceled");
	// unShade(noteToShade);
	// return true;
	// }
	//
	// }, 500, 200);
	//
	// }

	public ArrayList<Integer> retLogShade() {
		return logTouch;
	}

	/**
	 * for the tutorial note to enter: Black {C#, D#, F#, G#, A#, C#, D#, F#,
	 * G#, A#} White {C, D, E, F, G, A, B, C, D, E, F, G, A, B} encode as [# of
	 * note][sharp # or N]
	 */
	// (NotePlay[] notes)
	public void playSong(NotePlay[] notes, int speed) {
		if (!surfaceReady || bufferBitmap == null) {
			Log.e("Shade", "fail");
			return;
		}
        for(int i = 0; i < notes.length; i++) {
        	Log.i("NOTEPLAY", "THE NOTES ARRAY: " + notes[i].getNote());
        }
		songArr = notes;
		blinkShade = 1; // 1=unshade
		curr = 0;
		/*
		 * try { unShade(noteToShade); time.cancel(); } catch (Exception ex) {
		 * Log.i("unshading", " error playsong"); }
		 */
		time = new Timer();
		if (tutUnShade == false) {
			unShade(noteToShade);
			time.cancel();
			return;
		}
		time.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				String toPlaySound;
				// Shade and play note
				if (blinkShade == 1) {
					SurfaceHolder holder = getHolder();
					Canvas canvas = holder.lockCanvas();
					if (canvas == null) {
						Log.e("Shade", "fail in timer scheduling");
						time.cancel();
						return;
					}
					bufferCanvas.translate(margin + BlackBorder, margin
							+ BlackBorder);

					if (songArr[curr].getNote() == -1) {
						noteToShade = -1;
						toPlaySound = "";
					} else {
						double note = songArr[curr].getNote();
						if (note % 1 == 0) {
							noteToShade = white[(int) note]; // noteArr[curr].getNote()
							Log.i("noteShade", "noteToShade white: "
									+ noteToShade);
						} else {
							noteToShade = black[(int) Math.round(note) - 1];
							Log.i("noteShade", "noteToShade black: "
									+ noteToShade);
						}
						ShadeOneNote(bufferCanvas, noteToShade, Color.LTGRAY);
						toPlaySound = noteToShadetoPlayConverter(noteToShade,
								true);
					}
					songArr[curr].incCounter();

					/*
					 * In this place, if counter has reached duration, set
					 * blinkshade to 0 otherwise, keep blinkshade and play the
					 * note, but only once.
					 */
					if (songArr[curr].getCounter() > (songArr[curr]
							.getDuration())) {
						Log.i("GETTING DURATION",
								"at curr: " + curr
										+ " the duration and counter "
										+ songArr[curr].getDuration() + " , "
										+ songArr[curr].getCounter());
						blinkShade = 0;
					} else if (songArr[curr].getCounter() == 1) {
						Log.i("NOTEPLAY", " playing " + toPlaySound);
						soundPool.playNote(toPlaySound, 1);
					}
					bufferCanvas.translate(-(margin + BlackBorder),
							-(margin + BlackBorder));
					canvas.drawBitmap(bufferBitmap, 0, 0, paint);
					DrawNoteLetters(canvas);
					holder.unlockCanvasAndPost(canvas);

					// numBlinks++;
				} else if (blinkShade == 0) {
					unShade(noteToShade);
					blinkShade = 1;
					if (curr >= songArr.length - 1) {
						curr = 0;
						cancel();
					} else
						curr++;
				}
				if (!tutUnShade) {
					unShade(noteToShade);
					blinkShade = 2;
					return;
				}
			}

			@Override
			public boolean cancel() {
				super.cancel();
				Log.e("timer", "canceled");
				unShade(noteToShade);
				return true;
			}

		}, 500, speed);

	}

	/**
	 * Black keys range from 0.5, 1.5, 2.5, 3.5, 4.5, 5.5, 6.5, 7.5, 8.5, 9.5 
	 *                          C# D# F# G# A# C# D# F# G# A#
	 */
	private String noteToShadetoPlayConverter(int noteToShade,
			boolean blackorwhite) {
		// final int[] black = { 25, 27, 30, 32, 34, 37, 39, 42, 44, 46 };
		// final int[] white = { 24, 26, 28, 29, 31, 33, 35, 36, 38, 40, 41, 43,
		// 45, 47 };
		// find index first, then convert index to note from c = index 0, d =
		// index 1
		int index = 0;
		boolean iswhite = false;
		for (int i = 0; i < white.length; i++) {
			if (white[i] == noteToShade) {
				index = i;
				Log.i("found the index at index:", "index: " + i);
				iswhite = true;
			}
		}
		if (!iswhite) {
			for (int i = 0; i < black.length; i++) {
				if (black[i] == noteToShade) {
					index = i;
					Log.i("found the index at index:", "index: " + i);
				}
			}
			if(index >= 2) {
				index++;
			}
			if(index >= 6) {
				index++;
			}
			if(index >= 9) {
				index++;
			}
			
		}

		/* stuff to init for assembling string */
		String key = "key";
		char note = 'c';
		/* and other stuff */

		note = (char) (note + index);
		Log.i("current note:", " the note is " + note);
		if (note > 'g') {
			note = 'a';
			note = (char) (note + (index - 5));
			if(note > 'g') note = 'a';
		} else
			Log.i("current note:", "the note is :" + note);
		// if index > something change to 3 otherwise 4
		// if lower octave, encoding = 3,
		if (noteToShade <= 35)
			key = key + note + "3";
		else
			key = key + note + "4";

		if (!iswhite) {
			key += "s";
		}
		Log.i("current note:", "note: " + key);

		return key;
	}

	/** When the Piano is touched */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();
		PointerCoords pCord;

		// Log.d("TouchTest", "numpointers = " + event.getPointerCount());
		// INSTEAD OF TOUNSHADE.ADD CHANGE TO TOUNSHADE.SET GET ACTION INDEX
		if (event.getActionMasked() == android.view.MotionEvent.ACTION_DOWN) {
			// Log.d("TouchTest", "Touch down");
			toUnShade.add(ShadeRandom(x, y, false));
			logTouch.add(toUnShade.get(toUnShade.size() - 1));
		} else if (event.getActionMasked() == android.view.MotionEvent.ACTION_UP) {
			int size = toUnShade.size();
			try {
				for (int i = 0; i < size; i++) {
					if (size != 0) {
						unShade(toUnShade.get(0));
						toUnShade.remove(0);
						// Log.e("TouchTest", " unshaded :" + toUnShade.get(0));
					}
				}
			} catch (IndexOutOfBoundsException ex) {
				Log.e("TouchTest", " FAILS IN ARRAYLIST");
			}
			// Log.d("TouchTest", "Touch up");
		} else if (event.getActionMasked() == android.view.MotionEvent.ACTION_MOVE) {
			// Log.d("TouchTest", "move while down?")
			switch (event.getPointerCount()) {
			case 4:
				// Log.i("TouchTest", "COMES TO 4TH");
			case 3:
				// if my note -== last note
				if (xyPointer.get(2) != getMyNote(event.getX(2), event.getY(2))) {
					Log.d("TouchTest", "comes here! for sense");
					toUnShade.add(ShadeRandom(event.getX(2), event.getY(2),
							true));
					logTouch.add(toUnShade.get(toUnShade.size() - 1));
					// on insert- > first clear the item and then insert it
					// xy3.put((int) event.getX(2), (int) event.getY(2));
					xyPointer.set(2, toUnShade.get(toUnShade.size() - 1));
					// should unshade old
					// Log.d("TouchTest", "not the same !");
				}
				// else
				// Log.d("TouchTest", "THE SAME!");
				// Log.d("TouchTest",
				// "toUnShade.get last= "
				// + toUnShade.get(toUnShade.size() - 1));
			case 2:
				// if my note -== last note
				if (xyPointer.get(1) != getMyNote(event.getX(1), event.getY(1))) {
					Log.d("TouchTest", "comes here! for sense");
					toUnShade.add(ShadeRandom(event.getX(1), event.getY(1),
							true));
					logTouch.add(toUnShade.get(toUnShade.size() - 1));
					// on insert- > first clear the item and then insert it
					// xy3.put((int) event.getX(2), (int) event.getY(2));
					xyPointer.set(1, toUnShade.get(toUnShade.size() - 1));
					// should unshade old
					// Log.d("TouchTest", "not the same !");
				}
				// else
				// Log.d("TouchTest", "THE SAME!");
			case 1:
				// if my note -== last note
				if (xyPointer.get(0) != getMyNote(event.getX(0), event.getY(0))) {
					// Log.d("TouchTest", "comes here! for sense");
					toUnShade.add(ShadeRandom(event.getX(0), event.getY(0),
							true));
					logTouch.add(toUnShade.get(toUnShade.size() - 1));
					// on insert- > first clear the item and then insert it
					// xy3.put((int) event.getX(2), (int) event.getY(2));
					xyPointer.set(0, toUnShade.get(toUnShade.size() - 1));
					// should unshade old
					// Log.d("TouchTest", "not the same !");
				}
				// else
				// Log.d("TouchTest", "THE SAME!");
				// xy3.put((int) event.getX(0), (int) event.getY(0));
				// xyPointer.set(0, xy3);
				// Log.d("TouchTest",
				// "toUnShade.get last= "
				// + toUnShade.get(toUnShade.size() - 1));
				break;

			default:
				Log.i("TouchTest",
						"COMES TO default :" + event.getPointerCount());
				break;

			}

		} else if (event.getActionMasked() == android.view.MotionEvent.ACTION_POINTER_DOWN) {
			// Log.d("TouchTest",
			// "ANOTHER pointer touch down and its pointer id is "
			// + event.getPointerId(1) + " and action index "
			// + event.getActionIndex());
			pCord = new PointerCoords();
			event.getPointerCoords(event.getActionIndex(), pCord);
			// Log.d("TouchTest", "new Pointer's x: " + pCord.x + " y: " +
			// pCord.y);
			toUnShade.add(ShadeRandom(pCord.x, pCord.y, false));
			logTouch.add(toUnShade.get(toUnShade.size() - 1));
		} else if (event.getActionMasked() == android.view.MotionEvent.ACTION_POINTER_UP) {
			int size = toUnShade.size();
			for (int i = 0; i < size; i++) {
				unShade(toUnShade.get(0));
				toUnShade.remove(0);
			}
			// Log.d("TouchTest", "another is up??");

			// in addition, go to the arraylist of hashmaps and clear the value
			// stored in it
		} else
			Log.d("TouchTest", "whatever");
		// Toast.makeText(getContext(), "touched at: " + x + " " + y,
		// Toast.LENGTH_LONG).show();
		return true;
	}

	private void unShade(Integer note) { // should pass in what it is
		if (!surfaceReady || bufferBitmap == null) {
			Log.e("Shade", "fail");
			return;
		}
		SurfaceHolder holder = getHolder();
		Canvas canvas = holder.lockCanvas();
		if (canvas == null) {
			Log.e("Shade", "fail2");
			return;
		}

		bufferCanvas.translate(margin + BlackBorder, margin + BlackBorder);
		int color = -1;
		int[] black = { 25, 27, 30, 32, 34, 37, 39, 42, 44, 46 };
		int[] white = { 24, 26, 28, 29, 31, 33, 35, 36, 38, 40, 41, 43, 45, 47 };
		int foundNote = -1;
		for (int i = 0; i < white.length; i++) {
			if (white[i] == note) {
				foundNote = white[i];
				i = white.length - 1; // or break, just to exit loop
				color = Color.WHITE;
			}
		}
		if (foundNote == -1) {
			Log.d("Shade", "comes hereeeee");
			for (int i = 0; i < black.length; i++) {
				if (black[i] == note) {
					foundNote = black[i];
					i = black.length - 1;
					color = gray1;
				}
			}
		}
		if (color == gray1 || color == Color.WHITE)
			ShadeOneNote(bufferCanvas, foundNote, color);
		else
			Log.e("Shade", "color is " + color);
		bufferCanvas
				.translate(-(margin + BlackBorder), -(margin + BlackBorder));
		canvas.drawBitmap(bufferBitmap, 0, 0, paint);
		DrawNoteLetters(canvas); // makes it slow
		holder.unlockCanvasAndPost(canvas);
	}

	/**
	 * 
	 * NoteToPlay parsing -
	 * "[# 1,2 (octave)] [Char note] [S = Sharp N = Natural]"
	 * 
	 * @param x
	 * @param y
	 * @param moved
	 * @return -1 if return none and note if return something
	 */
	private int ShadeRandom(float x, float y, boolean moved) {
		String noteToPlay = "";
		int color = gray1;
		int halfwayPoint = (WhiteKeyWidth * 6 + (margin_val[0] + WhiteKeyWidth + BlackBorder));
		// WhiteKeyWidth + BlackBorder));
		if (!surfaceReady || bufferBitmap == null) {
			Log.e("Shade", "fail");
			return -1;
		}
		SurfaceHolder holder = getHolder();
		Canvas canvas = holder.lockCanvas();
		if (canvas == null) {
			Log.e("Shade", "fail2");
			return -1;
		}

		bufferCanvas.translate(margin + BlackBorder, margin + BlackBorder);

		int[] black = { 25, 27, 30, 32, 34, 37, 39, 42, 44, 46 };
		int[] white = { 24, 26, 28, 29, 31, 33, 35, 36, 38, 40, 41, 43, 45, 47 };
		// if (moved) {
		// for (int i = 0; i < white.length; i++) {
		// if (lastShaded == white[i])
		// color = Color.WHITE;
		// }
		// ShadeOneNote(bufferCanvas, lastShaded, color);
		// }
		if (x < halfwayPoint) {
			// if it's in C4
			if (x < (margin_val[0] + WhiteKeyWidth + BlackBorder)) {
				if (x > (blackKeyOffsets[0] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[0];
					noteToPlay = "1cS";
				} else {
					lastShaded = white[0];
					noteToPlay = "1cN";
				}

			} else if (x < (WhiteKeyWidth + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[1] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[1];
					noteToPlay = "1dS";
				} else if (x < (blackKeyOffsets[0] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[1];
					noteToPlay = "1cS";
					// Log.i("coord", " goes in this!!");
				} else {
					lastShaded = white[1];
					noteToPlay = "1dN";
				}
			} else if (x < (WhiteKeyWidth * 2 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x < (blackKeyOffsets[1] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[1];
					noteToPlay = "1dS";
				} else {
					lastShaded = white[2];
					noteToPlay = "1eN";
				}
			} else if (x < (WhiteKeyWidth * 3 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[2] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[2];
					noteToPlay = "1fS";
					// Log.e("coord", "BLACKKEYSENSE 1st ");
				} /*
				 * else if (x < (blackKeyOffsets[1] + BlackBorder +
				 * BlackKeyWidth) && y < (blackHeightY)) { lastShaded =
				 * black[2]; ShadeOneNote(bufferCanvas, black[2], shade1);
				 * Log.e("coord", "BLACKKEYSENSE 2nd "); // Log.i("coord",
				 * " goes in this!!"); }
				 */else {
					lastShaded = white[3];
					noteToPlay = "1fN";
				}
			} else if (x < (WhiteKeyWidth * 4 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[3] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[3];
					noteToPlay = "1gS";
					// Log.e("coord", "BLACKKEYSENSE 1st ");
				} else if (x < (blackKeyOffsets[2] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[3];
					noteToPlay = "1fS";
					// Log.e("coord", "BLACKKEYSENSE 2nd ");
					// Log.i("coord", " goes in this!!");
				} else {
					lastShaded = white[4];
					noteToPlay = "1gN";
				}
			} else if (x < (WhiteKeyWidth * 5 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[4] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[4];
					noteToPlay = "1aS";
				} else if (x < (blackKeyOffsets[3] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[4];
					noteToPlay = "1gS";
				} else {
					lastShaded = white[5];
					noteToPlay = "1aN";
				}
			} else if (x < (WhiteKeyWidth * 6 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x < (blackKeyOffsets[4]) && y < (blackHeightY)) {
					lastShaded = black[4];
					noteToPlay = "1aS";
				} else {
					lastShaded = white[6];
					noteToPlay = "1bN";
				}
			}
		} else { // lower half of keyboard
			// if it's in C5
			if (x < (WhiteKeyWidth * 7 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[5] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[5];
					noteToPlay = "2cS";
				} else {
					lastShaded = white[7];
					noteToPlay = "2cN";
				}

			} else if (x < (WhiteKeyWidth * 8 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[6] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[6];
					noteToPlay = "2dS";
				} else if (x < (blackKeyOffsets[5] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[5];
					noteToPlay = "2cS";
					// Log.i("coord", " goes in this!!");
				} else {
					lastShaded = white[8];
					noteToPlay = "2dN";
				}
			} else if (x < (WhiteKeyWidth * 9 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x < (blackKeyOffsets[6] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[6];
					noteToPlay = "2dS";
				} else {
					lastShaded = white[9];
					noteToPlay = "2eN";
				}
			} else if (x < (WhiteKeyWidth * 10 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[7] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[7];
					noteToPlay = "2fS";
				} else {
					lastShaded = white[10];
					noteToPlay = "2fN";
				}
			} else if (x < (WhiteKeyWidth * 11 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[8] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[8];
					noteToPlay = "2gS";
				} else if (x < (blackKeyOffsets[7] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[7];
					noteToPlay = "2fS";
				} else {
					lastShaded = white[11];
					noteToPlay = "2gN";
				}
			} else if (x < (WhiteKeyWidth * 12 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[9] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[9];
					noteToPlay = "2aS";
				} else if (x < (blackKeyOffsets[8] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[8];
					noteToPlay = "2gS";
				} else {
					lastShaded = white[12];
					noteToPlay = "2aN";
				}
			} else if (x < (WhiteKeyWidth * 13 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x < (blackKeyOffsets[9] + BlackKeyWidth + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[9];
					noteToPlay = "2aS";
				} else {
					lastShaded = white[13];
					noteToPlay = "2bN";
				}
			}
		}
		if (x < halfwayPoint)
			ShadeOneNote(bufferCanvas, lastShaded, shade1);
		else
			ShadeOneNote(bufferCanvas, lastShaded, shade2);

		// Log.e("coord", " THE COORD I WANT IS : " + (WhiteKeyWidth * 6 +
		// (margin_val[0] + WhiteKeyWidth + BlackBorder)));
		bufferCanvas
				.translate(-(margin + BlackBorder), -(margin + BlackBorder));
		canvas.drawBitmap(bufferBitmap, 0, 0, paint);
		DrawNoteLetters(canvas);
		holder.unlockCanvasAndPost(canvas);

		if (wantTutCall)
			TutorialMSActivity.callBack(lastShaded);

		/** now play the note **/
		if (noteToPlay == "")
			return -1;
		else {
			// change from 1cS
			// soundPool.playNote("keya4"); format to do
			String key = "key";
			int number = Integer.parseInt(noteToPlay.charAt(0) + "");
			char letter = noteToPlay.charAt(1);
			String sharp = noteToPlay.charAt(2) + "";
			if (sharp.equals("N")) {
				sharp = "";
			} else if (sharp.equals("S"))
				sharp = "s";
			else
				Log.e("key", "what does sharp equal" + noteToPlay.charAt(2)
						+ "");
			key = key + letter + (number + 2) + sharp;
			// Log.e("noteToPlay",
			// "the note is " +
			// key);

			soundPool.playNote(key, 1);
			return lastShaded;
		}

	}

	private int getMyNote(float x, float y) {
		int[] black = { 25, 27, 30, 32, 34, 37, 39, 42, 44, 46 };
		int[] white = { 24, 26, 28, 29, 31, 33, 35, 36, 38, 40, 41, 43, 45, 47 };
		int halfwayPoint = (WhiteKeyWidth * 6 + (margin_val[0] + WhiteKeyWidth + BlackBorder));
		if (x < halfwayPoint) {
			// if it's in C4
			if (x < (margin_val[0] + WhiteKeyWidth + BlackBorder)) {
				if (x > (blackKeyOffsets[0] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[0];
				} else {
					lastShaded = white[0];
				}

			} else if (x < (WhiteKeyWidth + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[1] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[1];
				} else if (x < (blackKeyOffsets[0] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[1];
					// Log.i("coord", " goes in this!!");
				} else {
					lastShaded = white[1];
				}
			} else if (x < (WhiteKeyWidth * 2 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x < (blackKeyOffsets[1] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[1];
				} else {
					lastShaded = white[2];
				}
			} else if (x < (WhiteKeyWidth * 3 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[2] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[2];
					// Log.e("coord", "BLACKKEYSENSE 1st ");
				} /*
				 * else if (x < (blackKeyOffsets[1] + BlackBorder +
				 * BlackKeyWidth) && y < (blackHeightY)) { lastShaded =
				 * black[2]; ShadeOneNote(bufferCanvas, black[2], shade1);
				 * Log.e("coord", "BLACKKEYSENSE 2nd "); // Log.i("coord",
				 * " goes in this!!"); }
				 */else {
					lastShaded = white[3];
				}
			} else if (x < (WhiteKeyWidth * 4 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[3] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[3];
					// Log.e("coord", "BLACKKEYSENSE 1st ");
				} else if (x < (blackKeyOffsets[2] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[3];
					// Log.e("coord", "BLACKKEYSENSE 2nd ");
					// Log.i("coord", " goes in this!!");
				} else {
					lastShaded = white[4];
				}
			} else if (x < (WhiteKeyWidth * 5 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[4] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[4];
				} else if (x < (blackKeyOffsets[3] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[4];
				} else {
					lastShaded = white[5];
				}
			} else if (x < (WhiteKeyWidth * 6 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x < (blackKeyOffsets[4]) && y < (blackHeightY)) {
					lastShaded = black[4];
				} else {
					lastShaded = white[6];
				}
			}
		} else { // lower half of keyboard
			// if it's in C5
			if (x < (WhiteKeyWidth * 7 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[5] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[5];
				} else {
					lastShaded = white[7];
				}

			} else if (x < (WhiteKeyWidth * 8 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[6] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[6];
				} else if (x < (blackKeyOffsets[5] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[5];
					// Log.i("coord", " goes in this!!");
				} else {
					lastShaded = white[8];
				}
			} else if (x < (WhiteKeyWidth * 9 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x < (blackKeyOffsets[6] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[6];
				} else {
					lastShaded = white[9];
				}
			} else if (x < (WhiteKeyWidth * 10 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[7] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[7];
				} else {
					lastShaded = white[10];
				}
			} else if (x < (WhiteKeyWidth * 11 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[8] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[8];
				} else if (x < (blackKeyOffsets[7] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[7];
				} else {
					lastShaded = white[11];
				}
			} else if (x < (WhiteKeyWidth * 12 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x > (blackKeyOffsets[9] + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[9];
				} else if (x < (blackKeyOffsets[8] + BlackBorder + BlackKeyWidth)
						&& y < (blackHeightY)) {
					lastShaded = black[8];
				} else {
					lastShaded = white[12];
				}
			} else if (x < (WhiteKeyWidth * 13 + (margin_val[0] + WhiteKeyWidth + BlackBorder))) {
				if (x < (blackKeyOffsets[9] + BlackKeyWidth + BlackBorder)
						&& y < (blackHeightY)) {
					lastShaded = black[9];
				} else {
					lastShaded = white[13];
				}
			}
		}
		return lastShaded;

	}
	
    public static String[] beatArrss = {"beat1", "beat2", "beat3"};
	public void playBeat(int i)
	{
		soundPool.playNote(beatArrss[i], 1);
	}
	
	
	
	/*
	 * Shade the given note with the given brush. We only draw notes from
	 * notenumber 24 to 96. (Middle-C is 60).
	 */
	// private void ShadeLineNote(Canvas canvas, int notenumber, int color,
	// float x, float y) {
	// Log.e("Shade", " first here");
	// int octave = notenumber / 12;
	// int notescale = notenumber % 12;
	//
	// octave -= 2;
	// if (octave < 0 || octave >= MaxOctave) {
	// Log.e("Shade", " octave " + octave + " MaxOctave " + MaxOctave);
	// return;
	// }
	// paint.setColor(color);
	// paint.setStyle(Paint.Style.FILL);
	// canvas.translate(octave * WhiteKeyWidth * KeysPerOctave, 0);
	// int x1, x2, x3;
	//
	// int bottomHalfHeight = WhiteKeyHeight - (BlackKeyHeight + 3);
	//
	// /* notescale goes from 0 to 11, from C to B. */
	// switch (notescale) {
	// case 0: /* C */
	// canvas.drawLine(x, y, x, (y + 100), paint);
	// x1 = 2;
	// x2 = blackKeyOffsets[0] - 2;
	// canvas.drawRect(x1, 0, x1 + x2 - x1, 0 + BlackKeyHeight + 3, paint);
	// canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
	// BlackKeyHeight + 3 + bottomHalfHeight, paint);
	// break;
	// case 1: /* C# */
	// x1 = blackKeyOffsets[0];
	// x2 = blackKeyOffsets[1];
	// canvas.drawRect(x1, 0, x1 + x2 - x1, 0 + BlackKeyHeight, paint);
	// if (color == gray1) {
	// paint.setColor(gray2);
	// canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
	// + 1 + BlackKeyWidth - 2, BlackKeyHeight
	// - BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
	// }
	// break;
	// case 2: /* D */
	// x1 = WhiteKeyWidth + 2;
	// x2 = blackKeyOffsets[1] + 3;
	// x3 = blackKeyOffsets[2] - 2;
	// canvas.drawRect(x2, 0, x2 + x3 - x2, 0 + BlackKeyHeight + 3, paint);
	// canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
	// BlackKeyHeight + 3 + bottomHalfHeight, paint);
	// break;
	// case 3: /* D# */
	// x1 = blackKeyOffsets[2];
	// x2 = blackKeyOffsets[3];
	// canvas.drawRect(x1, 0, x1 + BlackKeyWidth, 0 + BlackKeyHeight,
	// paint);
	// if (color == gray1) {
	// paint.setColor(gray2);
	// canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
	// + 1 + BlackKeyWidth - 2, BlackKeyHeight
	// - BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
	// }
	// break;
	// case 4: /* E */
	// x1 = WhiteKeyWidth * 2 + 2;
	// x2 = blackKeyOffsets[3] + 3;
	// x3 = WhiteKeyWidth * 3 - 1;
	// canvas.drawRect(x2, 0, x2 + x3 - x2, 0 + BlackKeyHeight + 3, paint);
	// canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
	// BlackKeyHeight + 3 + bottomHalfHeight, paint);
	// break;
	// case 5: /* F */
	// x1 = WhiteKeyWidth * 3 + 2;
	// x2 = blackKeyOffsets[4] - 2;
	// x3 = WhiteKeyWidth * 4 - 2;
	// canvas.drawRect(x1, 0, x1 + x2 - x1, 0 + BlackKeyHeight + 3, paint);
	// canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
	// BlackKeyHeight + 3 + bottomHalfHeight, paint);
	// break;
	// case 6: /* F# */
	// x1 = blackKeyOffsets[4];
	// x2 = blackKeyOffsets[5];
	// canvas.drawRect(x1, 0, x1 + BlackKeyWidth, 0 + BlackKeyHeight,
	// paint);
	// if (color == gray1) {
	// paint.setColor(gray2);
	// canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
	// + 1 + BlackKeyWidth - 2, BlackKeyHeight
	// - BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
	// }
	// break;
	// case 7: /* G */
	// x1 = WhiteKeyWidth * 4 + 2;
	// x2 = blackKeyOffsets[5] + 3;
	// x3 = blackKeyOffsets[6] - 2;
	// canvas.drawRect(x2, 0, x2 + x3 - x2, 0 + BlackKeyHeight + 3, paint);
	// canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
	// BlackKeyHeight + 3 + bottomHalfHeight, paint);
	// break;
	// case 8: /* G# */
	// x1 = blackKeyOffsets[6];
	// x2 = blackKeyOffsets[7];
	// canvas.drawRect(x1, 0, x1 + BlackKeyWidth, 0 + BlackKeyHeight,
	// paint);
	// if (color == gray1) {
	// paint.setColor(gray2);
	// canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
	// + 1 + BlackKeyWidth - 2, BlackKeyHeight
	// - BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
	// }
	// break;
	// case 9: /* A */
	// x1 = WhiteKeyWidth * 5 + 2;
	// x2 = blackKeyOffsets[7] + 3;
	// x3 = blackKeyOffsets[8] - 2;
	// canvas.drawRect(x2, 0, x2 + x3 - x2, 0 + BlackKeyHeight + 3, paint);
	// canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
	// BlackKeyHeight + 3 + bottomHalfHeight, paint);
	// break;
	// case 10: /* A# */
	// x1 = blackKeyOffsets[8];
	// x2 = blackKeyOffsets[9];
	// canvas.drawRect(x1, 0, x1 + BlackKeyWidth, 0 + BlackKeyHeight,
	// paint);
	// if (color == gray1) {
	// paint.setColor(gray2);
	// canvas.drawRect(x1 + 1, BlackKeyHeight - BlackKeyHeight / 8, x1
	// + 1 + BlackKeyWidth - 2, BlackKeyHeight
	// - BlackKeyHeight / 8 + BlackKeyHeight / 8, paint);
	// }
	// break;
	// case 11: /* B */
	// x1 = WhiteKeyWidth * 6 + 2;
	// x2 = blackKeyOffsets[9] + 3;
	// x3 = WhiteKeyWidth * KeysPerOctave - 1;
	// canvas.drawRect(x2, 0, x2 + x3 - x2, 0 + BlackKeyHeight + 3, paint);
	// canvas.drawRect(x1, BlackKeyHeight + 3, x1 + WhiteKeyWidth - 3,
	// BlackKeyHeight + 3 + bottomHalfHeight, paint);
	// break;
	// default:
	// break;
	// }
	// canvas.translate(-(octave * WhiteKeyWidth * KeysPerOctave), 0);
	// }
}