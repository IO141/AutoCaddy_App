package com.cbrmm.autocaddy.util;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.cbrmm.autocaddy.fragments.base.BaseFragment;


public interface FragUtils {
	
	Bundle getDefaultFragmentState();
	
	/**
	 * This helper method handles the FragmentTransaction required to change the fragment
	 * in each container view and updates chartFrag.
	 *
	 * @param baseFrag The fragment whose state is to be changed.
	 * @param layoutID The view where the baseFrag is or is to be removed from.
	 * @param add True if adding baseFrag to a view, false if removing it.
	 */
	default void setFragmentViewState(AppCompatActivity activity, BaseFragment baseFrag, int layoutID, boolean add) {
		FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
		if(add) {
			transaction.add(layoutID, baseFrag).commit();
		} else {
			transaction.remove(baseFrag);
			transaction.addToBackStack(null);
			transaction.commit();
		}
	}
	
}
