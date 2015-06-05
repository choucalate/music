package com.tncmusicstudio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
//import android.view.MenuInflater;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import com.testmusic.ListArrayAdapter;
import com.tncmusicstudio.R;
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

public class SoonToBe extends SherlockListFragment{//Activity {

	private RecManager rm;
	private String filename = "Rec1.txt";
	private String selectedValue;
	private String[] recKeys;
	private HashMap<String, ArrayList<RecNotes>> db;
	//private Piano piano;
	private String elem;
	private boolean renam = false;
	private static SPPlayer sp;
	private ListArrayAdapter listadapter;
	static ArrayList<RecNotes> myRec;
	static int offset = 0;
	//private static SPPlayer soundPool;
	static Timer time;
	//private String origin = "_PIANO";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_soon_to_be);
		setHasOptionsMenu(true);
		//setTitle("My Jams");
		Log.i("recordList", "inside recordList activity");
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		rm = new RecManager(getActivity().getApplicationContext());
		setUpSound();
		//piano = new Piano(this, sp);
		getDataBase();
		Log.i("recordList", "recKeys size: " + recKeys.length);
		listadapter = new ListArrayAdapter(getActivity().getApplicationContext(), recKeys, true);

		setListAdapter(listadapter);
		Log.i("recordList", "fill out ListView with Keys");

	}

	private void getDataBase() {
		// super.onCreate(savedInstanceState);

		try {
			db = rm.getSerialized(filename);
			if (db == null || db.keySet().size() ==0) {
				recKeys = new String[] { "No jams" };
				Log.i("recordList", "db empty (null)");
			} else
				recKeys = db.keySet().toArray(new String[0]);
			Log.i("recordList", "db not empty (null)");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			recKeys = new String[] { "No jams" };
			Log.i("recordList", "db empty (null)");
			e.printStackTrace();
		}
	}

	private void setUpSound() {
		// Log.e("SP", "FAILED ON ONCREATE");
		AssetManager am = getActivity().getAssets();
		// activity only stuff
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		sp = new SPPlayer(am, audioManager);
	}

	@Override
	public void onListItemClick(ListView parent, View view, int position,
			long id) {
		super.onListItemClick(parent, view, position, id);
		elem = recKeys[position];
		Log.i("ListItem", "element selected" + elem);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu, inflater);
		//menu.clear();
		inflater.inflate(R.menu.soon_to_be, menu);
		//getActivity().getActionBar().show();
		//getActivity().getActionBar().setBackgroundDrawable(
				//new ColorDrawable(Color.rgb(223, 160, 23)));
		//return true;
	}


//	@Override
//	public void onPrepareOptionsMenu(Menu menu) {
//		selectMenu(menu);
//	}
//
//	private void selectMenu(Menu menu) {
//		//getActivity().getMenuInflater().inflate(R.menu.soon_to_be, menu);
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

//		case 16908332:
//
//		{
//			Intent i = new Intent(this, AndroidDashboardDesignActivity.class);
//			startActivity(i);
//			return true;
//		}

		case R.id.play_icon: {
			// do stuf
			if (elem == null || elem.equals("No jams"))
				return true;
			myRec = db.get(elem);
			playBack(1);
			// piano.playBack(1);
			Log.i("recordList", "Play hit, playing" + elem);
			return true;
		}

		case R.id.rename: {
			if (elem == null)
				return true;
			Log.i("recordList", "rename");
			AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Rename your Jam");
			alert.setMessage(elem);

			// Set an EditText view to get user input
			final EditText input = new EditText(getActivity());
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String[] tokens = elem.split("[_]");
							String newnam = input.getText().toString() + "_" + tokens[1];
							rm.renamRec(newnam, elem);
							renam = true;
							if (renam) {

								Toast.makeText(getActivity(),
										"Jam Renamed!", Toast.LENGTH_SHORT)
										.show();
								getDataBase();
								listadapter.changeArray(recKeys);
								listadapter.notifyDataSetChanged();
							}
							// Do something with value!
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});

			alert.show();

			return true;
		}

		case R.id.trash_icon: {
			if (elem == null)
				return true;
			rm.delRec(elem);
			Toast.makeText(getActivity().getApplicationContext(), "Jam Deleted!", Toast.LENGTH_SHORT).show();
			getDataBase();
			listadapter.changeArray(recKeys);

			listadapter.notifyDataSetChanged();

			return true;
		}
//		case R.id.piano_icon: {
//			Intent i = new Intent(this, PlayAroundActivity.class);
//			startActivity(i);
//			return true;
//		}

		default: {
			// insert something
		}
			return false;

		}
	}
	public static void playBack(int speed) {

		time = new Timer();
		Calendar mycal = Calendar.getInstance();
		Calendar copy = Calendar.getInstance();

		Log.i("recstart", "size: " + myRec.size());

		for (int i = 0; i < myRec.size(); i++) {

			mycal = copy;
			if (i == 0) {
				mycal.add(Calendar.MILLISECOND, (int) (myRec.get(i)
						.getCurrTime() + offset));
			} else {
				mycal.add(Calendar.MILLISECOND, (int) (myRec.get(i)
						.getCurrTime() + offset - myRec.get(i - 1)
						.getCurrTime()));
			}
			Log.i("recstart",
					"going to play it at: "
							+ (mycal.getTimeInMillis() - copy.getTimeInMillis())
							+ " with the offset: "
							+ (int) myRec.get(i).getCurrTime());
			final int index = i;
			time.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stubx`
					if (!myRec.get(index).isBeat()) {

						Log.i("recstart",
								"about to play note: "
										+ myRec.get(index).getNoteToPlay());
						sp.playNote(myRec.get(index).getNoteToPlay(), 1);

					} else {
						// is beat
						playBeat(myRec.get(index).getBeat());
						Log.i("recstart", "is a beat! playing beat: "
								+ myRec.get(index).getBeat());
					}
				}

			}, mycal.getTime());
		}
	}

	public static String[] beatArrss = { "beat1", "beat2", "beat3", "beat4",
		"beat5", "beat6", "clap", "snare", "oneshot3", "oneshot4",
		"oneshot5", "oneshot6" };

	public static void playBeat(int i) {
		sp.playNote(beatArrss[i], 1);
	}

}
