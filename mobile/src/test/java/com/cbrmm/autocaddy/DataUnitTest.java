package com.cbrmm.autocaddy;

import com.cbrmm.autocaddy.util.Data;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

import static com.cbrmm.autocaddy.ui.Control.validSettings;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class DataUnitTest {
	
	private Data nullData = null, initData = null, assignData = null, defaultData = null, origData = null;
	private String initTitle = "Init", assignTitle = "Assign", defTitle = "Default", origTitle = "Original";
	
	@Before
	public void setUp() {
		nullData = null;
		initData = null;
		assignData = new Data(assignTitle);
		defaultData = new Data(defTitle);
		origData = new Data(origTitle);
	}
	
	@After
	public void tearDown() {
		nullData = null;
		initData = null;
		assignData = null;
		defaultData = null;
		origData = null;
		
		initTitle = null;
		assignTitle = null;
		defTitle = null;
		origTitle = null;
	}
	
	@Test
	public void initData() {
		assertNotNull(assignData);
		assertNotNull(defaultData);
		assertNotNull(origData);
		
		assertEquals(assignTitle, assignData.getName());
		assertEquals(defTitle, defaultData.getName());
		assertEquals(origTitle, origData.getName());
	}
	
	@Test
	public void assignData() {
		assertNotNull(assignData);
		assignData.put(validSettings[0], (short) 0);
		assertEquals(0, assignData.getShort(validSettings[0]));
	}
	
	@Test
	public void putBadData() {
		assertNotNull(defaultData);
		defaultData.put("Bad Setting", 1);
		assertArrayEquals(defaultData.getDataArr(), origData.getDataArr());
	}
	
	@Test
	public void getBadBoolData() {
		assertNotNull(defaultData);
		assertFalse(defaultData.getBool("Bad setting"));
	}
	
	@Test
	public void getBadShortData() {
		assertNotNull(defaultData);
		assertTrue(defaultData.getShort("Bad setting") == 0);
	}
	
	@Test
	public void getBadFloatData() {
		assertNotNull(defaultData);
		assertTrue(defaultData.getFloat("Bad setting") == 0f);
	}
	
	@Test
	public void setDataArr1() {
		assertNotNull(defaultData);
		assertNotNull(origData);
		assertNotEquals(defaultData.getName(), origData.getName());
		
		byte[] arr = defaultData.getDataArr();
		for(int i = 0; i < arr.length; i++) arr[i] = 1;
		
		assertFalse(Arrays.equals(arr, defaultData.getDataArr()));
		defaultData.setDataArr(arr);
		
		int last = validSettings.length - 1;
		boolean equal = Arrays.equals(arr, defaultData.getDataArr());
		assertFalse(equal);
	}
	
	@Test
	public void setDataArr2() {
		assertNotNull(defaultData);
		assertNotNull(assignData);
		assertNotNull(origData);
		
		assignData.put(validSettings[0], (short) 1);
		byte[] arr = assignData.getDataArr();
		
		defaultData.setDataArr(arr);
		assertArrayEquals(defaultData.getDataArr(), assignData.getDataArr());
		
		int last = validSettings.length - 1;
		boolean equal = Arrays.equals(arr, defaultData.getDataArr());
		assertTrue(equal);
		
		assertNotEquals(origData.getShort(validSettings[0]), defaultData.getShort(validSettings[0]));
	}
}
