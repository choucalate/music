package com.testmusic;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.midisheetmusic.R;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
	private final ArrayList<String> entries;
	private final boolean listRec;
	
	public MySimpleArrayAdapter(Context context, String[] values) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
		this.values = values;
		this.listRec = false;
		this.entries = null;
	}
	

	public MySimpleArrayAdapter(Context context, ArrayList<String> entries, boolean listRec) {
		super(context, R.layout.rowlayout, entries);
		this.context = context;
		this.values = null;
		this.listRec = listRec;
		this.entries = entries;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		rowView.setMinimumHeight(90);
		
		TextView textView = (TextView) rowView.findViewById(R.id.label);
//		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		
		textView.setText(values[position]);
		if(!listRec){
		// Change the icon for Windows and iPhone
			String s = values[position];
			
			s = s.substring(6, s.indexOf(":"));
			Log.i("tutlevel", "new tutlvl" + s);
			int tutLevel = Integer.parseInt(s);
			Log.i("tutlevel", "parsed tutlvl" + s);
	//		if (tutLevel != 1)
	//			imageView.setImageResource(R.drawable.locked);
	//		else
	//			imageView.setImageResource(R.drawable.unlocked);
		}
//		else{
//			textView.set
//		}
		return rowView;
	}
}
