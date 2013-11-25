package com.midisheetmusic;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;

import com.testmusic.MySimpleArrayAdapter;

public class LevelActivity extends ListActivity {
	ArrayList<String> levels = new ArrayList<String>();
	public final static String mSelected = "The Level";
	String[] values = new String[] { "Level 1: C-Major Scales & Practice" ,"Level 2: Twinkle Twinkle Little Stars",
			"Level 3: Mary had a Little Lamb", "Level 4: Justin BIEBS", "Level 5: D-Major Scales & Practice" , "Level 6: G-Major Scales & Practice" , 
			"Level 7: A-Major Scales & Practice", "Level 8: E-Major Scales & Practice", "Level 9: F#-Major Scales & Practice", 
			"Level 10: D Flat-Major Scales & Practice", "Level 11: A Flat-Major Scales & Practice", 
			"Level 12: E Flat-Major Scales & Practice", "Level 13: B Flat-Major Scales & Practice", "Level 14: F-Major Scales & Practice", 
			"Level 15: B-Major Scales & Practice", "Level 16: Thrift Shop"};
	public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, values);
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
