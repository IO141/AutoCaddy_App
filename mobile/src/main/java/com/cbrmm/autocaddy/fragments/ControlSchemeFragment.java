package com.cbrmm.autocaddy.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.driver.AC_Interface;
import com.cbrmm.autocaddy.fragments.base.BaseSubPanelFragment;
import com.cbrmm.autocaddy.util.Scheme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;


public class ControlSchemeFragment extends BaseSubPanelFragment {
	
	private final String KEY_SETT1 = AC_Interface.AC_KEY__SETT1;
	private final String KEY_SETT2 = AC_Interface.AC_KEY__SETT2;
	private final String KEY_SETT3 = AC_Interface.AC_KEY__SETT3;
	private final String KEY_SETT4 = AC_Interface.AC_KEY__SETT4;
	private final String KEY_SETT5 = AC_Interface.AC_KEY__SETT5;
	private final String KEY_SETT6A = AC_Interface.AC_KEY__SETT6A;
	private final String KEY_SETT6B = AC_Interface.AC_KEY__SETT6B;
	private final String KEY_SETT6C = AC_Interface.AC_KEY__SETT6C;
	
	private ArrayList<Scheme> schemes = new ArrayList<>();
	
	private ArrayAdapter<String> schemesAdapter;
	
	@BindView(R.id.spin_schemes) Spinner spinSchemes;
	
	@BindView(R.id.seek_prec_sett1) SeekBar seekSett1;
	@BindView(R.id.seek_prec_sett2) SeekBar seekSett2;
	
	@BindView(R.id.switch_sett1) Switch swSett3;
	@BindView(R.id.switch_sett2) Switch swSett4;
	@BindView(R.id.switch_sett3) Switch swSett5;
	
	@BindView(R.id.chk_sett1) CheckBox chkSett6A;
	@BindView(R.id.chk_sett2) CheckBox chkSett6B;
	@BindView(R.id.chk_sett3) CheckBox chkSett6C;
	
	@BindView(R.id.btn_go) Button btnGo;
	
	private Scheme currScheme;
	
	@Override
	protected int getLayoutId() {
		return R.layout.fragment_control_scheme;
	}
	
	@Override
	protected void initUIState(Bundle args) {
		initSpinSchemes();
		initControlSettings();
	}
	
	@Override
	protected void initSubPanel(Bundle args) {
		HashMap<String, Object> settings = new HashMap<>();
		
		settings.put(KEY_SETT1, 0);
		settings.put(KEY_SETT2, 0);
		settings.put(KEY_SETT3, false);
		settings.put(KEY_SETT4, false);
		settings.put(KEY_SETT5, false);
		settings.put(KEY_SETT6A, "6A");
		settings.put(KEY_SETT6B, "6B");
		settings.put(KEY_SETT6C, "6C");
		
		currScheme = new Scheme("Default", settings);
	}
	
	private void initSpinSchemes() {
		FragmentActivity frag =  Objects.requireNonNull((FragmentActivity) getActivity());
		ArrayList<String> spinDef = new ArrayList<>();
		
		spinDef.add("Default");
		schemesAdapter = new ArrayAdapter<>(frag, android.R.layout.simple_spinner_item, spinDef);
		schemesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinSchemes.setAdapter(schemesAdapter);
//		spinSchemes.setOnItemSelectedListener(makeSpinListener(KEY__MODE));
	}
	
	private void initControlSettings() {
	
	}
	
	private void updateSpinScheme() {
		for(Scheme prev : schemes) {
			if(Objects.equals(prev.getTitle(), currScheme.getTitle())) {
				schemes.remove(prev);
				schemesAdapter.remove(prev.getTitle());
			}
		}
		
		schemes.add(currScheme);
		schemesAdapter.add(currScheme.getTitle());
		schemesAdapter.notifyDataSetChanged();
	}
}
