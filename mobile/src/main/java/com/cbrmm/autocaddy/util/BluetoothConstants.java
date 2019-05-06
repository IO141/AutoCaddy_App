package com.cbrmm.autocaddy.util;

public interface BluetoothConstants {
	
	// Message types sent from the BluetoothCommServiceOLD Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	
	// Key names received from the BluetoothCommServiceOLD Handler
	public static final String DEVICE_NAME = "test_device";
	public static final String TOAST = "toast";
	
}
