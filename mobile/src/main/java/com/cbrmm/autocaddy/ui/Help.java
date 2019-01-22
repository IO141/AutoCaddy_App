package com.cbrmm.autocaddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.cbrmm.autocaddy.R;
import com.cbrmm.autocaddy.fragments.HelpFragment;
import com.cbrmm.autocaddy.util.FragUtils;

import butterknife.BindView;


public class Help extends AppCompatActivity implements FragUtils {
	
	private final String TAG = ":Help:";
	
	private HelpFragment fragmentH;
	
	private Button btnConnect;
	private Button btnContact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		initFragmentViews(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		initUI();
	}
	
	@Override
	public Bundle getDefaultFragmentState() {
		return new Bundle();
	}
	
	/**
	 * Initializes each fragment view if they have not been before, sets their default state, and
	 * populates this activity's layout with them.
	 * @param savedInstanceState - A Bundle containing the activity's previously saved state.
	 */
	private void initFragmentViews(Bundle savedInstanceState) {
		Bundle defaults = getDefaultFragmentState();
		if(savedInstanceState == null) {
			fragmentH = new HelpFragment();
			
			fragmentH.setArguments(defaults);

//			setFragmentViewState(this, fragmentH, R.id.help_base_container, true);
		}
	}
	
	private void initUI() {
		btnConnect = findViewById(R.id.btn_connect2);
		btnContact = findViewById(R.id.btn_contact);
		
		initClickOnTouch();
	}
	
	private void initClickOnTouch() {
		View.OnClickListener mClickListener = v -> {
			switch(v.getId()) {
				case R.id.btn_connect2:
					startControl();
					break;
				case R.id.btn_contact:
					showContact();
					break;
			}
		};
		
		btnConnect.setOnClickListener(mClickListener);
		btnContact.setOnClickListener(mClickListener);
	}
	
	private void startControl() {
		startActivity(new Intent(getApplicationContext(), Control.class));
	}
	
	private void showContact() {
	
	}
}
