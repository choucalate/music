package com.tncmusicstudio;

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

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.model.RecManager;
import com.model.RecNotes;
import com.testmusic.MySimpleArrayAdapter;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SoonToBe extends SherlockListActivity {

	private RecManager rm;
	private String filename = "Rec1.txt";
	private String selectedValue;
	private String[] recKeys;
	private HashMap<String, ArrayList<RecNotes>> db;
	//private Piano piano;
	private String elem;
	private boolean renam = false;
	private static SPPlayer sp;
	private MySimpleArrayAdapter listadapter;
	static ArrayList<RecNotes> myRec;
	static int offset = 0;
	//private static SPPlayer soundPool;
	static Timer time;
	//private String origin = "_PIANO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_soon_to_be);

		setTitle("My Jams");
		Log.i("recordList", "inside recordList activity soontobe");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		rm = new RecManager(this);
		setUpSound();
		//piano = new Piano(this, sp);
		getDataBase();
		Log.i("recordList", "recKeys size: " + recKeys.length);
		listadapter = new MySimpleArrayAdapter(this, recKeys, true);

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
		Log.i("ListItem", "element selected" + elem);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.soon_to_be, menu);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.rgb(223, 160, 23)));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case 16908332:

		{
			Intent i = new Intent(this, AndroidDashboardDesignActivity.class);
			startActivity(i);
			return true;
		}

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
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Rename your Jam");
			alert.setMessage(elem);

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
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

								Toast.makeText(getApplicationContext(),
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
			Toast.makeText(this, "Jam Deleted!", Toast.LENGTH_SHORT).show();
			getDataBase();
			listadapter.changeArray(recKeys);

			listadapter.notifyDataSetChanged();

			return true;
		}
		case R.id.piano_icon: {
			Intent i = new Intent(this, PlayAroundActivity.class);
			startActivity(i);
			return true;
		}
            case R.id.sync_icon: {
                Log.i("SYNC", "syncing to web app: " + elem);
                if (elem == null) return true;

                Log.i("recordList", "opening alert dialog");
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Sending Jam To Website... Enter Your Username");
                alert.setMessage(elem);

                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                alert.setView(input);

                alert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                String username = input.getText().toString();

                                Log.i("recordList", "sending data with username: " + username);
                                sendJamToServer(username, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        // If the response is JSONObject instead of expected JSONArray
                                        Toast.makeText(getApplicationContext(),
                                                "Jam SUCCEEDED!", Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                        Toast.makeText(getApplicationContext(),
                                                "Jam SUCCEEDED...!", Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        Toast.makeText(getApplicationContext(),
                                                "Jam FAILED!", Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                        Toast.makeText(getApplicationContext(),
                                                "Jam FAILED...!", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });

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


                // use the elem that gets chosen here to be synced

            }
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

       /*
       Creates the entire http stuff to send to the server
       this sends it in a json like this:
       {
        "song_title": String,
        "user_name": String,
        "data" : [
            "sound": String,
            "offset": Number
        ]
       }

        */
    public void sendJamToServer(String username, JsonHttpResponseHandler handler) {
        ArrayList<RecNotes> recNotesToConfigure = db.get(elem);
        RequestParams params = new RequestParams();
        params.put("song_title", elem);
        params.put("user_name", username);
        List<Map<String, Object>> data = new ArrayList<Map<String,
                Object>>();
        for(int i = 0; i < recNotesToConfigure.size(); i++) {
            Map<String, Object> sample = new HashMap<String, Object>();
            RecNotes rn = recNotesToConfigure.get(i);
            if (rn.isBeat()) {
                sample.put("sound", beatArrss[rn.getBeat()] + ".ogg");
            } else {
                sample.put("sound", SPPlayer.keyArrayToOgg(rn.getNoteToPlay()));
            }
            sample.put("offset", rn.getCurrTime());
            Log.i("sending data", sample.toString());
            data.add(sample);
        }
        params.put("data", data);
        ServerRestClient.post("send_jam", params, handler);
    }

}
