package com.midisheetmusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.midisheetmusic.R;

public class AndroidDashboardDesignActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dashboard_layout);

		/**
		 * Creating all buttons instances
		 * */
		// Dashboard News feed button
		Button btn_newsfeed = (Button) findViewById(R.id.btn_news_feed);

		// Dashboard Friends button
		Button btn_friends = (Button) findViewById(R.id.btn_friends);

		// Dashboard Messages button
		Button btn_messages = (Button) findViewById(R.id.btn_messages);

		// Dashboard Places button
		Button btn_places = (Button) findViewById(R.id.btn_places);

		// Dashboard Events button
		Button btn_events = (Button) findViewById(R.id.btn_events);

		// Dashboard Photos button
		Button btn_photos = (Button) findViewById(R.id.btn_photos);

		/**
		 * Handling all button click events
		 * */

		// Listening to News Feed button click
		btn_newsfeed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(),
						LevelActivity.class);
				startActivity(i);
			}
		});

		// Listening Friends button click
		btn_friends.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(),
						ChooseSongActivity.class);
				startActivity(i);
			}
		});

		// Listening Messages button click
		btn_messages.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(),
						SettingsActivity.class);
				startActivity(i);
			}
		});

		// Listening to Places button click
		btn_places.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				Intent i = new Intent(getApplicationContext(),
						TutorialMSActivity.class);
				startActivity(i);
			}
		});

		// Listening to Events button click
		btn_events.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				// Intent i = new Intent(getApplicationContext(),
				// EventsActivity.class);
				// startActivity(i);
			}
		});

		// Listening to Photos button click
		btn_photos.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching News Feed Screen
				// Intent i = new Intent(getApplicationContext(),
				// PhotosActivity.class);
				// startActivity(i);
			}
		});
	}
}