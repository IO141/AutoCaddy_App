package com.cbrmm.autocaddy.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.fragments.ControlDataFragment;
import com.cbrmm.autocaddy.fragments.ControlSchemeFragment;
import com.cbrmm.autocaddy.util.Data;
import com.cbrmm.autocaddy.util.FragUtils;


public class Control extends AppCompatActivity implements FragUtils {
	
	public static final String[] validSettings = {"Speed", "Turn", "XAccelerometer", "YAccelerometer", "ZAccelerometer", "XGPS", "YGPS", "Autonomous", "Sonic"};
	public static final int[] validSettingsSize = {2, 2, 4, 4, 4, 8, 8, 1, 1};
	
	public static final String C_KEY__MODEL = "Primary Data Model";
	
	private ControlSchemeFragment fragmentCS;
	private ControlDataFragment fragmentCD;
	
	protected static Data dataModel;
	
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
		Bundle defaults = getDefaultFragmentState();
		if(savedInstanceState == null) {
			fragmentCS = new ControlSchemeFragment();
			fragmentCD = new ControlDataFragment();
			
			fragmentCS.setArguments(defaults);
			fragmentCD.setArguments(defaults);
			
			setFragmentViewState(this, fragmentCS, R.id.control_scheme_container, true);
		}
	}
	
	/**
	 * Sets the default state of each fragment so that they can be displayed in each container
	 * layout without crashing the app.
	 * @return A new Bundle containing the default state of each fragment in this activity.
	 */
	@Override
	public Bundle getDefaultFragmentState() {
		Bundle args = new Bundle();
		
		initDataModel();
		
		//ControlScheme
		//ControlData
		args.putByteArray(dataModel.getName(), dataModel.getDataArr());
		
		return args;
	}
	
	protected static Data initExternalData(String name) {
		Data data = new Data(name);
		
		data.put(validSettings[0], 0);
		data.put(validSettings[1], 0);
		data.put(validSettings[2], false);
		data.put(validSettings[3], false);
		data.put(validSettings[4], false);
		data.put(validSettings[5], false);
		data.put(validSettings[6], false);
		data.put(validSettings[7], false);
		
		return data;
	}
	
	private void initDataModel() {
		dataModel = new Data(C_KEY__MODEL);
		
		dataModel.put(validSettings[0], 0);
		dataModel.put(validSettings[1], 0);
		dataModel.put(validSettings[2], false);
		dataModel.put(validSettings[3], false);
		dataModel.put(validSettings[4], false);
		dataModel.put(validSettings[5], false);
		dataModel.put(validSettings[6], false);
		dataModel.put(validSettings[7], false);
	}
}
