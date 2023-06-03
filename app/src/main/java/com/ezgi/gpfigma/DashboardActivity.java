package com.ezgi.gpfigma;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

public class DashboardActivity extends Activity {

	private RelativeLayout convert,qr,note,exit;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);


		convert = findViewById(R.id.convert);
		qr = findViewById(R.id.qr);
		note = findViewById(R.id.note);
		exit =  findViewById(R.id.exit);


		convert.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(DashboardActivity.this,ConvertingActivity.class));
			}
		});

		qr.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(DashboardActivity.this,QrActivity.class));
			}
		});

		note.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(DashboardActivity.this,NoteActivity.class));
			}
		});

		//buraya alert getirilebilir
		exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(DashboardActivity.this,QrActivity.class);
						startActivity(intent);
					}
				},0);
			}
		});


		
		//custom code goes here
	
	}

}
	
	