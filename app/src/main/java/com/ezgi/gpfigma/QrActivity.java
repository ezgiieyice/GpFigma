package com.ezgi.gpfigma;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;

public class QrActivity extends Activity {

	private CodeScanner mCodeScanner;
	private String scannedText;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.qr);

		CodeScannerView scannerView = findViewById(R.id.scanner);

		mCodeScanner = new CodeScanner(this, scannerView);
		mCodeScanner.setDecodeCallback(new DecodeCallback() {
			@Override
			public void onDecoded(@NonNull com.google.zxing.Result result) {
				scannedText = result.getText();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(QrActivity.this, scannedText, Toast.LENGTH_LONG).show();
					}
				});

			}
		});
		scannerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mCodeScanner.startPreview();
			}
		});

		scannerView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if(scannedText != null && !scannedText.isEmpty()){
					openLink(scannedText);
					return true;
				}
				return false;
			}
		});
	}
	private void openLink(String link) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(link));
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCodeScanner.startPreview();
	}

	@Override
	protected void onPause() {
		mCodeScanner.releaseResources();
		super.onPause();
	}
}



	