package wlan_positioning.client;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.net.wifi.*;
import android.view.*;
import android.widget.*;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;

public class Client extends Activity
{
    Wifi wifi;
    Handler handler;
    TextView v_show;
    Button v_start;
    EditText v_ip;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	
	//------------------------init--------------------
	wifi = new Wifi(this);
	handler = new Handler();
	v_show = (TextView)findViewById(R.id.show);
	v_start = (Button)findViewById(R.id.start);
	v_ip = (EditText)findViewById(R.id.ip);

	wifi.enable();
	v_start.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Sender(wifi, v_ip.getText().toString()).start();
		}
	    });
	new Receiver(handler, v_show).start();
	
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
}

class Sender extends Thread{
    String ip;
    Wifi wifi;
    public Sender(Wifi wifi, String ip){
	this.ip = ip;
	this.wifi = wifi;
    }

    public void run(){
	super.run();
	List<ScanResult> result_lst = wifi.scan();
	String entry = "{";
	for(int i=0;i<result_lst.size();++i){
	    entry += "\""+result_lst.get(i).BSSID+"\""+":"+result_lst.get(i).level;
	    if(i != result_lst.size()-1)
		entry+=",";
	    else
		entry+="}";
	}
	try{
	    Socket s = new Socket(ip,5672);
	    OutputStream os = s.getOutputStream();
	    os.write(entry.getBytes());
	    os.close();
	    s.close();
	} catch(Exception e){
	    e.printStackTrace();
	}
    }
}

class Receiver extends Thread{
    Handler handler;
    TextView v_show;
    ServerSocket ss;
    String content;
    public Receiver(Handler handler, TextView v_show){
	this.handler = handler;
	this.v_show = v_show;
	content = "";
    }

    public void run(){
	super.run();
	listen();
    }

    public void listen(){
	try{
	    ss = new ServerSocket(5674);
	}catch(Exception e){
	    e.printStackTrace();
	}

	while(true){
	    try{
		Socket s = ss.accept();
		InputStream is = s.getInputStream();
		byte[] buffer = new byte[1024];
		is.read(buffer);
		is.close();
		content += " "+new String(buffer).trim();
		System.out.println("++++++++++++++++++++++++++++++++"+content);
		Runnable update_ui = new Runnable(){
			public void run(){
			    v_show.setText(content);
			}
		    };
		handler.post(update_ui);
	    } catch(Exception e){
		e.printStackTrace();
	    }
	}
    }

}