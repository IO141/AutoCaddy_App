package com.cbrmm.autocaddy.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.util.BluetoothService;
import com.cbrmm.autocaddy.util.DevUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;


public class Dev extends AppCompatActivity implements BluetoothService.OnPassBtData{
	
	private final String TAG = "Dev";
	private final int DISCOVER_PERIOD = 60;
	
	private DevUtils utils;
	
	private BluetoothService service;
	BluetoothAdapter btAdapter;
	
	private View.OnLongClickListener onLongClickListener;
	private AdapterView.OnItemSelectedListener onItemSelectedListener;
	
	private ScheduledFuture<?> scheduledComm;
	private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
	
	ArrayList<BluetoothDevice> btAADevices = new ArrayList<>();
	ArrayAdapter<String> btAA;
	
	private Spinner spinNums, spinBools;
	private EditText editNums;
	private Switch swBools;
	private Button btnNums, btnBools, btnUpdate, btnReset, btnReturn;
	
	private TextView[] vtxtArr;
	
	private boolean isBtEnReturn, isBtEn;
	private int btAAPos = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev);
		
		utils = new DevUtils(this);
		
		initUI();
		
		//TODO bt code
		;
//		registerReceiver(btReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
//
//		service = BluetoothService.getBluetoothService();
//		btAdapter = BluetoothAdapter.getDefaultAdapter();
//		btAdapter.startDiscovery();
//		BluetoothService.btEnable(btAdapter, this);
//		btGetPairedDevices();
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
		
		final String[] numSettings = {Control.validSettings[0], Control.validSettings[1],
				Control.validSettings[2], Control.validSettings[3], Control.validSettings[4],
				Control.validSettings[5], Control.validSettings[6]};
		final String[] boolSettings = {Control.validSettings[7], Control.validSettings[8]};
		
		spinNums = findViewById(R.id.dev_spin_num_setts);
		spinBools = findViewById(R.id.dev_spin_bool_setts);
		
		editNums = findViewById(R.id.dev_edit_num_sett);
		swBools = findViewById(R.id.dev_switch_bool_sett);
		
		btnNums = findViewById(R.id.dev_btn_num_sett);
		btnBools = findViewById(R.id.dev_btn_bool_sett);
		btnUpdate = findViewById(R.id.dev_btn_update);
		btnReset = findViewById(R.id.dev_btn_reset);
		btnReturn = findViewById(R.id.dev_btn_return);
		
		vtxtArr = new TextView[] {
				findViewById(R.id.dev_vtxt_sett_speed),  //Speed
				findViewById(R.id.dev_vtxt_sett_turn),   //Turn
				findViewById(R.id.dev_vtxt_sett_accelx), //Accelerometer X
				findViewById(R.id.dev_vtxt_sett_accely), //Accelerometer X
				findViewById(R.id.dev_vtxt_sett_accelz), //Accelerometer X
				findViewById(R.id.dev_vtxt_sett_gpsx),   //GPS X
				findViewById(R.id.dev_vtxt_sett_gpsy),   //GPS Y
				findViewById(R.id.dev_vtxt_sett_auto),   //Autonomous
				findViewById(R.id.dev_vtxt_sett_sensor)  //Sensor
		};
		
		ArrayAdapter<String> numsAA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numSettings);
		numsAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinNums.setAdapter(numsAA);
		
		ArrayAdapter<String> boolsAA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, boolSettings);
		boolsAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinBools.setAdapter(boolsAA);
		
		setOnClickListener();
	}
	
	/**
	 * Defines the behavior of each button.
	 */
	private void setOnClickListener() {
		View.OnClickListener onClickListener = v -> {
			String name;
			boolean b_val;
			int i_val, index;
			
			switch(v.getId()) {
				case R.id.dev_btn_num_sett:
					name = (String) spinNums.getSelectedItem();
					index = Arrays.asList(Control.validSettings).indexOf(name);
					i_val = Integer.parseInt(editNums.getText().toString());
					
					setControlModel(name, i_val);
					utils.modNumber(vtxtArr[index], i_val);
					editNums.setText("");
					break;
				case R.id.dev_btn_bool_sett:
					name = (String) spinBools.getSelectedItem();
					index = Arrays.asList(Control.validSettings).indexOf(name);
					b_val = swBools.isChecked();
					
					setControlModel(name, b_val);
					utils.modSwitch(vtxtArr[index], b_val);
					editNums.setText("");
					break; //TODO bt code
				case R.id.dev_btn_update:
					//TODO Send bt signal
					break;
				case R.id.dev_btn_reset:
					Hub.dataModel.reset();
					break;
//				case R.id.dev_button_bt_discover:
//					btAdapter.startDiscovery();
//					break;
//				case R.id.dev_button_bt_pair:
//					service.makeNewConnection(btAADevices.get(btAAPos), btAdapter);
//					break;
//				case R.id.dev_button_bt_read:
//					scheduledComm = scheduledExecutorService.schedule(new Runnable() {
//						@Override
//						public void run() {
////							service.readFromConnection(); TODO
//						}
//					}, 0, TimeUnit.MILLISECONDS);
//					break;
//				case R.id.dev_button_bt_write:
//					break;
				case R.id.dev_btn_return:
					finish();
					break;
			}
		};
		
		btnNums.setOnClickListener(onClickListener);
		btnBools.setOnClickListener(onClickListener);
		btnUpdate.setOnClickListener(onClickListener);
		btnReset.setOnClickListener(onClickListener);
		btnReturn.setOnClickListener(onClickListener);
	}
	
	//TODO bt code
	;
//	private void setOnItemSelectedListener() {
//		onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
//				switch(v.getId()) {
//					case R.id.dev_spin_bt:
//						btAAPos = position;
//						break;
//				}
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//
//			}
//		};
//
//		spinBt.setOnItemSelectedListener(onItemSelectedListener);
//	}
	
	/**
	 * A wrapper for the call to the Hub data model that also logs the action.
	 * @param setting The name of the setting to modify.
	 * @param value The new value of the setting.
	 */
	private void setControlModel(String setting, Object value) {
		Hub.dataModel.put(setting, value);
		Log.i(TAG, "Control model modified.");
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
