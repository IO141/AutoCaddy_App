package com.cbrmm.autocaddy.ui;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.util.AC_Location;
import com.cbrmm.autocaddy.util.Bluetooth;
import com.cbrmm.autocaddy.util.BluetoothCallback;
import com.cbrmm.autocaddy.util.DeviceCallback;
import com.cbrmm.autocaddy.util.DiscoveryCallback;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;


public class Control extends AppCompatActivity {
	
	private final String TAG = "Control";
	
	// Accessible list of valid settings
	private static final String spd = "Speed";
	private static final String xgs = "XGPS";
	private static final String ygs = "YGPS";
	private static final String acl = "ALoc";
	private static final String axg = "AXGPS";
	private static final String ayg = "AYGPS";
	private static final String aag = "AAvg";
	
	private static final int szBool   = 1;
	private static final int szInt    = 4;
	private static final int szFloat  = 8;
	private static final int szDouble = 12;
	
	public static final char CMD_CODE_START  = '\4';
	public static final char CMD_CODE_RETURN = '\6';
	public static final char CMD_CODE_STAY   = '\5';
	public static final char CMD_CODE_STOP   = '\1';
	
	public static final String[] validSettings = {spd, xgs, ygs, acl, axg, ayg, aag};
	public static final int[] validSettingsSize =
			{szInt, szDouble, szDouble, szFloat, szDouble, szDouble, szFloat};
	
	public static final String C_KEY__MODEL = "Primary Data Model";
	
	private AC_Location locationAssistant;
	
	private BluetoothDevice btDevice;
	private Bluetooth bt;
	private boolean registered;
	private volatile boolean socket = false;
	
	private volatile boolean startStop = true;
	
	private Button btnStartStop, btnStop, btnReturn, btnStay, btnHelp;
	
	private String userHeight = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		
		bt = new Bluetooth(this);
		
		initUI();
		initBluetooth();
		checkLocationPermissions();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		startBluetooth();
		
		if(!Hub.isUserHeightRecv) buildHeightAlert();
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
	protected void onDestroy() {
		super.onDestroy();
		locationAssistant.destroy();
		destroyBluetooth();
	}
	
