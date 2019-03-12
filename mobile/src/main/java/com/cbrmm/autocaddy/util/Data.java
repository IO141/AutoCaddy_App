package com.cbrmm.autocaddy.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cbrmm.autocaddy.ui.Control;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


public class Data {
	
	private final String TAG = "Data";
	private String title;
	
	private static final Set<String> orderedKeys = new LinkedHashSet<>(Arrays.asList(Control.validSettings));
	private static final String[] orderedKeysArr = orderedKeys.toArray(new String[orderedKeys.size()]);
	
	private final int INT_LENGTH = 8;
	private final int BOOL_LENGTH = 1;
	
	private byte[][] data = new byte[10][];
	private int dataLen;
	
	public Data(String title) {
		this.title = title.trim().replaceAll("\\n", "").replaceAll("\\r", "");
	}
	
	public Data(String title, byte[] dataArr) {
		Data data = new Data(title);
		data.setDataArr(dataArr);
	}
	
	public void put(String setting, int value) {
		if(!orderedKeys.contains(setting))
			throw new IllegalArgumentException("Setting must be valid.");
		
		for(int i = 0; i < orderedKeysArr.length; i++) {
			if(Objects.equals(setting, orderedKeysArr[i])) {
				if(data[i] == null) dataLen += INT_LENGTH;
				data[i] = ByteBuffer.allocate(INT_LENGTH).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
				break;
			}
		}
	}
	
	public void put(String setting, boolean value) {
		if(!orderedKeys.contains(setting))
			throw new IllegalArgumentException("Setting must be valid.");
		
		for(int i = 0; i < orderedKeysArr.length; i++) {
			if(Objects.equals(setting, orderedKeysArr[i])) {
				if(data[i] == null) dataLen += BOOL_LENGTH;
				data[i] = ByteBuffer.allocate(BOOL_LENGTH).put((byte) (value ? 1:0)).array();
				break;
			}
		}
	}
	
	//Returns value of int setting
	//If setting is 0 or dne, returns 0
	public int getInt(String setting) {
		if(!orderedKeys.contains(setting))
			throw new IllegalArgumentException("Setting must be valid.");
		
		for(int i = 0; i < orderedKeysArr.length; i++) {
			if(Objects.equals(setting, orderedKeysArr[i])) {
				return data[i] == null ?
						0:ByteBuffer.wrap(data[i]).order(ByteOrder.LITTLE_ENDIAN).getInt();
			}
		}
		
		Log.wtf(TAG, "Data not read");
		throw new NullPointerException();
	}
	
	//Returns value of bool setting
	//If setting is false or dne, returns false
	public boolean getBool(String setting) {
		if(!orderedKeys.contains(setting))
			throw new IllegalArgumentException("Setting must be valid.");
		
		for(int i = 0; i < orderedKeysArr.length; i++) {
			if(Objects.equals(setting, orderedKeysArr[i])) {
				return data[i] != null && ByteBuffer.wrap(data[i]).order(ByteOrder.LITTLE_ENDIAN).get() == 1;
			}
		}
		
		Log.e(TAG, "Data not read");
		return false;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public int getDataLen() {
		return dataLen;
	}
	
	public byte[] getDataArr() {
		byte[] arr = new byte[dataLen];
		int k = 0;
		for(byte[] aData : data) {
			if(aData != null) for(int j = 0; j < aData.length; j++, k++) arr[k] = aData[j];
		}
		return arr;
	}
	
	public void setDataArr(byte[] dataArr) {
		if(dataLen != 0 && dataArr.length != dataLen) Log.e(TAG, "Invalid data length.", new IllegalArgumentException());
		else {
			int k = 0;
			for(int i = 0; i < data.length; i++) {
				if(data[i] == null) break;
				for(int j = 0; j < data[i].length; j++, k++) {
					data[i][j] = dataArr[k];
				}
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean equal;
		if(!(obj instanceof Data)) {
			return false;
		}
		
		equal = Objects.equals(this.title, ((Data) obj).title);
		equal &= this.data == ((Data) obj).data;
		return equal;
	}
	
	@Override
	public int hashCode() {
		return title.hashCode() ^ Arrays.deepHashCode(data);
	}

}
