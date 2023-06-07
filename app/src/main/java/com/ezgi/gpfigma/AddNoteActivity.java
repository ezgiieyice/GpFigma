package com.ezgi.gpfigma;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.realm.Realm;

public class AddNoteActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.saving_note);

        EditText titleInput,textInput;
        Button saveBttn;
        RelativeLayout goBack;

        saveBttn = findViewById(R.id.saveBttn);
        goBack   = findViewById(R.id.arrow_back);
        titleInput  = findViewById(R.id.title);
        textInput   = findViewById(R.id.text);

        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();

        String previousText = getIntent().getStringExtra("text");
        textInput.setText(previousText);

        saveBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleInput.getText().toString();
                String text  = textInput.getText().toString();
                long createdTİme = System.currentTimeMillis();

                realm.beginTransaction();
                Note note = realm.createObject(Note.class);
                note.setTitle(title);
                note.setText(text);
                note.setCreatedTime(createdTİme);
                realm.commitTransaction();

                Toast.makeText(getApplicationContext(),"Note saved",Toast.LENGTH_SHORT).show();
                finish();


            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddNoteActivity.this,ConvertingActivity.class));
            }
        });

    }
}
