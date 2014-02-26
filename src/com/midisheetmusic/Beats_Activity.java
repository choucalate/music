package com.midisheetmusic;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
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
    private SPPlayer sp; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beats_);
		setTitle("Beats");
	    setUpSound(); 

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     //   final MediaPlayer sound1; 
      //  final MediaPlayer sound2; 
       // final MediaPlayer sound3;
       // final MediaPlayer sound4; 
       // sound1= MediaPlayer.create(this,R.raw.beat1);
       // sound2= MediaPlayer.create(this,R.raw.beat2);
       // sound3= MediaPlayer.create(this,R.raw.snare);
       // sound4= MediaPlayer.create(this,R.raw.clap1);

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

         
         
        // Listening to News Feed button click
        beat1.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	System.out.println("INSIDE button1 ");

            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		playBeat(6);
            	}
            	else {
            		playBeat(0);
            	}
            }
        });
         
       // Listening Friends button click
        beat2.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	System.out.println("INSIDE button2 ");

            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		playBeat(7);
            		
            	}
            	else 
            		//sound2.start();
            		playBeat(1);
            
            }
        });
         
        // Listening Messages button click
        beat3.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	System.out.println("INSIDE button3 ");
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		playBeat(8);
            	}
            	else 
        		playBeat(2);

            }
        });
         
        // Listening to Places button click
        beat4.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		playBeat(9);
            	}
            	else 
            		playBeat(3);
           
            }
        });
         
        // Listening to Events button click
        beat5.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		playBeat(10);
            	}
            	else 
            		playBeat(4);
            }	
        });
         
        // Listening to Photos button click
        beat6.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
            	if( mymenu.getItem(1).getTitle().equals("One Shots")){
            		playBeat(11);
            	}
            	else 
            		playBeat(5);
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
        		//item.getItemId(R.id.loop_icon).setVisible(false);
        		//((Menu) item).getItem(1).setVisible(false);
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
	
	private void setUpSound() {
		// Log.e("SP", "FAILED ON ONCREATE");
		AssetManager am = getAssets();
		// activity only stuff
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		this.sp = new SPPlayer(am, audioManager);
	}
	public static String[] beatArrss = { "beat1", "beat2", "beat3", "beat4", "beat5", "beat6",
		"clap","snare", "oneshot3", "oneshot4", "oneshot5", "oneshot6" };

	public void playBeat(int i) {
		sp.playNote(beatArrss[i], 1);
}
}
