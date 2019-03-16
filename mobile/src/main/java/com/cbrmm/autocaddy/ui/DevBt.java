package com.cbrmm.autocaddy.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.cbrmm.autocaddy.R;

import java.util.ArrayList;
import java.util.Set;


public class DevBt extends AppCompatActivity {
	
	private BluetoothAdapter adapter;
	
	private Spinner spinDevices;
	private EditText editRead, editWrite;
	private RadioButton rbLight;
	private Button btnConnect, btnRead, btnWrite;
	
	private ArrayAdapter<String> aaDevices;
	private ArrayList<BluetoothDevice> devices = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev);
		
		initUI();
		
		adapter = BluetoothAdapter.getDefaultAdapter();
		btGetPairedDevices();
	}
	
	private void initUI() {
		
		spinDevices = findViewById(R.id.devbt_spin_devices);
		
		editRead = findViewById(R.id.devbt_edit_read);
		editWrite = findViewById(R.id.devbt_edit_write);
		
		rbLight = findViewById(R.id.devbt_radio_light);
		
		btnConnect = findViewById(R.id.devbt_btn_connect);
		btnRead = findViewById(R.id.devbt_btn_read);
		btnWrite = findViewById(R.id.devbt_btn_write);
		
		aaDevices = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
		aaDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinDevices.setAdapter(aaDevices);
		
		setClickListeners();
	}
	
	private void setClickListeners() {
		View.OnClickListener mOnClickListener = v -> {
			switch(v.getId()) {
				case R.id.devbt_btn_connect:
					//TODO Bt connect, light on
					break;
				case R.id.devbt_btn_read:
					//TODO Bt read
					break;
				case R.id.devbt_btn_write:
					//TODO Bt write
					break;
			}
		};
		
		View.OnLongClickListener mOnLongClickListener = v -> {
			switch(v.getId()) {
				case R.id.btn_connect:
					//TODO Bt disconnect, light off
					return true;
				default:
					return false;
			}
		};
		
		btnConnect.setOnClickListener(mOnClickListener);
		btnRead.setOnClickListener(mOnClickListener);
		btnWrite.setOnClickListener(mOnClickListener);
		
		btnConnect.setOnLongClickListener(mOnLongClickListener);
	}
	
	private void btGetPairedDevices() {
		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		if(pairedDevices.size() > 0) {
			for(BluetoothDevice device : pairedDevices) {
				aaDevices.add(device.getName());
				devices.add(device);
			}
		}
	}
}
