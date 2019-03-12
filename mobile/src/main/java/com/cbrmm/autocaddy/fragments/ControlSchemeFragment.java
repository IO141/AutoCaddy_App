package com.cbrmm.autocaddy.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.fragments.base.BaseControlFragment;
import com.cbrmm.autocaddy.ui.Control;
import com.cbrmm.autocaddy.util.Data;
import com.cbrmm.autocaddy.util.Scheme;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;


public class ControlSchemeFragment extends BaseControlFragment {
	
	public static final String CS_KEY__MODEL = "Copied Data Model";
	
	private ArrayList<Data> dataList = new ArrayList<>();
	private ArrayAdapter<String> dataAdapter;
	
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
	
	private Data currModel;
	
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
		currModel = new Data(CS_KEY__MODEL);
		currModel.setDataArr(args.getByteArray(Control.C_KEY__MODEL));
	}
	
	private void initSpinSchemes() {
		FragmentActivity frag =  Objects.requireNonNull(getActivity());
		ArrayList<String> spinDef = new ArrayList<>();
		
		spinDef.add("Default");
		dataAdapter = new ArrayAdapter<>(frag, android.R.layout.simple_spinner_item, spinDef);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinSchemes.setAdapter(dataAdapter);
	}
	
	private void initControlSettings() {
	
	}
	
	private void updateSpinScheme() {
		for(Data prev : dataList) {
			if(Objects.equals(prev.getTitle(), currModel.getTitle())) {
				dataList.remove(prev);
				dataAdapter.remove(prev.getTitle());
			}
		}
		
		dataList.add(currModel);
		dataAdapter.add(currModel.getTitle());
		dataAdapter.notifyDataSetChanged();
	}
}
