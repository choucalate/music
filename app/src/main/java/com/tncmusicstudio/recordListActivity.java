package com.tncmusicstudio;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.tncmusicstudio.R;
import com.model.RecManager;
import com.model.RecNotes;
import com.testmusic.MySimpleArrayAdapter;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class recordListActivity extends SherlockListActivity {
	
	//private Context context;
	private RecManager rm;
	private String filename;
	private String selectedValue;
	private String[] recKeys;
	private HashMap<String, ArrayList<RecNotes>> db;
	private Piano piano;
	private String elem;
	private boolean renam = false;
//	public recordListActivity(Context context, RecManager rm, String filenam){
//    	this.context = context;
//    	this.rm = rm;
//    	filename = filenam;
//    }
	//sherlock bar
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_soon_to_be);
		setTitle("My Jams");
		
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		//setTitle("Music Studio");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//Intent intent = getIntent();
		getDataBase();
				
		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, recKeys);
	    setListAdapter(adapter);

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
		 Log.i("MenuItem","item selected" + selectedValue);
		 //on next iteration the item selected should be highlighted
		
		 switch (item.getItemId()) {

		 case 16908332:
    
	     {
	    	 Intent i=new Intent(this, AndroidDashboardDesignActivity.class);
        	 startActivity(i);
        	return true	;
	     }

         case R.id.play_icon :
         {
            //do stuff 
        	 piano.setMyRec(db.get(elem));
        	//String play = db.get((recKeys[item.]));
        	return true;
         }

         case R.id.rename:
         {
        	 //alertdialog
        	 //String newnam = "";
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
     		 if(renam)
     			Toast.makeText(this, "Jam Renamed!", Toast.LENGTH_SHORT).show();

        	 
        	return true;   	
         }


         case R.id.trash_icon:
         {
        	 rm.delRec(elem);
        	 Toast.makeText(this, "Jam Deleted!", Toast.LENGTH_SHORT).show();

        	 return true;
         }
//         case R.id.piano_icon:
//         {
//        	 Intent i = new Intent(this, PlayAroundActivity.class);
//        	 startActivity(i);
//        	 return true;
//         }

         default:{
         //insert something
         }
         return false; 

		 }
	 }

	//@Override
	private void getDataBase() {
		//super.onCreate(savedInstanceState);
		
        
		try {
			db = rm.getSerialized(filename);
			recKeys = (String[])db.keySet().toArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			recKeys = new String [] {"No jams"};
			e.printStackTrace();
		}
	}
}
