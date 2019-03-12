package com.cbrmm.autocaddy.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.cbrmm.autocaddy.R;


public class DevUtils {
	
	private final String TAG = "DevUtils";
	private final String NULL, EN, DIS, OFF, ON;
	
	public DevUtils(Context context) {
		NULL = context.getResources().getString(R.string.sett_null);
		EN = context.getResources().getString(R.string.sett_enabled);
		DIS = context.getResources().getString(R.string.sett_disabled);
		OFF = context.getResources().getString(R.string.sett_off);
		ON = context.getResources().getString(R.string.sett_on);
	}
	
	public void modNumber(TextView resource, int num) {
		if(num > 0 && num <= 100) {
			resource.setText(String.valueOf(num));
		} else if(num == 0){
			resource.setText(NULL);
		}
	}
	
	public void modSwitch(TextView resource, boolean en) {
		resource.setText(en ? ON:OFF);
	}
	
	public void modEnable(TextView resource, boolean en) {
		resource.setText(en ? EN:DIS);
	}
	
}
