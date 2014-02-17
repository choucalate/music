package com.midisheetmusic;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;


public class SoonToBe extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soon_to_be);
		setTitle("My Jams");

	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.soon_to_be, menu);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(223,160,23)));
		return true;
	}
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {

		 case 16908332:
    
	     {
	    	 Intent i=new Intent(this, AndroidDashboardDesignActivity.class);
        	 startActivity(i);
        	return true	;
	     }

         case R.id.play_icon :
         {
            //do stuf 
        	return true;
         }

         case R.id.rename:
         {
        	return true;   	
         }


         case R.id.trash_icon:
         {
        	 return true;
         }
         case R.id.piano_icon:
         {
        	 Intent i = new Intent(this, PlayAroundActivity.class);
        	 startActivity(i); 
        	 return true;
         }

         default:{
         //insert something
         }
         return false; 

		 }
	 }

}
