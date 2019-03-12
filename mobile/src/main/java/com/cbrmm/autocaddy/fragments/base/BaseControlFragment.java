package com.cbrmm.autocaddy.fragments.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cbrmm.autocaddy.util.ControlUtils;

import butterknife.ButterKnife;


public abstract class BaseControlFragment extends BaseFragment implements ControlUtils {
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(getLayoutId(), null);
		ButterKnife.bind(this, root);
		initUIState(getArguments());
		initSubPanel(getArguments());
		
		return root;
	}
	
	/**
	 * Initializes the panel state of this fragment.
	 *
	 * @param args Contains initial settings required by this fragment from its parent Activity.
	 */
	protected abstract void initSubPanel(Bundle args);
}
