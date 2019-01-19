package com.cbrmm.autocaddy.ui;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.fragments.ControlDataFragment;
import com.cbrmm.autocaddy.fragments.ControlSchemeFragment;
import com.cbrmm.autocaddy.fragments.base.BaseFragment;

import java.util.Arrays;


public class Control extends AppCompatActivity {
	
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
		Bundle defaults = new Bundle();
		if(savedInstanceState == null) {
			fragmentCS = new ControlSchemeFragment();
			fragmentCD = new ControlDataFragment();
			
			fragmentCS.setArguments(defaults);
			fragmentCD.setArguments(defaults);
			
			setFragmentViewState(fragmentCS, R.id.control_scheme_container, true);
		}
	}
	
	/**
	 * This helper method handles the FragmentTransaction required to change the fragment
	 * in each container view and updates chartFrag.
	 *
	 * @param baseFrag The fragment whose state is to be changed.
	 * @param layoutID The view where the baseFrag is or is to be removed from.
	 * @param add True if adding baseFrag to a view, false if removing it.
	 */
	private void setFragmentViewState(BaseFragment baseFrag, int layoutID, boolean add) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		if(add) {
			transaction.add(layoutID, baseFrag).commit();
			
		} else {
			transaction.remove(baseFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
}
