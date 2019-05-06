package com.cbrmm.autocaddy.ui;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.util.AC_Location;
import com.cbrmm.autocaddy.util.Bluetooth;
import com.cbrmm.autocaddy.util.BluetoothCallback;
import com.cbrmm.autocaddy.util.DeviceCallback;
import com.cbrmm.autocaddy.util.DiscoveryCallback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Dev extends AppCompatActivity {
	
	private final String TAG = "Dev";
	private final String TRANSMISSION_DATA_SIGNAL = String.valueOf((char) 14);
	private final String MESSAGE_GPS_ON_SIGNAL = "GON";
	private final String MESSAGE_NET_ON_SIGNAL = "NON";
	private final String MESSAGE_GPS_OFF_SIGNAL = "GOFF";
	private final String MESSAGE_NET_OFF_SIGNAL = "NOFF";
	private final int DISCOVER_PERIOD = 60;
	
	private AC_Location locationAssistant;
	
	private BluetoothDevice btDevice;
	private Bluetooth bt;
	private boolean registered;
	private volatile boolean socket = false;
	
	private Spinner spinNums;//, spinBools;
	private EditText editNums;
//	private Switch swBools;
	private Button btnNums, /*btnBools,*/ btnStart, btnStop, btnUpdate, btnReset, btnReturn;
	
	private TextView[] vtxtArr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dev);
		
		bt = new Bluetooth(this);
		bt.onStart();
		
		initUI();
		initBluetooth();
		checkLocationPermissions();
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch(requestCode) {
			case AC_Location.PERMISSIONS_OK:
				boolean permission = grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED;
				
				if(permission) {
					LocationListener listener = initLocationListener();
					LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					locationAssistant = new AC_Location(manager, listener, true);
				} else {
					Log.e(TAG, "Location permissions not granted!");
				}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bluetooth_comm, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.bt_connect:
				if(!bt.isConnected()) connectBluetooth(true);
				else makeToast("Bluetooth already connected");
				return true;
			case R.id.bt_close:
				if(bt.isConnected()) connectBluetooth(false);
				else makeToast("Bluetooth already disconnected");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		startBluetooth();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		locationAssistant.destroy();
		destroyBluetooth();
	}
	
	private void initUI() {
		
		final String[] numSettings = {Control.validSettings[0], Control.validSettings[1],
				Control.validSettings[2], Control.validSettings[3], Control.validSettings[4],
				Control.validSettings[5], Control.validSettings[6]};
//		final String[] boolSettings = {};
		
		spinNums = findViewById(R.id.dev_spin_num_setts);
//		spinBools = findViewById(R.id.dev_spin_bool_setts);
		
		editNums = findViewById(R.id.dev_edit_num_sett);
//		swBools = findViewById(R.id.dev_switch_bool_sett);
		
		btnNums = findViewById(R.id.dev_btn_num_sett);
//		btnBools = findViewById(R.id.dev_btn_bool_sett);
		btnStart = findViewById(R.id.dev_btn_start);
		btnStop = findViewById(R.id.dev_btn_stop);
		btnUpdate = findViewById(R.id.dev_btn_update);
		btnReset = findViewById(R.id.dev_btn_reset);
		btnReturn = findViewById(R.id.dev_btn_return);
		
		vtxtArr = new TextView[] {
				findViewById(R.id.dev_vtxt_sett_speed),   //Speed
				findViewById(R.id.dev_vtxt_sett_gpsx),    //GPS X
				findViewById(R.id.dev_vtxt_sett_gpsy),    //GPS Y
				findViewById(R.id.dev_vtxt_sett_accloc),  //Acc Loc
				findViewById(R.id.dev_vtxt_sett_avggpsx), //Avg GPS X
				findViewById(R.id.dev_vtxt_sett_avggpsy), //Avg GPS Y
				findViewById(R.id.dev_vtxt_sett_accavg)   //Acc Avg
		};
		
		btnUpdate.setEnabled(false);
		
		ArrayAdapter<String> numsAA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, numSettings);
		numsAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinNums.setAdapter(numsAA);
		
