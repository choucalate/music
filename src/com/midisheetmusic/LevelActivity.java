package com.midisheetmusic;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LevelActivity extends ListActivity {
	ArrayList<String> levels = new ArrayList<String>();
	public final static String mSelected = "The Level";
	String[] values = new String[] { "Level 1: C-Major Scales & Practice" ,"Level 2: Twinkle Twinkle Little Stars",
			"Level 3: Mary had a Little Lamb", "Level 4: Justin BIEBS" };
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Choose A Level!");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView parent, View view, int position,
			long id) {
		super.onListItemClick(parent, view, position, id);
		// String item = (String) getListAdapter().getItem(position);

		Intent intent = new Intent(this, TutorialMSActivity.class);
		intent.putExtra(mSelected, values[position]);
		startActivity(intent);
		// Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.choose_song_menu, menu);
		return true;
	}

	public String getKey(String item) {
		if (item.equals("position"))
			return mSelected;
		else
			return "";
	}
	
	

}
