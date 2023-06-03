package com.ezgi.gpfigma;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class ConvertingActivity extends Activity {

	ImageView listen,goBack,translate,save;

	RelativeLayout clear,copy, addPhoto;
	EditText recogText;

	Uri imageUri;

	TextRecognizer textRecognizer;//ml kit ile geldi

	TextToSpeech textToSpeech;

	// Kamera izni için
	private static final int REQUEST_CAMERA_PERMISSION = 200;
	//depolama izni
	private static final int REQUEST_STORAGE_PERMISSION = 201;

	boolean cameraPermissionGranted = false;
	boolean storagePermissionGranted = false;

	// Kamera erişim izni kontrolü
	private boolean checkCameraPermission() {
		int result = ContextCompat.checkSelfPermission(ConvertingActivity.this, android.Manifest.permission.CAMERA);
		return result == PackageManager.PERMISSION_GRANTED;
	}
	//Depolamaya erişim izni kontrolü
	private boolean checkStoragePermission() {
		int resultRead = ContextCompat.checkSelfPermission(ConvertingActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
		return resultRead == PackageManager.PERMISSION_GRANTED;
	}

	// Kamera erişim izni isteği
	private void requestCameraPermission() {
		ActivityCompat.requestPermissions(ConvertingActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
	}
	// Depolamaya erişim izin isteği
	private void requestStoragePermission() {
		ActivityCompat.requestPermissions(ConvertingActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
	}

	// Kamera izni istendiğinde geri döndürülen sonuçları işleme
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_CAMERA_PERMISSION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// Kamera erişim izni verildi
				cameraPermissionGranted = true;
				// Kamera erişim izni verildi, depolama izni istenir
				if (!checkStoragePermission()) {
					requestStoragePermission();
				}
			}
			else {
				// Kamera erişim izni verilmedi
				Toast.makeText(ConvertingActivity.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
			}
		}
		else if (requestCode == REQUEST_STORAGE_PERMISSION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				storagePermissionGranted = true;
				// Depolama izni verildi
				Toast.makeText(ConvertingActivity.this, "Camera and Storage permission granted", Toast.LENGTH_SHORT).show();
			} else {
				// Depolama izni verilmedi
				Toast.makeText(ConvertingActivity.this, "Camera permission granted and Storage permission denied", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.convert);

		clear    = findViewById(R.id.clear_group);
		copy     = findViewById(R.id.copy_group);
		addPhoto = findViewById(R.id.add_group);
		goBack   = findViewById(R.id.note_icon);
		listen   = findViewById(R.id.exit_icon);
		translate= findViewById(R.id.translate_icon);
		save     = findViewById(R.id.save_icon);

		recogText= findViewById(R.id.recogText);

		textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

		goBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(ConvertingActivity.this, DashboardActivity.class);
						startActivity(intent);
					}
				}, 100); //delay in milliseconds, e.g. 1000 for 1 second delay
			}
		});

		addPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if(checkCameraPermission() || checkStoragePermission()){

					ImagePicker.with(ConvertingActivity.this)
							.crop()
							.compress(1024)
							.maxResultSize(1080,1080)
							.start();
				}
				else{
					// Kamera izni yok, izin isteği göster
					requestCameraPermission();
				}


			}
		});

		//copy button
		copy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String text = recogText.getText().toString();

				if(text.isEmpty()){

					Toast.makeText(ConvertingActivity.this,"There is no text to copy", Toast.LENGTH_SHORT).show();
				}
				else{
					ClipboardManager clipboardManager = (ClipboardManager) getSystemService(ConvertingActivity.this.CLIPBOARD_SERVICE);
					ClipData clipData = ClipData.newPlainText("Data",recogText.getText().toString());

					clipboardManager.setPrimaryClip(clipData);

					// Titreşim özelliğini kullanarak geri bildirim sağlama
					Vibrator vibrator = (Vibrator) getSystemService(ConvertingActivity.this.VIBRATOR_SERVICE);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
					} else {
						vibrator.vibrate(100);
					}

					Toast.makeText(ConvertingActivity.this,"Text is copied to Clipboard", Toast.LENGTH_SHORT).show();
				}
			}
		});

		//clear button
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String text = recogText.getText().toString();

				if(text.isEmpty()){

					Toast.makeText(ConvertingActivity.this,"There is no text to clear", Toast.LENGTH_SHORT).show();
				}
				else{
					recogText.setText("");
				}
			}
		});

		//listen button
		textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					// Başlatıldı
				} else {
					// Başlatılamadı
				}
			}
		});

		listen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = recogText.getText().toString();

				if(text.isEmpty()){
					Toast.makeText(ConvertingActivity.this,"There is no text to listen", Toast.LENGTH_SHORT).show();
				}
				else{
					textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
				}
			}
		});

		translate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = recogText.getText().toString();
				if(!text.isEmpty()){
					startActivity(new Intent(ConvertingActivity.this,AddNoteActivity.class));
				}
				else{
					Toast.makeText(ConvertingActivity.this,"There is no extracted text for saving", Toast.LENGTH_SHORT).show();
				}


			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode== Activity.RESULT_OK){

			if(data != null && data.getData() != null){
				//data!=null
				imageUri = data.getData();
				Toast.makeText(this,"Image is selected", Toast.LENGTH_SHORT).show();

				recognizeText();
			}
		}
		else{
			Toast.makeText(this,"Image is not selected", Toast.LENGTH_SHORT).show();
			// fotograf seçilmezse
		}
	}

	private void recognizeText(){

		if(imageUri != null){

			try {

				InputImage inputImage = InputImage.fromFilePath(ConvertingActivity.this,imageUri);

				Task<Text> result = textRecognizer.process(inputImage)
						.addOnSuccessListener(new OnSuccessListener<Text>() {
							@Override
							public void onSuccess(Text text) {

								String recognizeText = text.getText();
								recogText.setText(recognizeText);
							}
						}).addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {

								Toast.makeText(ConvertingActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
							}
						});
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
	
	