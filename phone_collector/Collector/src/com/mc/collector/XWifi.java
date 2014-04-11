package com.mc.collector;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class XWifi {
	private WifiManager mgr;
	public XWifi(Context context){
		mgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
	}
	public boolean is_wifi_enabled(){
		return mgr.isWifiEnabled();
	}
	public void enable_wifi(){
		mgr.setWifiEnabled(true);
	}
	public void disable_wifi(){
		mgr.setWifiEnabled(false);
	}
	public List<ScanResult> scan_result(){
		mgr.startScan();
		return mgr.getScanResults();
	}
	static public List<ScanResult> filter(List<ScanResult> scan_results,String [] items){
		List<ScanResult> results = new ArrayList<ScanResult>();
		String [] BSSIDs = get_bssid(scan_results);
		for(int i=0;i<items.length;i++){
			for(int j=0;j<BSSIDs.length;j++){
				if(BSSIDs[j].equals(items[i])){
					results.add(scan_results.get(j));
					System.out.println(scan_results.get(j).level);
				}
			}
			if(results.size()==i) results.add(null);
		}
		return results;
	}
	static public String [] get_ssid(List<ScanResult> result){
		String [] ssids = new String[result.size()];
		for(int i=0;i<result.size();i++){
			ssids[i] = result.get(i).SSID;
		}
		return ssids;
	}
	static public String [] get_bssid(List<ScanResult> result){
		String [] bssids = new String[result.size()];
		for(int i=0;i<result.size();i++){
			bssids[i] = result.get(i).BSSID;
		}
		return bssids;
	}
	static public int[] get_level(List<ScanResult> results){
		int [] levels = new int[results.size()];
		for(int i=0;i<levels.length;i++){
			if(results.get(i)!=null)
				levels[i] = results.get(i).level;
			else
				levels[i] = -120;
		}
		return levels;
	}
	
}
