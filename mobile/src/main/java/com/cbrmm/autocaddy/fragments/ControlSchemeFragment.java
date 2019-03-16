package com.cbrmm.autocaddy.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.fragments.base.BaseControlFragment;
import com.cbrmm.autocaddy.ui.Control;
import com.cbrmm.autocaddy.ui.Hub;

import butterknife.BindView;


public class ControlSchemeFragment extends BaseControlFragment {
	
	private final String TAG = "ControlSchemeFragment";
	
	@BindView(R.id.cs_rg_speed) RadioGroup rgSpeed;
	@BindView(R.id.cs_rg_auto) RadioGroup rgAuto;
	
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
					initModel();
					
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
	
	/**
	 * A safety method that allows a user to just press go and still have
	 * the AutoCaddy perform as advertised.
	 */
	private void initModel() {
		if(rgSpeed.getCheckedRadioButtonId() == -1) setControlModel(Control.getValidSpeed(), 50);
		if(rgAuto.getCheckedRadioButtonId() == -1) setControlModel(Control.getValidAuto(), true);
	}
	
	/**
	 * A wrapper for the call to the Hub data model that also logs the action.
	 * @param setting The name of the setting to modify.
	 * @param value The new value of the setting.
	 */
	private void setControlModel(String setting, Object value) {
		Hub.dataModel.put(setting, value);
		Log.i(TAG, "Control model modified.");
	}
}
