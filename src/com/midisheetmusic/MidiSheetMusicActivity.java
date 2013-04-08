/*
 * Copyright (c) 2011-2012 Madhav Vaidyanathan
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

import com.testmusic.SoundPoolExample;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.content.res.*;
import android.content.res.Resources.Theme;
import android.graphics.*;

/**
 * @class MidiSheetMusicActivity This is the launch activity for MidiSheetMusic.
 *        It simply displays the splash screen, and a button to choose a song.
 */
public class MidiSheetMusicActivity extends Activity {
	public static int THEME = R.style.MyAppTheme;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(THEME);
		loadImages();
		setContentView(R.layout.main);
		Button tutorial = (Button) findViewById(R.id.Tutorials);
		tutorial.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				chooseLevel();
			}
		});
		Button compose = (Button) findViewById(R.id.Compose_Choice);
		compose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				chooseSong();
			}
		});
	}
	private void chooseLevel() {
		Intent intent = new Intent(this, LevelActivity.class);
		startActivity(intent);
	}
	/** Start the ChooseSongActivity when the "Choose Song" button is clicked */
	private void chooseSong() {
		Intent intent = new Intent(this, SoundPoolExample.class);
		startActivity(intent);
	}

	/** Load all the resource images */
	private void loadImages() {
		ClefSymbol.LoadImages(this);
		TimeSigSymbol.LoadImages(this);
		MidiPlayer.LoadImages(this);
	}

	/** Always use landscape mode for this activity. */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
