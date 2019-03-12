package com.cbrmm.autocaddy.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.util.BluetoothService;
import com.cbrmm.autocaddy.util.Data;
import com.cbrmm.autocaddy.util.DevUtils;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class Dev extends AppCompatActivity implements BluetoothService.OnPassBtData{
	
	private final String TAG = "Dev";
	private final int DISCOVER_PERIOD = 60;
	
	private DevUtils utils;
	private Data model;
	
	private BluetoothService service;
	BluetoothAdapter btAdapter;
	
	private View.OnClickListener onClickListener;
	private View.OnLongClickListener onLongClickListener;
	private AdapterView.OnItemSelectedListener onItemSelectedListener;
	
	private ScheduledFuture<?> scheduledComm;
	private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	
	ArrayList<BluetoothDevice> btAADevices = new ArrayList<>();
	ArrayAdapter<String> btAA;
	
	private Spinner spinG1, spinG2, spinG3, spinBt;
	private EditText editG1, editBtMsg;
	private Switch swG2, swG3;
	private Button buttonG1, buttonG2, buttonG3, buttonReturn, buttonBtDiscover, buttonBtPair, buttonBtRead, buttonBtWrite;
	private TextView vtxt1, vtxt2, vtxt3, vtxt4, vtxt5, vtxt6, vtxt7, vtxt8, txtBT;
	
	private boolean isBtEnReturn, isBtEn;
	private int btAAPos = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev);
		
		utils = new DevUtils(this);
		model = Control.dataModel;
		if(model == null) {
			model = Control.initExternalData("Dummy Model");
			makeToast("Warning: Model is null, using dummy.", Toast.LENGTH_SHORT);
		}
		
		initUI();
		
		registerReceiver(btReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		
		service = BluetoothService.getBluetoothService();
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		btAdapter.startDiscovery();
		BluetoothService.btEnable(btAdapter, this);
		btGetPairedDevices();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(Control.dataModel != null) {
			model = Control.dataModel;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == BluetoothService.REQUEST_ENABLE_BT) {
			isBtEnReturn = true;
			isBtEn = resultCode == RESULT_OK;
			if(!isBtEn) Log.w(TAG, "Bluetooth failed to enable.");
		}
	}
	
	private void initUI() {
		
		final String[] g1 = {Control.validSettings[0], Control.validSettings[1]};
		final String[] g2 = {Control.validSettings[2], Control.validSettings[3], Control.validSettings[4]};
		final String[] g3 = {Control.validSettings[5], Control.validSettings[6], Control.validSettings[7]};
		
		spinG1 = findViewById(R.id.dev_spin_sett_g1);
		spinG2 = findViewById(R.id.dev_spin_sett_g2);
		spinG3 = findViewById(R.id.dev_spin_sett_g3);
		spinBt = findViewById(R.id.dev_spin_bt);
		
		editBtMsg = findViewById(R.id.dev_edit_bt_msg);
		editG1 = findViewById(R.id.dev_edit_sett_g1);
		swG2 = findViewById(R.id.dev_switch_sett_g2);
		swG3 = findViewById(R.id.dev_switch_sett_g3);
		
		buttonG1 = findViewById(R.id.dev_button_sett_g1);
		buttonG2 = findViewById(R.id.dev_button_sett_g2);
		buttonG3 = findViewById(R.id.dev_button_sett_g3);
		buttonReturn = findViewById(R.id.dev_button_return);
		buttonBtDiscover = findViewById(R.id.dev_button_bt_discover);
		buttonBtPair = findViewById(R.id.dev_button_bt_pair);
		buttonBtRead = findViewById(R.id.dev_button_bt_read);
		buttonBtWrite = findViewById(R.id.dev_button_bt_write);
		
		vtxt1 = findViewById(R.id.dev_vtxt_sett1);
		vtxt2 = findViewById(R.id.dev_vtxt_sett2);
		vtxt3 = findViewById(R.id.dev_vtxt_sett3);
		vtxt4 = findViewById(R.id.dev_vtxt_sett4);
		vtxt5 = findViewById(R.id.dev_vtxt_sett5);
		vtxt6 = findViewById(R.id.dev_vtxt_sett6);
		vtxt7 = findViewById(R.id.dev_vtxt_sett7);
		vtxt8 = findViewById(R.id.dev_vtxt_sett8);
		txtBT = findViewById(R.id.dev_txt_bt_dd_notice);
		
		ArrayAdapter<String> g1AA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, g1);
		g1AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinG1.setAdapter(g1AA);
		
		ArrayAdapter<String> g2AA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, g2);
		g2AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinG2.setAdapter(g2AA);
		
		ArrayAdapter<String> g3AA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, g3);
		g3AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinG3.setAdapter(g3AA);
		
		btAA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
		g3AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinBt.setAdapter(btAA);
		
		setOnClickListener();
		setOnLongClickListener();
		setOnItemSelectedListener();
	}
	
	public void btGetPairedDevices() {
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				btAA.add(device.getName() + "\n" + device.getAddress());
				btAADevices.add(device);
				btAAPos = 0;
			}
		} else {
			txtBT.setText("Discovered Devices");
		}
	}
	
	private void setOnClickListener() {
		onClickListener = v -> {
			String name;
			boolean b_val;
			int i_val;
			
			switch(v.getId()) {
				case R.id.dev_button_sett_g1:
					name = (String) spinG1.getSelectedItem();
					i_val = Integer.parseInt(editG1.getText().toString());
					if(i_val < 0 || i_val > 100) {
						makeToast("Invalid number", Toast.LENGTH_SHORT);
					} else {
						setModel(name, i_val);
						editG1.setText("");
						
						if(spinG1.getSelectedItemPosition() == 0) utils.modNumber(vtxt1, i_val);
						else utils.modNumber(vtxt2, i_val);
					}
					break;
				case R.id.dev_button_sett_g2:
					name = (String) spinG2.getSelectedItem();
					b_val = swG2.isChecked();
					setModel(name, b_val);
					
					if(spinG2.getSelectedItemPosition() == 0) utils.modSwitch(vtxt3, b_val);
					else if(spinG2.getSelectedItemPosition() == 1) utils.modSwitch(vtxt4, b_val);
					else utils.modSwitch(vtxt5, b_val);
					break;
				case R.id.dev_button_sett_g3:
					name = (String) spinG3.getSelectedItem();
					b_val = swG3.isChecked();
					setModel(name, b_val);
					
					if(spinG3.getSelectedItemPosition() == 0) utils.modEnable(vtxt6, b_val);
					else if(spinG3.getSelectedItemPosition() == 1) utils.modEnable(vtxt7, b_val);
					else utils.modEnable(vtxt8, b_val);
					break;
				case R.id.dev_button_bt_discover:
					btAdapter.startDiscovery();
					break;
				case R.id.dev_button_bt_pair:
					service.makeNewConnection(btAADevices.get(btAAPos), btAdapter);
					break;
				case R.id.dev_button_bt_read:
					scheduledComm = scheduledExecutorService.schedule(new Runnable() {
						@Override
						public void run() {
//							service.readFromConnection(); TODO
						}
					}, 0, TimeUnit.MILLISECONDS);
					break;
				case R.id.dev_button_bt_write:
					break;
				case R.id.dev_button_return:
					finish();
					break;
			}
		};
		
		buttonG1.setOnClickListener(onClickListener);
		buttonG2.setOnClickListener(onClickListener);
		buttonG3.setOnClickListener(onClickListener);
		buttonBtDiscover.setOnClickListener(onClickListener);
		buttonBtPair.setOnClickListener(onClickListener);
		buttonBtRead.setOnClickListener(onClickListener);
		buttonBtWrite.setOnClickListener(onClickListener);
		buttonReturn.setOnClickListener(onClickListener);
	}
	
	private void setOnLongClickListener() {
		onLongClickListener = v -> {
			switch(v.getId()) {
				case R.id.dev_button_bt_discover:
					//TODO
					break;
				case R.id.dev_button_bt_pair:
					//TODO
					break;
			}
			return false;
		};
		
		buttonBtDiscover.setOnLongClickListener(onLongClickListener);
		buttonBtPair.setOnLongClickListener(onLongClickListener);
	}
	
	private void setOnItemSelectedListener() {
		onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				switch(v.getId()) {
					case R.id.dev_spin_bt:
						btAAPos = position;
						break;
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			
			}
		};
		
		spinBt.setOnItemSelectedListener(onItemSelectedListener);
	}
	
	private void setModel(String setting, Object value) {
		if(value instanceof Integer) {
			model.put(setting, (int) value);
		} else if(value instanceof Boolean) {
			model.put(setting, (boolean) value);
		} else {
			Log.w(TAG, "Model not set");
		}
	}
	
	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				btAA.add(device.getName() + "\n" + device.getAddress());
				btAADevices.add(device);
			}
		}
	};
	
	private void makeToast(String message, int duration) {
		Toast.makeText(this, message, duration).show();
	}
	
	@Override
	public int getEnable() {
		if(!isBtEnReturn) return -1;
		else return isBtEn ? 1:0;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(btReceiver);
	}
}
