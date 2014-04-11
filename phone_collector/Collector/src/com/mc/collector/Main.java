package com.mc.collector;

import java.util.List;
import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Main extends Activity {
	XWifi wifi;
	XIO io;
	XTimer timer;
	CheckBox wifi_state;
	ImageView btn;
	ProgressBar bar;
	NumberPicker np;
	TextView pt_selected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		wifi_state.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					wifi.enable_wifi();
				}else{
					wifi.disable_wifi();
				}
			}
		});
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int delay = 3000;
				int interval = 1000;
				int times = 500;
				final String pid = "pt"+np.getValue();
				if(XIO.exist(pid)){
					XIO.delete(pid);
				}
				pt_selected.setText("²É¼¯ÖÐ");
				timer = new XTimer() {
					@Override
					public void notice() {
						np.setEnabled(true);
						bar.setProgress(0);
						XRing.SystemRingtone_notification(Main.this);
						XVibr.vibrate(Main.this, 1500);
					}
					@Override
					public void execute() {
						int prog = progress();
						if(prog/5 != (prog-1)/5){
							bar.setProgress(prog/5);
						}
						List<ScanResult> result_lst = wifi.scan_result();
						String out = new String();
						for(int i=0;i<result_lst.size();i++){
							ScanResult result = result_lst.get(i);
							out += result.BSSID+"@"+String.valueOf(result.level)+" ";
						}
						io.write(pid, pid+"\t"+out+"\n", true);
					}
				};
				timer.Timer_DIT(delay, interval, times);
			}
		});
	}
	
	public void init(){
		wifi = new XWifi(this);
		io = new XIO(this);
		
		pt_selected = (TextView)findViewById(R.id.pt_selected);
		np = (NumberPicker)findViewById(R.id.x_coord);
		PickerAdapter adapter = new PickerAdapter(pt_selected);
		np.setFormatter(adapter);  
		np.setOnValueChangedListener(adapter);  
		np.setOnScrollListener(adapter);  
		np.setMaxValue(50);  
		np.setMinValue(0);  
		((EditText) np.getChildAt(1)).setOnFocusChangeListener(null);
		
		btn = (ImageView)findViewById(R.id.collect);
		bar = (ProgressBar)findViewById(R.id.progress);
		bar.setMax(100);
		wifi_state = (CheckBox)findViewById(R.id.wifi_enabled);
		if(wifi.is_wifi_enabled()){
			wifi_state.setChecked(true);
		}else{
			wifi_state.setChecked(false);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.exit(0);
	}
	
}
