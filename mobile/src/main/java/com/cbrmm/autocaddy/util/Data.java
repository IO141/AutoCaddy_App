package com.cbrmm.autocaddy.util;

import android.util.Log;

import com.cbrmm.autocaddy.ui.Control;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;


public class Data {
	
	private final String TAG = "Data";
	private String name;
	
	private static final String[] keys = Control.validSettings;
	private static final int[] keySizes = Control.validSettingsSize;
	
	private static final byte[] DATA_SIGNAL = toBytes(new char[] {'A', 'C'});
	
	private final int SIGNAL_LENGTH = 4; //DATA_SIGNAL.len + first-two-chars-of-a-key.len = 2 + 2
	
	private byte[][] data;
	private int dataLen;
	
	/**
	 * Constructor for the Data class.
	 * Holds the value data corresponding to the editable AutoCaddy settings in a
	 * byte array. The data is formatted so that it can easily be manipulated and
	 * read by both the app and the user.
	 * @param name The name of this Data object.
	 */
	public Data(String name) {
		this.name = name.trim().replaceAll("\\n", "").replaceAll("\\r", "");
		initDataArray();
	}
	
	/**
	 * Initializes the 2D byte array used to hold the signal and value of the
	 * AutoCaddy settings. The signal precedes the values, and is composed of the
	 * initial data signal 'AC' and the first two characters of the setting's name.
	 */
	private void initDataArray() {
		this.data = new byte[keys.length][];
		this.dataLen = 0;
		
		byte[] keyChar;
		for(int i = 0; i < this.data.length; i++) {
			keyChar = toBytes(new char[] {keys[i].charAt(0), keys[i].charAt(1)});
			this.data[i] = new byte[SIGNAL_LENGTH + keySizes[i]];
			this.dataLen += this.data[i].length;
			System.arraycopy(DATA_SIGNAL, 0, this.data[i], 0, DATA_SIGNAL.length);
			System.arraycopy(keyChar, 0, this.data[i], DATA_SIGNAL.length, keyChar.length);
		}
	}
	
	/**
	 * Puts a new value for a setting in the data array, overwriting the previous
	 * one.
	 * @param setting The name of the setting whose value is being changed.
	 * @param value The new value of the setting. If not of type Boolean, Integer,
	 * or Float, no value will be written.
	 */
	public void put(String setting, Object value) {
		if(!Arrays.asList(keys).contains(setting)) {
			Log.e(TAG, "Invalid setting", new IllegalArgumentException());
			return;
		}
		
		int index = Arrays.asList(keys).indexOf(setting);
		put(index, value);
	}
	
	/**
	 * Helper put method that handles the arraycopy operation.
	 * @param index The index of the setting in the data array.
	 * @param value The new value of the setting. If not of type Boolean, Integer,
	 * or Float, no value will be written.
	 */
	private void put(int index, Object value) {
		byte[] bytes;
		if(value instanceof Boolean) {
			bytes = toBytes((boolean) value);
		} else if(value instanceof Integer) {
			bytes = toBytes((int) value);
		} else if(value instanceof Float) {
			bytes = toBytes((float) value);
		} else {
			bytes = new byte[0];
		}
		
		System.arraycopy(bytes, 0, this.data[index], SIGNAL_LENGTH, bytes.length);
	}
	
	/**
	 * Gets the boolean value of a setting.
	 * @param setting The name of the setting whose value is returned.
	 * @return The boolean value of the setting. Returns false if the setting does not
	 * have a boolean value or if it does not exist.
	 */
	public boolean getBool(String setting) {
		if(!Arrays.asList(keys).contains(setting)) {
			Log.e(TAG, "Invalid setting", new IllegalArgumentException());
			return false;
		}
		
		int index = Arrays.asList(keys).indexOf(setting);
		return this.data[index].length - SIGNAL_LENGTH == keySizes.length
				&& toBool(Arrays.copyOfRange(this.data[index], SIGNAL_LENGTH, this.data[index].length));
	}
	
	/**
	 * Gets the integer value of a setting.
	 * @param setting The name of the setting whose value is returned.
	 * @return The integer value of the setting. Returns 0 if the setting does not have
	 * an integer value or if it does not exist.
	 */
	public int getInt(String setting) {
		if(!Arrays.asList(keys).contains(setting)) {
			Log.e(TAG, "Invalid setting", new IllegalArgumentException());
			return 0;
		}
		
		int index = Arrays.asList(keys).indexOf(setting);
		if(this.data[index].length - SIGNAL_LENGTH != keySizes.length) return 0;
		return toInt(Arrays.copyOfRange(this.data[index], SIGNAL_LENGTH, this.data[index].length));
	}
	
