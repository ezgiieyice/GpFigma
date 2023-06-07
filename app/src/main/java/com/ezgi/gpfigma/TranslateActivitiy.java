package com.ezgi.gpfigma;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TranslateActivitiy extends Activity {

    //UI Views
    private EditText source;
    private TextView target;
    private RelativeLayout goBack;
    private MaterialButton sourceLangBtton,destinationLangChooseBttn,translateBtn;

    //Translator options to set source and dest languages English -> Turkish
    private TranslatorOptions translatorOptions;
    //Translator object, for configuring it with the source and target lang.es:
    private Translator translator;

    private ProgressDialog progressDialog;

    //will contain list with language code and title
    private ArrayList<ModelLanguage> languageArrayList;

    //to show logs
    private static final String TAG = "MAIN_TAG";

    private String sourceLanguageCode = "en";
    private String sourceLanguageTitle = "English";
    private String destinationLanguageCode = "tr";
    private String destinationLanguageTitle = "Türkçe";


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate);

        goBack = findViewById(R.id.arrow_back);
        source = findViewById(R.id.sourceLang);
        target = findViewById(R.id.targetLang);
        sourceLangBtton = findViewById(R.id.sourceLangChooseBtn);
        destinationLangChooseBttn = findViewById(R.id.destinationLangChooseBtn);
        translateBtn = findViewById(R.id.translateBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        String previousText = getIntent().getStringExtra("text");
        source.setText(previousText);

        loadAvailableLanguages();

        sourceLangBtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceLanguageChoose();
            }
        });

        destinationLangChooseBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationLanguageChoose();
            }
        });

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TranslateActivitiy.this, ConvertingActivity.class));
            }
        });
    }

    private String sourceLanguageText = "";
    private void validateData(){
        sourceLanguageText = source.getText().toString().trim();

        Log.d(TAG,"validateData: sourceLanguageText: "+ sourceLanguageText);

        if(sourceLanguageText.isEmpty()){
            Toast.makeText(this,"Enter text to translate...",Toast.LENGTH_SHORT).show();
        }
        else{
            startTranslations();
        }
    }
    private void startTranslations(){
        progressDialog.setMessage("Processing language model...");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(destinationLanguageCode)
                .build();

        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"onSuccess: model ready,starting translate...");
                        progressDialog.setMessage("Translating...");
                        translator.translate(sourceLanguageText)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String translatedText) {
                                        //successfully translated
                                        Log.d(TAG,"onSuccess: translatedText: "+translatedText);
                                        progressDialog.dismiss();

                                        target.setText(translatedText);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed to translate
                                        progressDialog.dismiss();
                                        Log.d(TAG,"onFailure: ",e);
                                        Toast.makeText(TranslateActivitiy.this,"Failed to translate due to "+ e.getMessage(),Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onFailure: ",e);
                        Toast.makeText(TranslateActivitiy.this,"Failed to translate due to "+ e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });




    }
    private void sourceLanguageChoose(){

        PopupMenu popupMenu = new PopupMenu(this,sourceLangBtton);

        for(int i = 0; i<languageArrayList.size(); i++){
            popupMenu.getMenu().add(Menu.NONE,i,i,languageArrayList.get(i).languageTitle);

        }
        popupMenu.getMenu();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int position = item.getItemId();

                sourceLanguageCode = languageArrayList.get(position).languageCode;
                sourceLanguageTitle = languageArrayList.get(position).languageTitle;

                sourceLangBtton.setText(sourceLanguageTitle);
                source.setHint("Enter: " + sourceLanguageTitle);

                //show in logs
                Log.d(TAG,"onMenuItemClick: sourceLanguageCode: " +sourceLanguageCode);
                Log.d(TAG,"onMenuItemClick: sourceLanguageTitle: " +sourceLanguageTitle);

                return false;
            }
        });
    }

    private void destinationLanguageChoose(){

        PopupMenu popupMenu = new PopupMenu(this,destinationLangChooseBttn);

        for(int i = 0; i<languageArrayList.size(); i++){
            popupMenu.getMenu().add(Menu.NONE,i,i,languageArrayList.get(i).languageTitle);

        }
        popupMenu.getMenu();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int position = item.getItemId();

                destinationLanguageCode = languageArrayList.get(position).languageCode;
                destinationLanguageTitle = languageArrayList.get(position).languageTitle;

                destinationLangChooseBttn.setText(destinationLanguageTitle);

                //show in logs
                Log.d(TAG,"onMenuItemClick: destinationLanguageCode: " +destinationLanguageCode);
                Log.d(TAG,"onMenuItemClick: destinationLanguageTitle: " +destinationLanguageTitle);

                return false;
            }
        });

    }

    private void loadAvailableLanguages() {

        languageArrayList = new ArrayList<>();

        List<String> languageCodeList = TranslateLanguage.getAllLanguages();

        for(String languageCode:languageCodeList){
            String languageTitle = new Locale(languageCode).getDisplayLanguage();//en->english
            Log.d(TAG,"loadAvailableLanguages: languageCode: " +languageCode);
            Log.d(TAG,"loadAvailableLanguages: languageTitle: " +languageTitle);

            ModelLanguage modelLanguage = new ModelLanguage(languageCode,languageTitle);
            languageArrayList.add(modelLanguage);


        }
    }

}
