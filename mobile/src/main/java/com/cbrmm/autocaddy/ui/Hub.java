package com.cbrmm.autocaddy.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.util.BluetoothService;
import com.cbrmm.autocaddy.util.Data;


//TODO Investigate Hub/Control merge
public class Hub extends AppCompatActivity {
	
	private static final String TAG = ":Hub:";
	
	protected static BluetoothService btService;
	
	public static Data dataModel = new Data("Control"); //TODO public static is not ideal
	
	private final boolean DEV = false;
	private final int DEV_WINDOW = 2000;
	private final int DEV_CNT = 6;
	
	private int mDevCount = 0;
	private long mDevTime = 0;
	
	private TextView txtVersion;
	
	private Button btnConnect;
	private Button btnHelp;
	private Button btnDev;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hub);
		
		btService = BluetoothService.getBluetoothService();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		initUI();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(btnDev != null) {
			if(event.getAction() == MotionEvent.ACTION_UP) {
				long time = System.currentTimeMillis();
				
				if(mDevTime == 0 || time - mDevTime > DEV_WINDOW) {
					mDevCount = 0;
					mDevTime = time;
				} else {
					mDevCount++;
				}
				
				setVisibleDev();
			}
		}
		
		return super.onTouchEvent(event);
	}
	
	private void initUI() {
		
		txtVersion = findViewById(R.id.txt_dev_version);
		
		btnConnect = findViewById(R.id.btn_connect);
		btnHelp = findViewById(R.id.btn_help);
		btnDev = findViewById(R.id.btn_dev);
		
		setClickListeners();
	}
	
	private void setClickListeners() {
		OnClickListener mClickListener = v -> {
			switch(v.getId()) {
				case R.id.btn_connect:
					startControl();
					break;
				case R.id.btn_help:
					startHelp();
					break;
				case R.id.btn_dev:
					startDev();
					break;
			}
		};
		
		View.OnLongClickListener mLongClickListener = v -> {
			switch(v.getId()) {
				case R.id.btn_dev:
					startDevBt();
					return true;
				default:
					return false;
			}
		};
		
		btnConnect.setOnClickListener(mClickListener);
		btnHelp.setOnClickListener(mClickListener);
		btnDev.setOnClickListener(mClickListener);
		
		btnDev.setOnLongClickListener(mLongClickListener);
	}
	
	private void startControl() {
		startActivity(new Intent(this, Control.class));
	}
	
	private void startHelp() {
		startActivity(new Intent(this, Help.class));
	}
	
	private void startDev() {
		startActivity(new Intent(this, Dev.class));
	}
	
	private void startDevBt() {
		startActivity(new Intent(this, DevBt.class));
	}
	
	private void setVisibleDev() {
		if(mDevCount == 0) {
			btnDev.setVisibility(btnDev.getVisibility() == View.INVISIBLE ? View.INVISIBLE : View.VISIBLE);
			txtVersion.setVisibility(txtVersion.getVisibility() == View.INVISIBLE ? View.INVISIBLE : View.VISIBLE);
		} else if(mDevCount >= DEV_CNT) {
			btnDev.setVisibility(btnDev.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
			txtVersion.setVisibility(txtVersion.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
			mDevTime = 0;
		}
	}
}
