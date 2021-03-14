//package com.example.mycitycourts;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.CheckBox;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class RegTry extends AppCompatActivity {
//    CheckBox cb_terms;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.reg_activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        cb_terms = findViewById(R.id.cb_terms);
//    }
//
//    public void TermsOfUse(View views){
//        Intent intent = new Intent(getApplicationContext(),TermsActivity.class);
//        startActivity(intent);
//    }
////    public void CheckBoxClicked(View view){
////        if(!flag_terms_clicked)cb_terms.setError("Must read terms before approved");
////        else{
////            cb_terms.setError(null);
////            cb_terms.setChecked(true);
////        }
////    }
//
//}