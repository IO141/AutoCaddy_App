package com.cbrmm.autocaddy.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;


public class BluetoothService {
	
	private final static String TAG = "BluetoothService";
	
	public final static int REQUEST_ENABLE_BT = 1;
	public final static UUID APP_UUID = UUID.fromString("2db928c8-f05d-45e5-b389-7f45b7b4140f");
	
	private static BluetoothService instance = null;
	
	private volatile int threadReq = -1;
	private volatile byte[] threadRead;
	private volatile byte[] threadWrite = new byte[1024];
	
	private OnPassBtData onPassBtData;
	
	private ConnectThread connectThread;
	private ConnectionThread workThread;
	
	private boolean isEnabled;
	public static boolean isConnect = false;
	
	private final Handler mHandler = new Handler() {
//		@Override TODO
//		public void handleMessage(Message msg) {
//			switch(msg.what) {
//				case MESSAGE_STATE_CHANGE:
//					if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
//					switch (msg.arg1) {
//						case BluetoothChatService.STATE_CONNECTED:
//							mTitle.setText(R.string.title_connected_to);
//							mTitle.append(mConnectedDeviceName);
//							mConversationArrayAdapter.clear();
//							break;
//						case BluetoothChatService.STATE_CONNECTING:
//							mTitle.setText(R.string.title_connecting);
//							break;
//						case BluetoothChatService.STATE_LISTEN:
//						case BluetoothChatService.STATE_NONE:
//							mTitle.setText(R.string.title_not_connected);
//							break;
//					}
//					break;
//				case MESSAGE_WRITE:
//					byte[] writeBuf = (byte[]) msg.obj;
//					// construct a string from the buffer
//					String writeMessage = new String(writeBuf);
//					mConversationArrayAdapter.add("Me:  " + writeMessage);
//					break;
//				case MESSAGE_READ:
//					byte[] readBuf = (byte[]) msg.obj;
//					// construct a string from the valid bytes in the buffer
//					String readMessage = new String(readBuf, 0, msg.arg1);
//					mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
//					break;
//				case MESSAGE_DEVICE_NAME:
//					// save the connected device's name
//					mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
//					Toast.makeText(getApplicationContext(), "Connected to "
//							+ mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//					break;
//				case MESSAGE_TOAST:
//					Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
//							Toast.LENGTH_SHORT).show();
//					break;
//			}
//		}
	};
	
	private BluetoothService() { }
	
	public static BluetoothService getBluetoothService() {
		if(instance == null) {
			instance = new BluetoothService();
		}
		return instance;
	}
	
	public static void btEnable(BluetoothAdapter adapter, Context context) {
		if(adapter != null && !adapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((AppCompatActivity) context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
	
	public void setBtEnable(boolean en) {
		isEnabled = en;
	}
	
	public void makeNewConnection(BluetoothDevice device, BluetoothAdapter adapter) {
		Log.i(TAG, "Connect thread started.");
		connectThread = new ConnectThread(device, adapter);
		connectThread.start();
	}
	
	public void readFromConnection(Data data) {
		data.setDataArr(workThread.mBuffer);
	}
	
	public void writeToConnection(Data data) {
		workThread.write(data.getDataArr());
	}
	
	public void cancelCurrConnection() {
		Log.i(TAG, "Connect and Work threads ended.");
		connectThread.cancel();
		workThread.cancel();
	}
	
	public interface OnPassBtData {
		int getEnable();
	}
	
	private interface BtMsgConstants {
		int MESSAGE_READ = 0;
		int MESSAGE_WRITE = 1;
		int MESSAGE_TOAST = 2;
	}
	
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private BluetoothAdapter mmAdapter;
		
		ConnectThread(BluetoothDevice device, BluetoothAdapter adapter) {
			BluetoothSocket tmp = null;
			mmAdapter = adapter;
			isConnect = false;
			
			try {
				tmp = device.createRfcommSocketToServiceRecord(APP_UUID);
			} catch (IOException e) {
				Log.e(TAG, "Bluetooth socket was not created.");
			}
			mmSocket = tmp;
		}
		
		public void run() {
			mmAdapter.cancelDiscovery();
			
			try {
				mmSocket.connect();
			} catch (IOException connectException) {
				Log.w(TAG, "Bluetooth socket cannot be connected.");
				try {
					mmSocket.close();
				} catch (IOException closeException) {
					Log.e(TAG, "Bluetooth socket cannot be closed.");
				}
				return;
			}
			
			isConnect = true;
			workThread = new ConnectionThread(mmSocket);
		}
		
		void cancel() {
			try {
				isConnect = false;
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Bluetooth Socket cannot be canceled.");
			}
		}
	}
	
	private class ConnectionThread extends Thread {
		private final BluetoothSocket mSocket;
		private final InputStream mInStream;
		private final OutputStream mOutStream;
		private final byte[] mBuffer;
		
		ConnectionThread(BluetoothSocket socket) {
			mSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			
			try{
				tmpIn = mSocket.getInputStream();
				tmpOut = mSocket.getOutputStream();
			} catch(IOException e) {
				Log.e(TAG, "Input/OutputStream failed to create.", e);
			}
			
			mBuffer = new byte[1024];
			mInStream = tmpIn;
			mOutStream = tmpOut;
		}
		
		@Override
		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;
			
			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// Read from the InputStream
					bytes = mInStream.read(buffer);
					
					// Send the obtained bytes to the UI Activity
					mHandler.obtainMessage(BtMsgConstants.MESSAGE_READ, bytes, -1, buffer)
							.sendToTarget();
				} catch (IOException e) {
					Log.e(TAG, "Input stream exception", e);
					break;
				}
			}
		}
		
		public void write(byte[] buffer) {
			try {
				mOutStream.write(buffer);
				
				// Share the sent message back to the UI Activity
				mHandler.obtainMessage(BtMsgConstants.MESSAGE_WRITE, -1, -1, buffer)
						.sendToTarget();
			} catch (IOException e) {
				Log.e(TAG, "Output stream exception.", e);
			}
		}
		
		void cancel() {
			try {
				mSocket.close();
			} catch(IOException e) {
				Log.e(TAG, "Bluetooth socket close() failed.", e);
			}
		}
	}

}
