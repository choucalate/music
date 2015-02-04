//package com.midisheetmusic;
//
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.actionbarsherlock.app.SherlockActivity;
//import com.actionbarsherlock.view.Menu;
//import com.larswerkman.holocolorpicker.ColorPicker;
//import com.larswerkman.holocolorpicker.ColorPicker.OnColorChangedListener;
//import com.larswerkman.holocolorpicker.OpacityBar;
//import com.larswerkman.holocolorpicker.SVBar;
//
//public class SettingsActivity extends SherlockActivity implements
//		OnColorChangedListener {
//	private ColorPicker picker;
//	private SVBar svBar;
//	private OpacityBar opacityBar;
//	private Button button;
//	private TextView text;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_settings);
//		setTitle("Piano Color Picker");
//		picker = (ColorPicker) findViewById(R.id.picker);
//		svBar = (SVBar) findViewById(R.id.svbar);
//		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
//		button = (Button) findViewById(R.id.button1);
//		text = (TextView) findViewById(R.id.textView1);
//		
//		picker.addSVBar(svBar);
//		picker.addOpacityBar(opacityBar);
//		picker.setOnColorChangedListener(this);
//
//		button.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				text.setTextColor(picker.getColor());
//				picker.setOldCenterColor(picker.getColor());
//			}
//		});
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getSupportMenuInflater().inflate(R.menu.settings, menu);
//		getSupportActionBar().setBackgroundDrawable(
//				new ColorDrawable(Color.rgb(223, 160, 23)));
//		return true;
//	}
//
//	@Override
//	public void onColorChanged(int color) {
//		// TODO Auto-generated method stub
//
//	}
//
// }
