package com.midisheetmusic;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Beats_Activity extends SherlockActivity {
	boolean check = true ; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beats_);
		setTitle("Beats");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.beats_, menu);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(223,160,23)));
	     
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
		if(16908332 == item.getItemId())
        
        {
        	Intent i=new Intent(this, AndroidDashboardDesignActivity.class);
        	startActivity(i);
        	return true	;
        }
		else if (item.getItemId() == R.id.piano_icon)
        {
        	Intent i=new Intent(this, PlayAroundActivity.class);
        	startActivity(i);
        	return true;
        }
		else if (item.getItemId() == R.id.loop_icon)
        {
        	return true;
        }
		else if (item.getItemId() == R.id.toggle){
        	if(check)
        	{
        		//change to beats 
        		item.setTitle(R.string.toggleBeats);
        		setTitle("Beats");
        	    check = false; 

        	}
        	else
        	{
        		item.setTitle(R.string.toggleShots);
        		setTitle("One Shots");
        		check = true; ; 

        	}
        	return true;
        }
    
		else return false;
	}
}
