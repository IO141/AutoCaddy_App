package com.cbrmm.autocaddy.fragments.base;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;


public abstract class BaseFragment extends Fragment {
	
	/**
	 * Gets the layout ID of this fragment.
	 *
	 * @return The layout ID of this fragment.
	 */
	@LayoutRes
	protected abstract int getLayoutId();
	
	/**
	 * Initializes the UI state of this fragment.
	 *
	 * @param args Contains initial settings required by this fragment from its parent Activity.
	 */
	protected abstract void initUIState(Bundle args);
}
