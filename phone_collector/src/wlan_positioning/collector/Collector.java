package wlan_positioning.collector;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.content.Context;
import android.media.RingtoneManager;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import java.io.FileOutputStream;
import android.widget.*;
import android.net.wifi.*;
import android.net.Uri;
import android.view.View;

/*
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import java.util.List;
*/


public class Collector extends Activity implements View.OnClickListener, NumberPicker.OnValueChangeListener
{
    //--------widget-----------
    CheckBox state;
    ImageView btn;
    ProgressBar bar;
    NumberPicker np;
    TextView pt_slct;
    //--------param------------
    int delay = 3000;
    int interval = 1000;
    int times = 500;
    int cnt = 0;
    //--------viriable---------
    Wifi wifi;
    static String path = Environment.getExternalStorageDirectory().getPath()+"/";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	//----------------------------------variable-----------------------------
	wifi = new Wifi(this);
	pt_slct = (TextView)findViewById(R.id.pt_selected);
	btn = (ImageView)findViewById(R.id.collect);
	bar = (ProgressBar)findViewById(R.id.progress);
	state = (CheckBox)findViewById(R.id.wifi_enabled);
	//------------------------------------init-------------------------------
	bar.setMax(times);
	
	btn.setOnClickListener(this);

	np = (NumberPicker)findViewById(R.id.x_coord);
	np.setMaxValue(50);
	np.setMinValue(0);
	np.setOnValueChangedListener(this);

	if(wifi.is_enabled())
	    state.setChecked(true);
	else
	    state.setChecked(false);
	state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
		    if(isChecked)
			wifi.enable();
		    else
			wifi.disable();
		}
	    });
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
	pt_slct.setText("pt"+newVal);
    }

    public void onClick(View v){
	final String pid = "selected: pt"+np.getValue();
	cnt = 0;
	bar.setProgress(cnt);

        TimerTask task = new TimerTask(){
    	    public void run(){
		cnt++;
		bar.setProgress(cnt);
		List<ScanResult> result_lst = wifi.scan();
		String line = new String();
		for(int i=0;i<result_lst.size();++i){
		    ScanResult result = result_lst.get(i);
		    line += result.BSSID+"@"+String.valueOf(result.level)+" ";
		}
		line = pid+"\t"+line+"\n";
    		try{
		    File file = new File(path+pid);
		    FileOutputStream fos = new FileOutputStream(file,true);
    		    fos.write(line.getBytes());
		    fos.close();		    
    		}catch(Exception e){
    		    e.printStackTrace();
    		}
    		if(cnt>=times){
    		    this.cancel();
		    //----------------notify vibrate--------------------
		    Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		    vibrator.vibrate(1000);
		    //----------------notify ring-----------------------
		    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		    android.media.Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
		    if(rt != null)
			rt.play();
    		}
    	    }
    	};
		
        new Timer().schedule(task, delay, interval);
    }
}

class Wifi{
    private WifiManager mgr;
    public Wifi(Context context){
	mgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }
    public boolean is_enabled(){
	return mgr.isWifiEnabled();
    }
    public void enable(){
	mgr.setWifiEnabled(true);
    }
    public void disable(){
	mgr.setWifiEnabled(false);
    }
    public List<ScanResult> scan(){
	mgr.startScan();
	return mgr.getScanResults();
    }
    public String[] get_ssid(List<ScanResult> result){
	String [] ssids = new String[result.size()];
	for(int i=0;i<result.size();++i){
	    ssids[i] = result.get(i).SSID;
	}
	return ssids;
    }
    public String[] get_bssid(List<ScanResult> result){
	String [] bssids = new String[result.size()];
	for(int i=0;i<result.size();++i){
	    bssids[i] = result.get(i).BSSID;
	}
	return bssids;
    }
    public int[] get_level(List<ScanResult> result){
	int [] levels = new int[result.size()];
	for(int i=0;i<result.size();++i){
	    if(result.get(i)!=null)
		levels[i]=result.get(i).level;
	    else
		levels[i] = -100;
	}
	return levels;
    }
}