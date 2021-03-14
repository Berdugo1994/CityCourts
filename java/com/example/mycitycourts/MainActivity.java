package com.example.mycitycourts;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mycitycourts.ObserverDesignPattern.MaxIdClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
/*
Login with Firebase -  Realtime DB.
 */
public class MainActivity extends AppCompatActivity {
    private EditText UserEt, PasswordEt;
    private ValueEventListener usersListener;
    FirebaseDatabase rootNode;
    DatabaseReference usersDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserEt = findViewById(R.id.tv_Username);
        PasswordEt = findViewById(R.id.tv_Password);
        rootNode = FirebaseDatabase.getInstance();
        usersDB = rootNode.getReference("users");
    }

    public void OnLogin(View view){
        final String userName = UserEt.getText().toString().trim();
        final String password = PasswordEt.getText().toString().trim();
        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Must full all the fields !",Toast.LENGTH_SHORT).show();
        }
        else {
            final Query checkUserName =usersDB.orderByChild("username").equalTo(userName);
            checkUserName.addListenerForSingleValueEvent(usersListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        String passwordFromDB= dataSnapshot.child(userName).child("password").getValue(String.class);
                        if(passwordFromDB!=null &&passwordFromDB.equals(password)){
                            Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                            intent.putExtra("level",dataSnapshot.child(userName).child("level").getValue(int.class));
                            intent.putExtra("username",userName);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this,"name/password wrong.",Toast.LENGTH_LONG).show(); }
                        }
                    else{
                        Toast.makeText(MainActivity.this,"name/password wrong.",Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public void OnRegister(View view){
        Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(i);
    }
}
