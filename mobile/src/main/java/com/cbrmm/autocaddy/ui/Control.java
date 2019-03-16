package com.cbrmm.autocaddy.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.fragments.ControlDataFragment;
import com.cbrmm.autocaddy.fragments.ControlSchemeFragment;
import com.cbrmm.autocaddy.util.FragUtils;


public class Control extends AppCompatActivity implements FragUtils {
	
	private final String TAG = "Control";
	
	// Accessible list of valid settings
	private static final String spd = "Speed";
	
	private static final String trn = "Turn";
	private static final String xac = "XAccelerometer";
	private static final String yac = "YAccelerometer";
	private static final String zac = "ZAccelerometer";
	private static final String xgs = "XGPS";
	private static final String ygs = "YGPS";
	private static final String atn = "Autonomous";
	private static final String sns = "Sensor";
	
	public static final String[] validSettings = {spd, trn, xac, yac, zac, xgs, ygs, atn, sns};
	public static final int[] validSettingsSize = {2, 2, 4, 4, 4, 8, 8, 1, 1};
	
	public static final String C_KEY__MODEL = "Primary Data Model";
	
	private ControlSchemeFragment fragCS;
	private ControlDataFragment fragCD;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		
		initFragmentViews(savedInstanceState);
	}
	
	/**
	 * Initializes each fragment view if they have not been before, sets their default state, and
	 * populates this activity's layout with them.
	 * @param savedInstanceState - A Bundle containing the activity's previously saved state.
	 */
	private void initFragmentViews(Bundle savedInstanceState) {
		if(savedInstanceState == null) {
			fragCS = new ControlSchemeFragment();
			fragCD = new ControlDataFragment();
			
			setFragmentViewState(this, fragCS, R.id.control_scheme_container, true);
		}
	}
	
	@Override
	public Bundle getDefaultFragmentState() {
		return null;
	}
	
	public static String getValidSpeed() {
		return spd;
	}
	
	public static String getValidTurn() {
		return trn;
	}
	
	public static String getValidXAccel() {
		return xac;
	}
	
	public static String getValidYAccel() {
		return yac;
	}
	
	public static String getValidZAccel() {
		return zac;
	}
	
	public static String getValidXGps() {
		return xgs;
	}
	
	public static String getValidYGps() {
		return ygs;
	}
	
	public static String getValidAuto() {
		return atn;
	}
	
	public static String getValidSensor() {
		return sns;
	}
}
