package com.mc.collector;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;

public class XIO {
	static String defpath = Environment.getExternalStorageDirectory().getPath()+"/";
	Context context;
	public XIO(Context context){
		this.context = context;
	}
	
	static boolean exist(String path, String name){
		File file = new File(path+name);
		return file.exists();
	}
	
	static boolean exist(String name){
		return exist(defpath,name);
	}
	
	static boolean delete(String path, String name){
		File file = new File(path+name);
		return file.delete();
	}
	
	static boolean delete(String name){
		return delete(defpath, name);
	}
	
	static
	
	public void write(String path, String name, String content, boolean append){
		try {
			File file = new File(path+name);
			FileOutputStream fos = new FileOutputStream(file,append);
			fos.write(content.getBytes());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void write(String name, String content, boolean append){
		write(defpath, name, content, append);
	}
}
