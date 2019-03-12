package com.cbrmm.autocaddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.fragments.base.BaseFragment;
import com.cbrmm.autocaddy.ui.Control;

import butterknife.BindView;

@Deprecated
public class HelpFragment extends BaseFragment {
	
	private final String TAG = ":HelpFrag:";
	
	@BindView(R.id.btn_return) Button btnConnect;
	@BindView(R.id.btn_contact) Button btnContact;
	
	@Override
	protected int getLayoutId() {
		return R.layout.fragment_help;
	}
	
	@Override
	protected void initUIState(Bundle args) {
		initClickOnTouch();
	}
	
	private void initClickOnTouch() {
		View.OnClickListener mClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch(view.getId()) {
					case R.id.btn_return:
						startControl();
						break;
					case R.id.btn_contact:
						showContact();
						break;
				}
			}
		};
		
		btnConnect.setOnClickListener(mClickListener);
		btnContact.setOnClickListener(mClickListener);
	}
	
	private void startControl() {
		startActivity(new Intent(getActivity().getApplicationContext(), Control.class));
	}
	
	private void showContact() {
	
	}
	
}
