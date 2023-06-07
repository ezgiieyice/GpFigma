package com.ezgi.gpfigma;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

		//alert
		exit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
				builder.setMessage("Uygulamadan çıkmak istiyor musunuz?");
				builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				builder.setNegativeButton("Hayır", null);
				builder.show();
			}
		});


		
		//custom code goes here
	
	}

}
	
	