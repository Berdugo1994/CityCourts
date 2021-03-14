package com.example.mycitycourts;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
public class TermsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
    }

    public void CloseClicked(View view){
        finish();
    }
}