package com.cbrmm.autocaddy.fragments.base;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.LayoutRes;


public abstract class BaseDialogueFragment extends DialogFragment {
	
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
