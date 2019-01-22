package com.cbrmm.autocaddy.ui;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.driver.AC_Interface;
import com.cbrmm.autocaddy.fragments.ControlDataFragment;
import com.cbrmm.autocaddy.fragments.ControlSchemeFragment;
import com.cbrmm.autocaddy.util.FragUtils;


public class Control extends AppCompatActivity implements FragUtils {
	
	private ControlSchemeFragment fragmentCS;
	private ControlDataFragment fragmentCD;
	
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
		
		//ControlScheme
		int[] sett1to2 = new int[2]; //Init to 0
		boolean[] sett3to5 = new boolean[3]; //Init to false
		boolean[] sett6AtoC = new boolean[3]; //Init to false
		
		args.putIntArray(ControlSchemeFragment.CS_KEY__PREC_SETTS, sett1to2);
		args.putBooleanArray(ControlSchemeFragment.CS_KEY__BIN_SETTS, sett3to5);
		args.putBooleanArray(ControlSchemeFragment.CS_KEY__CHK_SETTS, sett6AtoC);
		
		//ControlData
		
		return args;
	}
}
