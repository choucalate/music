package com.midisheetmusic;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class AndroidDashboardDesignActivity extends Activity {
	Facebook facebook;
	AsyncTask<Void, ?, ?> wait;
	AsyncFacebookRunner mFacebookRunner;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("create", "NOF ACEBOOK");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dashboard_layout);
		wait = new waitUp();
		wait.execute((Void) null);
		/*
		 * facebook = new Facebook("544056102317137"); mFacebookRunner = new
		 * AsyncFacebookRunner(facebook); // facebook.setAccessToken(token);
		 * facebook.setAccessToken(facebook.getAccessToken()); if
		 * (facebook.isSessionValid()) { Log.e("async", "session valid"); wait =
		 * new waitUp(); wait.execute((Void) null); } else {
		 * facebook.authorize(this, new String[] { "publish_actions" }, new
		 * DialogListener() {
		 * 
		 * @Override public void onComplete(Bundle values) { Log.e("async",
		 * "fb"); wait = new waitUp(); wait.execute((Void) null); }
		 * 
		 * @Override public void onFacebookError(FacebookError error) {
		 * 
		 * Log.e("async", "fberr" + error); wait = new waitUp();
		 * wait.execute((Void) null); }
		 * 
		 * @Override public void onError(DialogError e) { Log.e("async", "err");
		 * wait = new waitUp(); wait.execute((Void) null); }
		 * 
		 * @Override public void onCancel() { Log.e("async", "cancel"); wait =
		 * new waitUp(); wait.execute((Void) null); } }); }
		 */
		/*
		 * LinearLayout layout = (LinearLayout) findViewById(R.id.home_root);
		 * Bitmap bmImg = decodeSampledBitmapFromResource(getResources(),
		 * R.drawable.pianobckgd1, 768, 469); BitmapDrawable background = new
		 * BitmapDrawable(bmImg); int sdk = android.os.Build.VERSION.SDK_INT; if
		 * (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
		 * layout.setBackgroundDrawable(background); } else {
		 * layout.setBackground(background); }
		 */
		/*
		 * setContentView(layout); // player.SetPiano(piano);
		 * layout.requestLayout();
		 */
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

		facebook.authorizeCallback(requestCode, resultCode, data);

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