	private void initUI() {
		btnStartStop = findViewById(R.id.ctrl_btn_start);
		btnReturn = findViewById(R.id.ctrl_btn_return);
		btnStay = findViewById(R.id.ctrl_btn_stay);
		btnHelp = findViewById(R.id.ctrl_btn_help);
		
		setOnClickListeners();
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
					Log.d(TAG, "Bluetooth disabled for AutoCaddy");
				}
			});

			bt.setDiscoveryCallback(new DiscoveryCallback() {
				@Override
				public void onDiscoveryStarted() {
					Log.d(TAG, "Discovery Started");
				}

				@Override
				public void onDiscoveryFinished() {
					Log.d(TAG, "Discovery Finished");
				}

				@Override
				public void onDeviceFound(BluetoothDevice device) {
					Log.d(TAG, "Device found: " + device.getName() + ":" + device.getAddress());
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
					Log.d(TAG, "Device unpaired: " + device.getName() + ":" + device.getAddress());
				}

				@Override
				public void onError(String message) {
					Log.e(TAG, "Error: " + message);
				}
			});

			bt.setDeviceCallback(new DeviceCallback() {
				@Override
				public void onDeviceConnected(BluetoothDevice device) {
					runOnUiThread(() -> {
						btnStartStop.setEnabled(true);
						btnReturn.setEnabled(true);
						btnStay.setEnabled(true);
					});
					makeToast("Connected to " + device.getName());
				}

				@Override
				public void onDeviceDisconnected(BluetoothDevice device, String message) {
					runOnUiThread(() -> {
						btnStartStop.setEnabled(false);
						btnReturn.setEnabled(false);
						btnStay.setEnabled(false);
					});
					makeToast("Device not connected!");
				}

				@Override
				public void onMessage(String message) {
					if(message != null) {
						Log.d(TAG, "Bluetooth msg received: " + message);
						switch(message) {
							case AC_Location.MESSAGE_GPS_ON_SIGNAL:
								locationAssistant.setGPSLocations(true);
								locationAssistant.enable(true);
								break;
							case AC_Location.MESSAGE_GPS_OFF_SIGNAL:
								locationAssistant.setGPSLocations(false);
								locationAssistant.enable(false);
								break;
							case AC_Location.MESSAGE_NET_ON_SIGNAL:
								locationAssistant.setNetLocations(true);
								locationAssistant.enable(true);
								break;
							case AC_Location.MESSAGE_NET_OFF_SIGNAL:
								locationAssistant.setNetLocations(false);
								locationAssistant.enable(false);
								break;
						}
					}
				}

				@Override
				public void onError(String message) {
					Log.w(TAG, "Error: " + message);
				}

				@Override
				public void onConnectError(final BluetoothDevice device, String message) {
					Log.w(TAG, "Error: " + message);
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
	
	private void buildHeightAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Enter your height:");
		
		EditText editHeight = new EditText(this);
		editHeight.addTextChangedListener(new CustomTextWatcher(editHeight));
		
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		builder.setView(editHeight);
		editHeight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_TEXT);
		
		// Set up the buttons
		builder.setPositiveButton("OK", (dialog, which) -> {
			if(!Objects.equals(editHeight.getText().toString(), "")) {
				userHeight = editHeight.getText().toString();
				Hub.isUserHeightRecv = true;
			}
//			editHeight.setVisibility(View.INVISIBLE);
		});
		builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
		
		builder.show();
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
						sendTransmission("");
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
	
	private void setOnClickListeners() {
		View.OnClickListener mClickListener = v -> {
			switch(v.getId()) {
				case R.id.ctrl_btn_start:
					if(startStop) {
						if(bt.isConnected()) {
							bt.send(String.valueOf(Control.CMD_CODE_START));
							btnStartStop.setText(R.string.stop);
							startStop = false;
							
							Log.d(TAG, "Start command sent.");
						} else {
							makeToast("Bluetooth not connected.");
						}
					} else {
						if(bt.isConnected()) {
							bt.send(String.valueOf(Control.CMD_CODE_STOP));
							bt.send(String.valueOf(Control.CMD_CODE_STOP));
							bt.send(String.valueOf(Control.CMD_CODE_STOP));
							btnStartStop.setText(R.string.start);
							startStop = true;
							
							Log.d(TAG, "Stop command sent.");
						} else {
							makeToast("Bluetooth not connected.");
						}
					}
					break;
				case R.id.ctrl_btn_stay:
					if(bt.isConnected()) {
						bt.send(String.valueOf(CMD_CODE_STAY));
						Log.d(TAG, "Stay command sent.");
					} else {
						makeToast("Bluetooth not connected.");
					}
					break;
				case R.id.ctrl_btn_return:
					if(bt.isConnected()) {
						bt.send(String.valueOf(CMD_CODE_RETURN));
						Log.d(TAG, "Return command sent.");
					} else {
						makeToast("Bluetooth not connected.");
					}
					break;
				case R.id.ctrl_btn_help:
					startHelp();
					break;
			}
		};
		
		btnStartStop.setOnClickListener(mClickListener);
		btnStay.setOnClickListener(mClickListener);
		btnReturn.setOnClickListener(mClickListener);
		btnHelp.setOnClickListener(mClickListener);
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
	
	private void sendTransmission(String msg) {
		bt.send(Bluetooth.TRANSMISSION_DATA_SIGNAL);
		bt.send(Hub.dataModel.getDataArrForTransmissionString());
		if(!Objects.equals(msg, "")) makeToast(msg);
	}
	
	private void sendHeightTransmission(String msg) {
		bt.send(Bluetooth.TRANSMISSION_HEIGHT_SIGNAL);
		bt.send(userHeight);
		if(!Objects.equals(msg, "")) makeToast(msg);
	}
	
	/**
	 * A wrapper for the call to the Hub data model that also logs the action.
	 * @param setting The name of the setting to modify.
	 * @param value The new value of the setting.
	 */
	private void setControlModel(String setting, Object value) {
		Hub.dataModel.put(setting, value);
		Log.d(TAG, "Control model updated.");
	}
	
	private void makeToast(final String message) {
		this.runOnUiThread(() -> Toast.makeText(Control.this, message, Toast.LENGTH_SHORT).show());
	}
	
	private void startHelp() {
		startActivity(new Intent(this, Help.class));
	}
	
	public static String getSpeedName() {
		return spd;
	}
	
	public static String getNameLat() {
		return xgs;
	}
	
	public static String getNameLong() {
		return ygs;
	}
	
	public static String getNameAccLoc() {
		return acl;
	}
	
	public static String getNameAvgLat() {
		return axg;
	}
	
	public static String getNameAvgLong() {
		return ayg;
	}
	
	public static String getNameAccAvg() {
		return aag;
	}
	
	public static int getSzBool() {
		return szBool;
	}
	
	public static int getSzInt() {
		return szInt;
	}
	
	public static int getSzFloat() {
		return szFloat;
	}
	
	public static int getSzDouble() {
		return szDouble;
	}
	
	private class CustomTextWatcher implements TextWatcher {
		private EditText mEditText;
		
		public CustomTextWatcher(EditText e) {
			mEditText = e;
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count,
		                              int after) {
		}
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		
		public void afterTextChanged(Editable s) {
			int count = s.length();
			String str = s.toString();
			if (count == 1) {
				str = str + "'";
				
				mEditText.setText(str);
				mEditText.setSelection(mEditText.getText().length());
			} else {
				if(count >= 4) {
					String rep;
					if(Integer.parseInt(str.substring(2, 3)) > 11) {
						rep = str.substring(0, 1) + "11";
					} else if(Integer.parseInt(str.substring(2, 3)) < 0) {
						rep = str.substring(0, 1) + "0";
					} else {
						return;
					}
					mEditText.setText(rep);
				}
			}
		}
	}
}
