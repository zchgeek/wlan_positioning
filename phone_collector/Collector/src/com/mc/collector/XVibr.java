package com.mc.collector;

import android.content.Context;
import android.os.Vibrator;

public class XVibr {
	public static void vibrate(Context context,long time){
		Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(time);
	}
}
