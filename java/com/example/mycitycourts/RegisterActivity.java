package com.example.mycitycourts;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Add User To Realtime DB Firebase.
 */
public class RegisterActivity extends AppCompatActivity {
    EditText  et_UserName,et_Password,et_Email,et_BirthDate,et_CellPhone;
    CheckBox cb_terms;
    String str_password;
    String str_username;
    String str_email;
    String str_birthdate;
    String str_cellphone;
    UserValidation userValidation;
    int maxid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getMeaning();
    }

    public void SignUp(View v){
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setFocusableInTouchMode(false);
        str_password = et_Password.getText().toString().trim();
        str_username = et_UserName.getText().toString().trim();
        str_email = et_Email.getText().toString().trim();
        str_birthdate = et_BirthDate.getText().toString().trim();
        str_cellphone = et_CellPhone.getText().toString().trim();
        if(userValidation.ClientSideCheck()) {
            if (!cb_terms.isChecked()) {
                cb_terms.setError("Must confirm to use City Court!");
                return;
            } else {
                cb_terms.setError(null);
                userValidation.DBCheck();
            }
        }
    }
    public void TermsOfUse(View views){
        Intent intent = new Intent(getApplicationContext(),TermsActivity.class);
        startActivity(intent);
    }

    private void getMeaning() {
        cb_terms = findViewById(R.id.cb_terms);
        et_UserName = findViewById(R.id.et_reg_username);
        et_Password = findViewById(R.id.et_reg_password);
        et_Email = findViewById(R.id.et_reg_email);
        et_BirthDate = findViewById(R.id.et_reg_birthdate);
        et_CellPhone = findViewById(R.id.et_reg_mob);
        userValidation = new UserValidation(et_UserName, et_Password, et_Email, et_BirthDate, et_CellPhone,this,this);
        maxid = 0;
        //id number take care.
    }
    public void Success(boolean Status){
        RegisterHelperClass CurUser = new RegisterHelperClass(str_username, str_password, str_email,str_birthdate,str_cellphone);
        FirebaseDatabase.getInstance().getReference("users").child(str_username).setValue(CurUser);
        Toast.makeText(this,"User Added !",Toast.LENGTH_LONG).show();
        finish();
    }

    public void CbChecked(View v){
        if (cb_terms.isChecked()){
            cb_terms.setError(null);
        }
    }

}