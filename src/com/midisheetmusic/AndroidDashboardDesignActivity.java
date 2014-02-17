package com.midisheetmusic;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

import android.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


public class AndroidDashboardDesignActivity extends SherlockActivity {
	AsyncTask<Void, ?, ?> wait;

	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);    
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

		setContentView(R.layout.dashboard_layout);

		setTitle("Town and Country");
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// the .png file makes it appear with the yellow underline, but if we want it completely 
	    // removed, setBackgroundDrawable(null)
		
		// Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.dashboard_menu, menu);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.transp_y));
        return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		if(R.id.tutorial_icon==item.getItemId()){
			
			Intent i= new Intent(this,LevelActivity.class );
			startActivity(i);
			return true;
		}
		else if(item.getItemId()==R.id.recnote_icon){
			Intent i = new Intent(this, SoonToBe.class);
			startActivity(i);
			return true;
		}
		else if(item.getItemId()==R.id.beats_icon){
			Intent i = new Intent(this, Beats_Activity.class);
			startActivity(i);
			return true;
		}
		else if(item.getItemId()==R.id.pianointro_icon){
			Intent i = new Intent(this,PlayAroundActivity.class);
			startActivity(i);
			return true;
		}
		return false;
		
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);


	}


	public class waitUp extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			try {
				Log.e("async", "pre");
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Log.e("async", "post");
			Intent i = new Intent(getApplicationContext(), LevelActivity.class);
			startActivity(i);
		}
		
	}
}