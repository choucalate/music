package com.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class RecManager {
	Context mCtx;

	public RecManager(Context ctx) {
		mCtx = ctx;
	}

	// here we set the thing to be serialized to a file
	public void setSerialized(String fileName, ArrayList<ArrayList<RecNotes>> c)
			throws IOException {
		Context context = this.mCtx;
		FileOutputStream fos;
		ObjectOutputStream os = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			os = new ObjectOutputStream(fos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("winecontroller", " exception in here");
			e.printStackTrace();
		}
		os.writeObject(c);
		os.close();
	}

	public ArrayList<ArrayList<RecNotes>> getSerialized(String fileName) throws IOException {
		Context context = this.mCtx;
		FileInputStream fis = context.openFileInput(fileName);
		ObjectInputStream is = new ObjectInputStream(fis);
		ArrayList<ArrayList<RecNotes>> simpleClass = null;
		try {
			simpleClass = (ArrayList<ArrayList<RecNotes>>) is.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException io) {
			io.printStackTrace();
		}
		is.close();
		return simpleClass;
	}
}
