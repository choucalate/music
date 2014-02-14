package com.midisheetmusic;

import com.actionbarsherlock.R.drawable;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class Beats_Activity extends SherlockActivity {
	boolean check = true ; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beats_);
		setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.beats_, menu);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(54,127,63)));
	     
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item )
	{
        switch (item.getItemId()) {
        case 16908332:
        
        {
        	Intent i=new Intent(this, AndroidDashboardDesignActivity.class);
        	startActivity(i);
        	return true	;
        }
        case R.id.piano_icon:
        {
        	Intent i=new Intent(this, PlayAroundActivity.class);
        	startActivity(i);
        	return true;
        }
        case R.id.loop_icon:
        {
        	return true;
        }
        case R.id.toggle:{
        	if(check)
        	{
        		//change to beats 
        		item.setIcon(R.drawable.beats_letters);
        	    check = false; 

        	}
        	else
        	{
        		item.setIcon(R.drawable.one_shots);
             //   item.setTitle(
        		check = true; ; 

        	}
        	return true;
        }
    
        default:
        	return false;  
        }
	}
}
