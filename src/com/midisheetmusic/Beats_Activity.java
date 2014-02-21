package com.midisheetmusic;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Beats_Activity extends SherlockActivity {
	boolean check = true ;
	Menu mymenu; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beats_);
		setTitle("Beats");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final MediaPlayer sound1; 
        final MediaPlayer sound2; 
        final MediaPlayer sound3;
        final MediaPlayer sound4; 
        sound1= MediaPlayer.create(this,R.raw.beat1);
        sound2= MediaPlayer.create(this,R.raw.beat2);
        sound3= MediaPlayer.create(this,R.raw.snare);
        sound4= MediaPlayer.create(this,R.raw.clap1);

        /**
         * Creating all buttons instances
         * */
        // Dashboard News feed button
        Button beat1 = (Button) findViewById(R.id.button1);
         
        // Dashboard Friends button
        Button beat2 = (Button) findViewById(R.id.button2);
         
        // Dashboard Messages button
        Button beat3 = (Button) findViewById(R.id.button3);
         
        // Dashboard Places button
        Button beat4 = (Button) findViewById(R.id.button4);
         
        // Dashboard Events button
        Button beat5 = (Button) findViewById(R.id.button5);
         
        // Dashboard Photos button
        Button beat6 = (Button) findViewById(R.id.button6);
        // Dashboard Photos button
        Button beat7 = (Button) findViewById(R.id.button7);
         
        // Dashboard Photos button
        Button beat8 = (Button) findViewById(R.id.button8);
         
         
        // Listening to News Feed button click
        beat1.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		sound3.start();
            	}
            	else {
            		sound1.start();
            	}
            }
        });
         
       // Listening Friends button click
        beat2.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		sound4.start();
            		
            	}
            	else 
            		sound2.start();
            
            }
        });
         
        // Listening Messages button click
        beat3.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		sound3.start();
            	}
            	else 
            		sound1.start();

            }
        });
         
        // Listening to Places button click
        beat4.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		sound4.start();
            	}
            	else 
            		sound2.start();
           
            }
        });
         
        // Listening to Events button click
        beat5.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		sound3.start();
            	}
            	else 
            		sound1.start();
            }	
        });
         
        // Listening to Photos button click
        beat6.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		sound4.start();
            	}
            	else 
            		sound2.start();
            }
        });
        
        // Listening to Photos button click
        beat7.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if(mymenu.getItem(1).getTitle().equals("One Shots")){
            		sound3.start();
            	}
            	else 
            		sound1.start();
         
            }
        });
        
        // Listening to Photos button click
        beat8.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		sound4.start();
            	}
            	else 
            		sound2.start();
         
            }
        });
	}
    

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.beats_, menu);
		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.rgb(144,140,140)));
	    mymenu= menu;
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
        		setTitle("One Shots");
        	    check = false; 

        	}
        	else
        	{
        		item.setTitle(R.string.toggleShots);
        		setTitle("Beats");
        		check = true; ; 

        	}
        	return true;
        }
    
		else return false;
	}
}
