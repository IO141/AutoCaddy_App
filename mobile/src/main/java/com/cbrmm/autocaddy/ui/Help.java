package com.cbrmm.autocaddy.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.cbrmm.autocaddy.R;


public class Help extends AppCompatActivity {
	
	private final String TAG = "Help";
	
	private ScrollView helpView;
	private LinearLayout overlayView;
	
	private Button btnConnect;
	private Button btnContact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		initUI();
	}
	
	private void initUI() {
		
		helpView = findViewById(R.id.help_scrollview);
		helpView.setBackgroundColor(Color.BLACK);
		helpView.getBackground().setAlpha(0);
		
		overlayView = findViewById(R.id.help_overlay_container);
		overlayView.setElevation(20);
		overlayView.setBackgroundColor(Color.WHITE);
		overlayView.getBackground().setAlpha(255);
		overlayView.setVisibility(View.INVISIBLE);
		
		btnConnect = findViewById(R.id.btn_return);
		btnContact = findViewById(R.id.btn_contact);
		
		initClickOnTouch();
	}
	
	private void initClickOnTouch() {
		final boolean[] show = new boolean[1];
		View.OnClickListener mClickListener = v -> {
			switch(v.getId()) {
				case R.id.btn_return:
					finish();
					break;
				case R.id.btn_contact:
					show[0] = !show[0];
					showContact(show[0]);
					break;
			}
		};
		
		btnConnect.setOnClickListener(mClickListener);
		btnContact.setOnClickListener(mClickListener);
	}
	
	private void showContact(boolean show) {
		if(show) {
			helpView.getBackground().setAlpha(200);
			overlayView.setVisibility(View.VISIBLE);
		} else {
			overlayView.setVisibility(View.INVISIBLE);
			helpView.getBackground().setAlpha(0);
		}
	}
}