	/**
	 * Gets the float value of a setting.
	 * @param setting The name of the setting whose value is returned.
	 * @return The float value of the setting. Returns 0 if the setting does not have
	 * a float value or if it does not exist.
	 */
	public float getFloat(String setting) {
		if(!Arrays.asList(keys).contains(setting)) {
			Log.e(TAG, "Invalid setting", new IllegalArgumentException());
			return 0f;
		}
		
		int index = Arrays.asList(keys).indexOf(setting);
		if(this.data[index].length - SIGNAL_LENGTH != keySizes.length) return 0f;
		return toFloat(Arrays.copyOfRange(this.data[index], SIGNAL_LENGTH, this.data[index].length));
	}
	
	/**
	 * Converts an array of characters into an array of bytes.
	 * @param chars The character array to be converted.
	 * @return A byte array representation of chars.
	 */
	private static byte[] toBytes(char[] chars) {
		return new String(chars).getBytes(StandardCharsets.UTF_8);
	}
	
	/**
	 * Converts a boolean into an array of bytes.
	 * @param bool The boolean to be converted.
	 * @return A byte array representation of bool.
	 */
	private static byte[] toBytes(boolean bool) {
		byte val = (byte) (bool ? 1:0);
		return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).put(val).array();
	}
	
	/**
	 * Converts an integer into an array of bytes.
	 * @param num The integer to be converted.
	 * @return A byte array representation of num.
	 */
	private static byte[] toBytes(int num) {
		return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(num).array();
	}
	
	/**
	 * Converts a float into an array of bytes.
	 * @param num The float to be converted.
	 * @return A byte array representation of num.
	 */
	private static byte[] toBytes(float num) {
		return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(num).array();
	}
	
	/**
	 * Converts a byte array into a boolean.
	 * @param bytes The byte array to be converted.
	 * @return A boolean representation of bytes.
	 */
	private static boolean toBool(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).get() == 1;
	}
	
	/**
	 * Converts a byte array into an array of characters.
	 * @param bytes The byte array to be converted.
	 * @return A char array representation of bytes.
	 */
	private static char[] toChar(byte[] bytes) {
		return new String(bytes).toCharArray();
	}
	
	/**
	 * Converts a byte array into an integer.
	 * @param bytes The byte array to be converted.
	 * @return An integer representation of bytes.
	 */
	private static int toInt(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}
	
	/**
	 * Converts a byte array into a float.
	 * @param bytes The byte array to be converted.
	 * @return A float representation of bytes.
	 */
	private static float toFloat(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
	}
	
	/**
	 * Gets the internal array and formats it as a 1D byte array.
	 * @return A 1D byte array.
	 */
	public byte[] getDataArr() {
		byte[] arr = new byte[dataLen];
		
		int k = 0;
		for(byte[] aData : data) {
			if(aData != null) for(int j = 0; j < aData.length; j++, k++) arr[k] = aData[j];
		}
		return arr;
	}
	
	/**
	 * Overwrites the internal data array using an external 1D byte array.
	 * This method is safe: only values are overwritten so if the contents of the
	 * signal space are incorrect then the default value is written there.
	 * If the parameter length does not match the existing array length, no data
	 * is written.
	 * @param dataArr The new array representation of the data.
	 */
	public void setDataArr(byte[] dataArr) {
		if(dataArr.length != dataLen) {
			Log.e(TAG, "Invalid data length.", new IllegalArgumentException());
			return;
		}
		
		int k = 0;
		byte[] signalNew, signalOld = new byte[] {data[0][0], data[0][1], data[0][2], data[0][3]};
		for(int i = 0; i < data.length; i++) {
			signalNew = new byte[] {dataArr[k], dataArr[k + 1], dataArr[k + 2], dataArr[k + 3]};
			if(!Arrays.equals(signalOld, signalNew)) put(keys[i], 0);
			else {
				for(int j = 0; j < data[i].length; j++, k++) {
					if(j < 4) continue;
					data[i][j] = dataArr[k];
				}
			}
		}
	}
	
	/**
	 * Gets the Data object's name.
	 * @return The name of the Data.
	 */
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Data
				&& Objects.equals(this.name, ((Data) obj).name)
				&& Arrays.equals(this.getDataArr(), ((Data) obj).getDataArr());
	}
	
	@Override
	public int hashCode() {
		return name.hashCode() * Arrays.deepHashCode(data);
	}

}
