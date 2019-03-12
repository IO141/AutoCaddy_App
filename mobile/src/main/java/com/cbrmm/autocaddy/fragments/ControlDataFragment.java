package com.cbrmm.autocaddy.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cbrmm.autocaddy.fragments.base.BaseFragment;
import com.cbrmm.autocaddy.util.OnPassCommData;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class ControlDataFragment extends BaseFragment {
	
	private final static int REQUEST_ENABLE_BT = 1;
	private final static String TAG = "ControlDataFragment";
	
	private boolean btEn;
	
	private String acBtAddr = ""; //TODO Save and load
	
	private BluetoothSocket btSocket;
	private BluetoothAdapter btAdapter;
	private Set<BluetoothDevice> pairedBtDevices;
	
	protected OnPassBtData dataPasser;
	
	@Override
	protected int getLayoutId() {
		return 0;
	}
	
	@Override
	protected void initUIState(Bundle args) {
	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
			btEn = true;
		}
	}
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		dataPasser = (OnPassBtData) context;
	}
	
	protected void initBtEn() {
		if(!btAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
	
	protected boolean findPairedDevice() {
		pairedBtDevices = btAdapter.getBondedDevices();
		
		if(pairedBtDevices.size() > 0) {
			for(BluetoothDevice device : pairedBtDevices) {
				if(Objects.equals(device.getName(), acBtAddr)) {
					//connext
				}
			}
		}
		return false;
	}
	
	
	public interface OnPassBtData extends OnPassCommData {}
	
	private class BtThread extends Thread {
		private final String name = "AutoCaddy";
		private final UUID uuid = UUID.fromString("A32103E0-3069-11E9-B56E-0800200C9A66");
		private final BluetoothServerSocket btServerSocket;
		
		public BtThread() {
			BluetoothServerSocket tmp = null;
			try {
				tmp = btAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
			} catch(IOException e) {
				Log.e(TAG, "Socket listen failed.", e);
			}
			btServerSocket = tmp;
		}
		
		@Override
		public void run() {
			BluetoothSocket btSocket = null;
			while(true) {
				try {
					btSocket = btServerSocket.accept();
				} catch(IOException e) {
					Log.e(TAG, "Socket accept failed.", e);
					break;
				}
				if(btSocket != null) {
				
				}
			}
		}
	}
}
