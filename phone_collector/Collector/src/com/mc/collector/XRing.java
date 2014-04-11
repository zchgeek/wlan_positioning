package com.mc.collector;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;

public class XRing {
	/**
	 * 拥有众多播放铃声的函数
	 */
	//****************************************************************//
	//跟随系统的通知铃声
	public static void SystemRingtone_notification(Context context){
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		android.media.Ringtone rt = RingtoneManager.getRingtone(context, uri);
		if(rt != null)
		rt.play();
	}
}
