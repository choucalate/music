package com.tncmusicstudio;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
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

import com.model.TupleStringInt;
import com.testmusic.ListArrayAdapter;
import com.testmusic.MySimpleArrayAdapter;

import java.util.List;


public class AndroidDashboardDesignActivity extends SherlockFragmentActivity {
	AsyncTask<Void, ?, ?> wait;

	DrawerLayout mDrawerLayout;
	ListView mDrawerList;
	ActionBarDrawerToggle mDrawerToggle;
	ListArrayAdapter mMenuAdapter;
	String[] title, subtitle;
	int[] icon;

	public static final String HOME_FRAG_TAG = "HOME FRAGMENT";
	public static final String TUTORIAL_FRAG_TAG = "TUTORIAL FRAGMENT";
	public static final String PLAY_FRAG_TAG = "PLAYAROUND FRAGMENT";
	public static final String BEATS_FRAG_TAG = "BEATS FRAGMENT";
	public static final String SOON_FRAG_TAG = "SOON FRAGMENT";
	public static final String MIC_FRAG_TAG = "MIC FRAGMENT";


	Fragment fragment1 = new HomeFragment();
	Fragment fragment2 = new TutorialMSActivity();
	Fragment fragment3 = new PlayAroundActivity();
	Fragment fragment4 = new Beats_Activity();
	Fragment fragment5 = new SoonToBe();
	Fragment fragment6 = new Mic_Test();
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);    
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

		setContentView(R.layout.drawer_main);

		setTitle("Town and Country");

		title = new String[] {"Home","Tutorial", "Piano", "Beatbox","My Beats","Mic"};
		icon = new int[]{R.drawable.home32, R.drawable.tutorial_icon, R.drawable.piano_icon,
		       R.drawable.beats_icon, R.drawable.recnote_icon, R.drawable.mic_icon};
		mDrawerLayout =(DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.listview_drawer);
		mMenuAdapter = new ListArrayAdapter(AndroidDashboardDesignActivity.this, title, icon);
		mDrawerList.setAdapter(mMenuAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.transp_y));
		//getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_menu_white_24dp, R.string.drawer_open, R.string.drawer_close){
			public void onDrawerClosed(View view){
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

			}
			public void onDrawerOpened(View view){
				super.onDrawerOpened(view);
				getSupportActionBar().setTitle(mDrawerTitle);
				//requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if(savedInstanceState == null)
			selectItem(0);
	}

	/*Handle key presses

	*/
    public Fragment getCurFragment() {
        List<Fragment> myFrags = getSupportFragmentManager().getFragments();
        for(int i = 0; i< myFrags.size(); i++) {
            if(myFrags.get(i) != null && myFrags.get(i).isVisible()) {
                return myFrags.get(i);
            }
        }
        return null;
    }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment f = getCurFragment();
        Log.i("key", "KEY DOWN FRAG");
        if (f == null) {
            Log.i("key", "NULL FRAG");
            return false;
            // add your code here
        }
        /*
        public static final String HOME_FRAG_TAG = "HOME FRAGMENT";
	public static final String TUTORIAL_FRAG_TAG = "TUTORIAL FRAGMENT";
	public static final String PLAY_FRAG_TAG = "PLAYAROUND FRAGMENT";
	public static final String BEATS_FRAG_TAG = "BEATS FRAGMENT";
	public static final String SOON_FRAG_TAG = "SOON FRAGMENT";
	public static final String MIC_FRAG_TAG = "MIC FRAGMENT";
         */
        if(f.getTag()== HOME_FRAG_TAG) {

        } else if(f.getTag() == TUTORIAL_FRAG_TAG) {
        } else if(f.getTag() == PLAY_FRAG_TAG) {
            ((PlayAroundActivity) f).onKeyDown(keyCode, event);
        } else if(f.getTag() == BEATS_FRAG_TAG) {
            ((Beats_Activity) f).onKeyDown(keyCode, event);

        } else if(f.getTag() == SOON_FRAG_TAG) {

        } else if(f.getTag() == MIC_FRAG_TAG) {
            ((Mic_Test) f).onKeyDown(keyCode, event);

        }

		// play the sound based on the hashmap from keyCode to note

		return false;
	}
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Fragment f = getCurFragment();
        Log.i("key", "KEY UP FRAG");
        if (f == null) {
            Log.i("key", "NULL FRAG");
            return false;
            // add your code here
        }
        if(f.getTag()== HOME_FRAG_TAG) {

        } else if(f.getTag() == TUTORIAL_FRAG_TAG) {
        } else if(f.getTag() == PLAY_FRAG_TAG) {
            ((PlayAroundActivity) f).onKeyUp(keyCode, event);
        } else if(f.getTag() == BEATS_FRAG_TAG) {
            ((Beats_Activity) f).onKeyUp(keyCode, event);

        } else if(f.getTag() == SOON_FRAG_TAG) {

        } else if(f.getTag() == MIC_FRAG_TAG) {
//            ((Mic_Test) f).onKeyUp(keyCode, event);

        }
        // play the sound based on the hashmap from keyCode to note

        return false;
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// the .png file makes it appear with the yellow underline, but if we want it completely
//	    // removed, setBackgroundDrawable(null)
//
//		// Inflate the menu; this adds items to the action bar if it is present.
//       // getSupportMenuInflater().inflate(R.menu.dashboard_menu, menu);
//        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.transp_y));
//
//        return true;
//	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		//menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}


	public boolean onOptionsItemSelected(MenuItem item){
		if (item.getItemId() == android.R.id.home) {

			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}

		return super.onOptionsItemSelected(item);
		/*if(R.id.tutorial_icon==item.getItemId()){
			
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
		else if(item.getItemId()==R.id.mic_icon){
			Intent i = new Intent(this,Mic_Test.class);
			startActivity(i);
			return true;
		}*/
		//return false;
		
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// Locate Position
		switch (position) {
			case 0:
				ft.replace(R.id.content_frame, fragment1, HOME_FRAG_TAG); // Home
				break;
			case 1:
				ft.replace(R.id.content_frame, fragment2, TUTORIAL_FRAG_TAG); // Tutorial
				break;
			case 2:
				ft.replace(R.id.content_frame, fragment3, PLAY_FRAG_TAG); // Piano
				break;
			case 3:
				ft.replace(R.id.content_frame, fragment4, BEATS_FRAG_TAG); // Beats
				break;
			case 4:
				ft.replace(R.id.content_frame, fragment5, SOON_FRAG_TAG); // Recordings
				break;
			case 5:
				ft.replace(R.id.content_frame, fragment6, MIC_FRAG_TAG); // Mic
				break;
		}
		ft.commit();
		mDrawerList.setItemChecked(position, true);

		// Get the title followed by the position
		setTitle(title[position]);
		// Close drawer
		mDrawerLayout.closeDrawer(mDrawerList);
		//this.invalidateOptionsMenu();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
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