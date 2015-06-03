package com.testmusic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tncmusicstudio.R;

/**
 * Created by Fabian on 5/29/2015.
 */
public class ListArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private String[] values;
    private int[] icon;
    private final String[] entries;
    private final boolean listRec;

    public ListArrayAdapter(Context context, String[] values, int[] icon) {
        super(context, R.layout.listviewtxtimg, values);
        this.context = context;
        this.values = values;
        this.listRec = false;
        this.entries = null;
        this.icon = icon;
    }

    public ListArrayAdapter(Context context, String[] entries,
                                boolean listRec) {
        super(context, R.layout.listviewtxtimg, entries);
        this.context = context;
        this.values = entries;
        this.listRec = listRec;
        this.entries = null;
    }

    public void changeArray(String[] vals) {
        this.values = vals;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listviewtxtimg, parent, false);
        rowView.setMinimumHeight(150);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        Log.i("MyList", "position: " + position);
        for (int i = 0; i < values.length; i++) {
            Log.i("MyList", "values: " + values[i]);

        }
        if (position >= values.length)
            return null;
        textView.setText(values[position]);
        if (listRec) {
            // Change the icon for Windows and iPhone
            String s = values[position];

//            s = s.substring(6, s.indexOf(":"));
//            Log.i("tutlevel", "new tutlvl" + s);
//            int tutLevel = Integer.parseInt(s);
//            Log.i("tutlevel", "parsed tutlvl" + s);
            // if (tutLevel != 1)
            // imageView.setImageResource(R.drawable.locked);
            // else
            imageView.setImageResource(R.drawable.musical_note);
        }
        else
            imageView.setImageResource(icon[position]);
        return rowView;
    }
}
