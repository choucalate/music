package com.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.util.Log;

public class RecManager {
	Context mCtx;
    private String filename = "Rec1.txt";
	public RecManager(Context ctx) {
		mCtx = ctx;
	}

	// here we set the thing to be serialized to a file
//	public void setSerialized(String fileName, ArrayList<ArrayList<RecNotes>> c)
//			throws IOException {
//		Context context = this.mCtx;
//		FileOutputStream fos;
//		ObjectOutputStream os = null;
//		try {
//			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//			os = new ObjectOutputStream(fos);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			Log.e("winecontroller", " exception in here");
//			e.printStackTrace();
//		}
//		os.writeObject(c);
//		os.close();
//	}
 
	//HashMap implementation
	public void setSerialized(String fileName, HashMap<String , ArrayList<RecNotes>> c)
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
	/*public ArrayList<ArrayList<RecNotes>> getSerialized(String fileName) throws IOException {
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
	}*/
	
	//HashMap implementation
	
	public HashMap<String, ArrayList<RecNotes>> getSerialized(String fileName) throws IOException {
		Context context = this.mCtx;
		FileInputStream fis = context.openFileInput(fileName);
		ObjectInputStream is = new ObjectInputStream(fis);
		HashMap<String, ArrayList<RecNotes>> simpleClass = null;
		try {
			simpleClass = (HashMap<String, ArrayList<RecNotes>>) is.readObject();
			//can this^^be a problem???
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(IOException io) {
			io.printStackTrace();
		}
		is.close();
		return simpleClass;
	}
	
	public HashMap<String, ArrayList<RecNotes>> loadRec(){
		//String name = ""+rn.size();
		//HashMap<String, ArrayList<RecNotes>> db = new HashMap<String, ArrayList<RecNotes>>();
		try {
			HashMap<String, ArrayList<RecNotes>> rn = getSerialized(filename);
			Set<String> rnkeys = rn.keySet();
			for (String st : rnkeys)
				Log.i("option", "printing rn: "+ st + " " +rn.get(st).toString());
			return rn;	
		}catch (Exception e){
			e.printStackTrace();
			return new HashMap<String, ArrayList<RecNotes>>();
		}
	}
	
	public void saveRec(ArrayList<RecNotes> newrec) {
		HashMap<String, ArrayList<RecNotes>> database = new HashMap<String, ArrayList<RecNotes>>();
		try {
			Log.e("serialize", "starting serializing");
			database = getSerialized(filename);
            String dfnam = "MyJam " + (database.size() + 1); 
			/*if (rn == null) {
				Log.i("option", "rn was null, making new");
				rn = new HashMap<String, ArrayList<RecNotes>>();
			}*/	
			database.put(dfnam, newrec);
			setSerialized(filename, database);
			Log.e("serialize", "finished serializing");
		} catch (IOException e) {
//			if (database == null) {
//				Log.i("option", "rn was null, making new");
//				database = new HashMap<String, ArrayList<RecNotes>>();
//			}
			database = new HashMap<String, ArrayList<RecNotes>>();
			String dfnam = "MyJam " + (database.size() + 1);
			database.put(dfnam, newrec);
			try {
				setSerialized(filename, database);
			} catch (IOException e1) {
				Log.e("option", "failed to even set serialize??");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			Log.e("option", "failed to get serialized");
			e.printStackTrace();
		}
	}
	
	//update
		public void renamRec (String newnam, String oldnam){
			try {
				HashMap<String, ArrayList<RecNotes>> rn = getSerialized(filename); 
				ArrayList<RecNotes> record_cp = rn.get(oldnam);   //save recording
				rn.remove(oldnam); // remove recording
				rn.put(newnam, record_cp); //add recording with new name
				
				setSerialized(filename, rn);
			} catch (IOException e1) {
				Log.e("option", "failed to even set serialize??");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		//delete
		public void delRec (String target){
			
			try {
				HashMap<String, ArrayList<RecNotes>> rn = getSerialized(filename); 
				rn.remove(target);	
				setSerialized(filename, rn);
			} catch (IOException e1) {
				Log.e("option", "failed to even set serialize??");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
}
