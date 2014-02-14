package com.midisheetmusic;

import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.model.RecManager;
import com.model.RecNotes;

public class recordListActivity extends ListActivity {
	
	private Context context;
	private RecManager rm;
	private String filename;
	private String selectedValue;
	
//	public recordListActivity(Context context, RecManager rm, String filenam){
//    	this.context = context;
//    	this.rm = rm;
//    	filename = filenam;
//    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        HashMap<String, ArrayList<RecNotes>> db;
		try {
			db = rm.getSerialized(filename);
		
        String[] recKeys = (String[])db.keySet().toArray();
		// no more this
		// setContentView(R.layout.list_fruit);
        
		setListAdapter(new ArrayAdapter<String>(this, R.layout.recordslist_layout,recKeys));
 
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
        
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    // When clicked, show a toast with the TextView text
				//Context me = getApplicationContext();
				//me.getText(position);
				//CharSequence name = ((TextView) view)).getText();
			    //Toast.makeText(getApplicationContext(),((TextView) view).getText(), Toast.LENGTH_SHORT);
			    selectedValue = (String) getListAdapter().getItem(position);
			    Log.i("ListView List","item selected" + selectedValue);
			}
		});
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
