package com.cbrmm.autocaddy.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.util.AC_Location;
import com.cbrmm.autocaddy.util.Bluetooth;
import com.cbrmm.autocaddy.util.BluetoothCallback;
import com.cbrmm.autocaddy.util.BluetoothService;
import com.cbrmm.autocaddy.util.Data;
import com.cbrmm.autocaddy.util.DeviceCallback;
import com.cbrmm.autocaddy.util.DiscoveryCallback;

import java.util.Arrays;
import java.util.Objects;


//TODO Investigate Hub/Control merge
public class Hub extends AppCompatActivity {
	
	private static final String TAG = ":Hub:";
	private final static boolean DEBUG = false;
	
	protected static boolean isUserHeightRecv = false;
	
	public static Data dataModel = new Data("Control");
	
	public static AC_Location locationAssistant;
	
	private final boolean DEV = false;
	private final int DEV_WINDOW = 2000;
	private final int DEV_CNT = 6;
	
	private int mDevCount = 0;
	private long mDevTime = 0;
	
	private Button btnConnect;
	private Button btnHelp;
	private Button btnDev;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hub);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		initUI();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!DEBUG) {
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
		}
		
		return super.onTouchEvent(event);
	}
	
	private void initUI() {
		
		btnConnect = findViewById(R.id.hub_btn_connect);
		btnHelp = findViewById(R.id.hub_btn_help);
		btnDev = findViewById(R.id.hub_btn_dev);
		
		setClickListeners();
		
		if(!DEBUG) btnDev.setVisibility(View.INVISIBLE);
	}
	
	private void setClickListeners() {
		OnClickListener mClickListener = v -> {
			switch(v.getId()) {
				case R.id.hub_btn_connect:
					startControl();
					break;
				case R.id.hub_btn_help:
					startHelp();
					break;
				case R.id.hub_btn_dev:
					startDev();
					break;
			}
		};
		
		btnConnect.setOnClickListener(mClickListener);
		btnHelp.setOnClickListener(mClickListener);
		btnDev.setOnClickListener(mClickListener);
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
	
	private void setVisibleDev() {
		if(mDevCount == 0) {
			btnDev.setVisibility(btnDev.getVisibility() == View.INVISIBLE ? View.INVISIBLE : View.VISIBLE);
		} else if(mDevCount >= DEV_CNT) {
			btnDev.setVisibility(btnDev.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
			mDevTime = 0;
		}
	}
	
	private void makeToast(final String message) {
		this.runOnUiThread(() -> Toast.makeText(Hub.this, message, Toast.LENGTH_SHORT).show());
	}
}
