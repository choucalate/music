package com.midisheetmusic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.model.RecManager;
import com.model.RecNotes;
import com.testmusic.MySimpleArrayAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;



public class SoonToBe extends SherlockListActivity {
	
	private RecManager rm;
	private String filename = "Rec1.txt";
	private String selectedValue;
	private String[] recKeys;
	private HashMap<String, ArrayList<RecNotes>> db;
	private Piano piano;
	private String elem;
	private boolean renam = false;
	private SPPlayer sp;
	private MySimpleArrayAdapter listadapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_soon_to_be);
		
		setTitle("My Jams");
		Log.i("recordList", "inside recordList activity");
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    rm = new RecManager(this);
	    setUpSound();
	    piano = new Piano(this,sp);
	    getDataBase();
	    Log.i("recordList", "recKeys size: " + recKeys.length);
	    listadapter = new MySimpleArrayAdapter(this, recKeys, true);
	    
	    setListAdapter(listadapter);
	    Log.i("recordList", "fill out ListView with Keys");
	    
	}
	private void getDataBase() {
		//super.onCreate(savedInstanceState);
		
        
		try {
			db = rm.getSerialized(filename);
			if (db == null){
				recKeys = new String [] {"No jams"};
			Log.i("recordList", "db empty (null)");
			}
			else
				recKeys = db.keySet().toArray(new String[0]);
			Log.i("recordList", "db not empty (null)");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			recKeys = new String [] {"No jams"};
			Log.i("recordList", "db empty (null)");
			e.printStackTrace();
		}
	}
	
	private void setUpSound() {
		// Log.e("SP", "FAILED ON ONCREATE");
		AssetManager am = getAssets();
		// activity only stuff
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		sp = new SPPlayer(am, audioManager);
	}
	
	@Override
	protected void onListItemClick(ListView parent, View view, int position,
			long id) {
		super.onListItemClick(parent, view, position, id);
		elem = recKeys[position];
		 Log.i("ListItem","element selected" + elem);
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
        	 if(elem == null)
        		 return true;
        	 piano.setMyRec(db.get(elem));
        	 piano.playBackAudioOnly(1);
        	 //piano.playBack(1);
        	 Log.i("recordList", "Play hit, playing" + elem);
        	return true;
         }

         case R.id.rename:
         {
        	 if(elem == null)
        		 return true;
        	 Log.i("recordList", "rename");
        	 AlertDialog.Builder alert = new AlertDialog.Builder(this);
     		 alert.setTitle("Rename your Jam");
     		 alert.setMessage(elem);

     		// Set an EditText view to get user input 
     		 final EditText input = new EditText(this);
     		 alert.setView(input);

     		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	     		public void onClick(DialogInterface dialog, int whichButton) {
	     		  String newnam = input.getText().toString();
	     		  rm.renamRec(newnam, elem);
	     		  renam = true;
	     		  
	     		  // Do something with value!
	     		}
     		});

     		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
     		  public void onClick(DialogInterface dialog, int whichButton) {
     		    // Canceled.
     		  }
     		});

     		alert.show();
     		 if(renam){
     			 
     			Toast.makeText(this, "Jam Renamed!", Toast.LENGTH_SHORT).show();
     			getDataBase();
     			listadapter.notifyDataSetChanged();
     		 }
        	return true;   	
         }


         case R.id.trash_icon:
         {
        	 if(elem == null)
        		 return true;
        	 rm.delRec(elem);
        	 Toast.makeText(this, "Jam Deleted!", Toast.LENGTH_SHORT).show();
        	 getDataBase();
        	 listadapter.notifyDataSetChanged();
        	 
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
