package com.mc.collector;

import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import android.widget.NumberPicker.OnScrollListener;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

public class PickerAdapter implements OnValueChangeListener, Formatter, OnScrollListener{
	public TextView pt;
	public PickerAdapter(TextView pt){
		this.pt = pt;
	}
	@Override
	public void onScrollStateChange(NumberPicker view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String format(int value) {
		// TODO Auto-generated method stub
		return "Pt "+value;
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// TODO Auto-generated method stub
		pt.setText("当前选中：pt"+String.valueOf(newVal));
	}
	
}