//		ArrayAdapter<String> boolsAA = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, boolSettings);
//		boolsAA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinBools.setAdapter(boolsAA);
		
		setOnClickListener();
		getInitialDataState();
	}
	
	private void initBluetooth() {
		if(bt != null) {
			bt.setCallbackOnUI(this);
			
			bt.setBluetoothCallback(new BluetoothCallback() {
				@Override
				public void onBluetoothTurningOn() {
				
				}
				
				@Override
				public void onBluetoothOn() {
				
				}
				
				@Override
				public void onBluetoothTurningOff() {
				
				}
				
				@Override
				public void onBluetoothOff() {
				
				}
				
				@Override
				public void onUserDeniedActivation() {
					makeToast("Bluetooth disabled for AutoCaddy");
				}
			});
			
			bt.setDiscoveryCallback(new DiscoveryCallback() {
				@Override
				public void onDiscoveryStarted() {
					makeToast("Discovery Started");
				}
				
				@Override
				public void onDiscoveryFinished() {
					makeToast("Discovery Finished");
				}
				
				@Override
				public void onDeviceFound(BluetoothDevice device) {
					makeToast("Device found: " + device.getName() + ":" + device.getAddress());
				}
				
				@Override
				public void onDevicePaired(BluetoothDevice device) {
					List<BluetoothDevice> pairedDevs = bt.getPairedDevices();
					
					makeToast("Device paired: " + device.getName() + ":" + device.getAddress());
					
					if(pairedDevs.size() == 1) {
						btDevice = device;
						connectBluetooth(true);
					} else if(pairedDevs.size() > 1) {
						for(BluetoothDevice pairedDev : pairedDevs) {
							if(Objects.equals(pairedDev.getName(), "HC-06")) {
								btDevice = pairedDev;
								connectBluetooth(true);
							}
						}
						makeToast("Device \"HC-06\" not found");
					}
				}
				
				@Override
				public void onDeviceUnpaired(BluetoothDevice device) {
					makeToast("Device unpaired: " + device.getName() + ":" + device.getAddress());
				}
				
				@Override
				public void onError(String message) {
					makeToast("Error: " + message);
				}
			});
			
			bt.setDeviceCallback(new DeviceCallback() {
				@Override
				public void onDeviceConnected(BluetoothDevice device) {
					makeToast("Connected to " + device.getName() + " - " + device.getAddress());
					runOnUiThread(() -> btnUpdate.setEnabled(true));
				}
				
				@Override
				public void onDeviceDisconnected(BluetoothDevice device, String message) {
					makeToast("Disconnected!");
					runOnUiThread(() -> btnUpdate.setEnabled(false));
				}
				
				@Override
				public void onMessage(String message) {
					if(message != null) {
						Log.d(TAG, "Bluetooth msg received: " + message);
						switch(message) {
							case MESSAGE_GPS_ON_SIGNAL:
								locationAssistant.setGPSLocations(true);
								locationAssistant.enable(true);
								break;
							case MESSAGE_GPS_OFF_SIGNAL:
								locationAssistant.setGPSLocations(false);
								locationAssistant.enable(false);
								break;
							case MESSAGE_NET_ON_SIGNAL:
								locationAssistant.setNetLocations(true);
								locationAssistant.enable(true);
								break;
							case MESSAGE_NET_OFF_SIGNAL:
								locationAssistant.setNetLocations(false);
								locationAssistant.enable(false);
								break;
						}
					}
				}
				
				@Override
				public void onError(String message) {
					makeToast("Error: " + message);
				}
				
				@Override
				public void onConnectError(final BluetoothDevice device, String message) {
					makeToast("Error: " + message);
					btnUpdate.setEnabled(false);
				}
			});
		}
	}
	
	private void startBluetooth() {
		if(!registered) {
			bt.onStart();
			registered = true;
		}
		if(!bt.isEnabled()) bt.enable();
		if(btDevice == null && bt.getBluetoothAdapter() != null) btDevice = bt.getPairedDevices().get(0);
		if(btDevice != null) connectBluetooth(true);
	}
	
	private void destroyBluetooth() {
		if(btDevice != null && bt.isConnected()) connectBluetooth(false);
		if(registered) {
			bt.onStop();
			registered = false;
		}
	}
	
	private void getInitialDataState() {
		for(int i = 0; i < vtxtArr.length; i++) {
			if(Control.validSettingsSize[i] == Control.getSzBool()) {
//				modSwitch(vtxtArr[i], Hub.dataModel.getBool(Control.validSettings[i]));
			} else if(Control.validSettingsSize[i] == Control.getSzInt()) {
				modNumber(vtxtArr[i], Hub.dataModel.getInt(Control.validSettings[i]));
			} else if(Control.validSettingsSize[i] == Control.getSzFloat()) {
				modNumber(vtxtArr[i], Hub.dataModel.getFloat(Control.validSettings[i]));
			} else if(Control.validSettingsSize[i] == Control.getSzDouble()) {
				modNumber(vtxtArr[i], Hub.dataModel.getDouble(Control.validSettings[i]));
			}
		}
	}
	
	private LocationListener initLocationListener() {
		return new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					Location currLoc = locationAssistant.updateLocation(location);
					if(location != currLoc && locationAssistant.getTimeDelta() > AC_Location.ONE_MINUTE / 12) {
						makeToast("Location changed: " + currLoc.getProvider());
					}
					Log.d(TAG, "Updated Location: "
							+ Double.toString(currLoc.getLatitude()) + ","
							+ Double.toString(currLoc.getLongitude()));
					
					Location avgLoc = locationAssistant.getCalcAvgLocation();
					setControlModel(Control.getNameLat(), currLoc.getLatitude());
					setControlModel(Control.getNameLong(), currLoc.getLongitude());
					setControlModel(Control.getNameAvgLat(), avgLoc.getLatitude());
					setControlModel(Control.getNameAvgLong(), avgLoc.getLongitude());
					
					setControlModel(Control.getNameAccLoc(), currLoc.getAccuracy());
					setControlModel(Control.getNameAccAvg(), avgLoc.getAccuracy());
					
					if(bt.isConnected() &&
							(!Objects.equals(currLoc, location)
									|| !Objects.equals(avgLoc, currLoc)
									|| locationAssistant.getTimeDelta() >= AC_Location.ONE_MINUTE)) {
						sendDataTransmission("");
						Log.d(TAG, "Location update notification sent to AutoCaddy");
					}
				} else {
					Log.d(TAG, "Updated location: Null");
				}
			}
			
			@Override
			public void onStatusChanged(String s, int i, Bundle bundle) {
				Log.d(TAG, "Location status changed: " + s);
			}
			
			@Override
			public void onProviderEnabled(String s) {
			
			}
			
			@Override
			public void onProviderDisabled(String s) {
			
			}
		};
	}
	
	private boolean checkLocationPermissions() {
		if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, AC_Location.PERMISSIONS_OK);
			return false;
		} else {
			initLocationAssistant(true);
		}
		return true;
	}
	
	private void initLocationAssistant(boolean permission) {
		if(permission) {
			LocationListener listener = initLocationListener();
			LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationAssistant = new AC_Location(manager, listener, true);
		} else {
			Log.e(TAG, "Location permissions not granted!");
		}
	}
	
	private void connectBluetooth(boolean enable) {
		if(enable) {
			if(!bt.isConnected() && !socket) {
				socket = true;
				bt.connectToDevice(btDevice);
			}
		} else {
			if(bt.isConnected() && socket) {
				bt.disconnect();
				socket = false;
			}
		}
	}
	
	private void sendDataTransmission(String msg) {
		bt.send(Bluetooth.TRANSMISSION_DATA_SIGNAL);
		bt.send(Hub.dataModel.getDataArrForTransmissionString());
		if(!Objects.equals(msg, "")) makeToast(msg);
	}
	
	
	
	/**
	 * Defines the behavior of each button.
	 */
	private void setOnClickListener() {
		View.OnClickListener onClickListener = v -> {
			String name, value;
			boolean b_val;
			int index;
			short i_val;
			double f_val;
			
			switch(v.getId()) {
				case R.id.dev_btn_num_sett:
					name = (String) spinNums.getSelectedItem();
					value = editNums.getText().toString();
					index = Arrays.asList(Control.validSettings).indexOf(name);
					if(value.contains(".")) {
						try {
							f_val = Double.parseDouble(value);
						} catch(Exception e) {
							Log.d(TAG, "Error parsing double value: " + value + " for setting" + name);
							f_val = 0;
						}
						if(Control.validSettingsSize[index] == Control.getSzFloat()) {
							setControlModel(name, (float) f_val);
						} else if(Control.validSettingsSize[index] == Control.getSzDouble()) {
							setControlModel(name, f_val);
						} else {
							setControlModel(name, Math.round(f_val));
						}
					} else {
						i_val = !Objects.equals(value, "") ? Short.parseShort(value) : 0;
						if(Control.validSettingsSize[index] == Control.getSzFloat()) {
							setControlModel(name, (float) i_val);
						} else if(Control.validSettingsSize[index] == Control.getSzDouble()) {
							setControlModel(name, (double) i_val);
						} else {
							setControlModel(name, i_val);
						}
					}
					break;
//				case R.id.dev_btn_bool_sett:
//					name = (String) spinBools.getSelectedItem();
//					b_val = swBools.isChecked();
//
//					setControlModel(name, b_val);
//					break;
				case R.id.dev_btn_start:
					if(bt.isConnected()) {
						bt.send(String.valueOf(Control.CMD_CODE_START));
						makeToast("Start command sent.");
					} else {
						makeToast("Bluetooth not connected.");
					}
					break;
				case R.id.dev_btn_stop:
					if(bt.isConnected()) {
						bt.send(String.valueOf(Control.CMD_CODE_STOP));
						makeToast("Stop command sent.");
					} else {
						makeToast("Bluetooth not connected.");
					}
					break;
				case R.id.dev_btn_update:
					if(bt.isConnected()) sendDataTransmission("Updated");
					break;
				case R.id.dev_btn_reset:
					Hub.dataModel.reset();
					break;
				case R.id.dev_btn_return:
					finish();
					break;
			}
		};
		
		btnNums.setOnClickListener(onClickListener);
		btnStart.setOnClickListener(onClickListener);
		btnStop.setOnClickListener(onClickListener);
		btnUpdate.setOnClickListener(onClickListener);
		btnReset.setOnClickListener(onClickListener);
		btnReturn.setOnClickListener(onClickListener);
	}
	
	/**
	 * A wrapper for the call to the Hub data model that also logs the action.
	 * @param setting The name of the setting to modify.
	 * @param value The new value of the setting.
	 */
	private void setControlModel(String setting, Object value) {
		Hub.dataModel.put(setting, value);
		Log.d(TAG, "Control model updated.");
		
		
		int index = Arrays.asList(Control.validSettings).indexOf(setting);
		
		modNumber(vtxtArr[index], (Number) value);
		editNums.setText("");
		
//		switch(index) {
//			case 0:
//			case 1:
//			case 2:
//			case 3:
//			case 4:
//			case 5:
//			case 6:
//			case 7:
//			case 8:
//				modNumber(vtxtArr[index], (Number) value);
//				editNums.setText("");
//				break;
//			case 9:
//			case 10:
//				modSwitch(vtxtArr[index], (Boolean) value);
//				break;
//		}
	}
	
	private void makeToast(final String message) {
		this.runOnUiThread(() -> Toast.makeText(Dev.this, message, Toast.LENGTH_SHORT).show());
	}
	
	private void modNumber(TextView resource, final Number num) {
		if(num instanceof Double) {
			BigDecimal bd = new BigDecimal((Double) num).setScale(7, RoundingMode.HALF_UP);
			runOnUiThread(() -> resource.setText(String.valueOf(bd.doubleValue())));
		} else runOnUiThread(() -> resource.setText(String.valueOf(num)));
	}
}
