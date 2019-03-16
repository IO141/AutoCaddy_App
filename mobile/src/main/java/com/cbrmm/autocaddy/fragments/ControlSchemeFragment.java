package com.cbrmm.autocaddy.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.fragments.base.BaseControlFragment;
import com.cbrmm.autocaddy.ui.Control;
import com.cbrmm.autocaddy.ui.Hub;
import com.cbrmm.autocaddy.util.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;


public class ControlSchemeFragment extends BaseControlFragment {
	
	private final String TAG = "ControlSchemeFragment";
	
	@BindView(R.id.cs_rb_slow) RadioButton rbSlow;
	@BindView(R.id.cs_rb_medium) RadioButton rbMedium;
	@BindView(R.id.cs_rb_fast) RadioButton rbFast;
	
	@BindView(R.id.cs_rb_holes) RadioButton rbHoles;
	@BindView(R.id.cs_rb_follow) RadioButton rbFollow;
	
	@BindView(R.id.btn_go) Button btnGo;
	
	@Override
	protected int getLayoutId() {
		return R.layout.fragment_control_scheme;
	}
	
	@Override
	protected void initUIState(Bundle args) {
		initControlSettings();
	}
	
	@Override
	protected void initSubPanel(Bundle args) {
		setClickListeners();
	}
	
	private void initControlSettings() { }
	
	private void setClickListeners() {
		View.OnClickListener mOnClickListener = view -> {
			switch(view.getId()) {
				case R.id.cs_btn_go:
					//TODO FragTransaction w/ControlData
					break;
			}
		};
		
		CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = (cb, b) -> {
			switch(cb.getId()) {
				case R.id.cs_rb_slow:
					setControlModel(Control.getValidSpeed(), 20);
					break;
				case R.id.cs_rb_medium:
					setControlModel(Control.getValidSpeed(), 50);
					break;
				case R.id.cs_rb_fast:
					setControlModel(Control.getValidSpeed(), 80);
					break;
				case R.id.cs_rb_holes:
					setControlModel(Control.getValidAuto(), false);
					break;
				case R.id.cs_rb_follow:
					setControlModel(Control.getValidAuto(), true);
					break;
			}
		};
		
		btnGo.setOnClickListener(mOnClickListener);
		
		rbSlow.setOnCheckedChangeListener(mOnCheckedChangeListener);
		rbMedium.setOnCheckedChangeListener(mOnCheckedChangeListener);
		rbFast.setOnCheckedChangeListener(mOnCheckedChangeListener);
		
		rbHoles.setOnCheckedChangeListener(mOnCheckedChangeListener);
		rbFollow.setOnCheckedChangeListener(mOnCheckedChangeListener);
	}
	
	private void setControlModel(String setting, Object value) {
		Hub.dataModel.put(setting, value);
		Log.i(TAG, "Control model modified.");
	}
}
