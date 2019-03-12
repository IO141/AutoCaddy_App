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
		assignData = initData(assignTitle, 0);
		defaultData = initData(defTitle, 7);
		origData = initData(origTitle, 7);
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
	public void makeData() {
		assertNull(nullData);
		assertNotNull(assignData);
		assertNotNull(defaultData);
		assertNotNull(origData);
	}
	
	@Test
	public void initData() {
		assertNull(initData);
		initData = new Data(initTitle);
		assertNotNull(initData);
		assertEquals(initTitle, initData.getTitle());
	}
	
	@Test
	public void assignData1() {
		assertNotNull(assignData);
		assignData.put(validSettings[0], 0);
		assertEquals(0, assignData.getInt(validSettings[0]));
		assertEquals(7, assignData.getDataLen());
	}
	
	@Test
	public void assignData2() {
		assertNotNull(assignData);
		for(int i = 0; i < 8; i++) {
			if(i < 2) {
				assignData.put(validSettings[i], 0);
				assertEquals(0, assignData.getInt(validSettings[0]));
			}
			else {
				assignData.put(validSettings[i], false);
				assertFalse(assignData.getBool(validSettings[0]));
			}
		}
		assertEquals(20, assignData.getDataLen());
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void putBadData() {
		assertNotNull(defaultData);
		//arrange
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Setting must be valid.");
		//act
		defaultData.put("Bad Setting", 0);
	}
	
	@Test
	public void getBadIntData() {
		assertNotNull(defaultData);
		//arrange
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Setting must be valid.");
		//act
		defaultData.getInt("Bad Setting");
	}
	
	@Test
	public void getBadBoolData() {
		assertNotNull(defaultData);
		//arrange
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Setting must be valid.");
		//act
		defaultData.getBool("Bad Setting");
	}
	
	@Test
	public void setDataArr() {
		assertNotNull(defaultData);
		assertNotEquals(defaultData.getTitle(), origData.getTitle());
		
		byte[] arr = defaultData.getDataArr();
		for(int i = 0; i < arr.length; i++) arr[i] = 1;
		
		assertNotEquals(arr, defaultData.getDataArr());
		defaultData.setDataArr(arr);
		
		boolean equal = Arrays.equals(arr, defaultData.getDataArr());
		assertTrue(equal);
		assertNotEquals(origData.getInt(validSettings[0]), defaultData.getInt(validSettings[0]));
		assertNotEquals(origData.getBool(validSettings[6]), defaultData.getBool(validSettings[6]));
		assertEquals(origData.getDataLen(), defaultData.getDataLen());
	}
	
	private Data initData(String name, int num) {
		Data data = new Data(name);
		
		if(num > 0) {
			num = num > 7 ? 7:num;
			for(int i = 0; i < num; i++) {
				if(i < 2) data.put(validSettings[i], 0);
				else data.put(validSettings[i], false);
			}
		}
		
		return data;
	}
}
