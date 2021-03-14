package com.example.mycitycourts;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mycitycourts.ObserverDesignPattern.MaxIdClass;
import com.example.mycitycourts.ObserverDesignPattern.MyObserverImpl;
import com.example.mycitycourts.ObserverDesignPattern.SubjectObserverable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
public class AddLoctaionActivity extends AppCompatActivity {
    private MaxIdClass maxIdClass;
    private String img_path;
    private ImageView imageView;
    private Uri filePath;
    final int STORAGE_REQ = 98;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference pointsDB;
    Query checkFieldName;
    private boolean Upl_Status;
    private final int PICK_IMAGE_REQUEST = 50;
    private EditText et_court_name,et_size_width,et_size_height,et_numOfParticipants,et_Description;
    private CheckBox cb_water, cb_lights,
            cb_shades, cb_seats;
    private Spinner spin_Parking;
    private Chip chip_football,chip_basketball,chip_tennis,chip_gym,chip_volley, chip_swim;
    private String field_name,field_fac, field_type,Longitude,Latitude,User_name, field_width, field_height,field_des,field_num;
    private TextView tv_types;
    private int UserMaxLevel;
    private boolean GalleryPermission;
    ValueEventListener maxIdListener,nameListener;
    private int MaxIdNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_loctaion);
        pointsDB=FirebaseDatabase.getInstance().getReference("points");
        Upl_Status=false;
        img_path="NO";
        GalleryPermission=false;
        Intent receivedIntent = getIntent();
        Bundle extras = receivedIntent.getExtras();
        assert extras != null;
        Latitude = extras.getString("Latitude", "");
        Longitude = extras.getString("Longitude", "");
        User_name= extras.getString("UserName", "");
        UserMaxLevel =extras.getInt("UserMaxLevel",0);
        MaxIdNow = extras.getInt("MaxId",0);
        getMeaning();
        checkGalleryPermission();
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //TODO Still not totally works must fix
                if( !ImageSizeReasonble(filePath)){
                    Toast.makeText(this,"Image Is Bigger than 512KB!",Toast.LENGTH_LONG).show();
                    return;
                }
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        Upl_Status=false;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            img_path=UUID.randomUUID().toString();
            StorageReference ref = storageReference.child("images/"+ img_path);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddLoctaionActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddLoctaionActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                            if((int)progress==100){
                                Upl_Status=true;
                            }
                        }
                    });

        }
    }
    public void FinishClicked(View view){
        field_type ="";
        field_fac ="";
        field_name=et_court_name.getText().toString().trim();
        field_width =et_size_width.getText().toString().trim();
        field_height =et_size_height.getText().toString().trim();
        field_des=et_Description.getText().toString().trim();
        field_num=et_numOfParticipants.getText().toString().trim();
        if (field_name.isEmpty()) {
            et_court_name.setError("Must be filled");
            return;
        }
        if(field_name.contains("#")||field_name.contains(".")||field_name.contains("$")||field_name.contains("]")||field_name.contains("[")){
            et_court_name.setError("Can't contain .#$[] ");
            return;
        }
        //check emptiness
        if (field_width.isEmpty()) {
            et_size_width.setError("Bad court width");
            return;
        }
        if(!android.text.TextUtils.isDigitsOnly(field_width)){
            et_size_width.setError("Must contain only digits");
            return;
        }
        if (field_height.isEmpty()) {
            et_size_width.setError("Bad court height");
            return;
        }
        if(!android.text.TextUtils.isDigitsOnly(field_height)){
            et_size_height.setError("Must contain only digits");
            return;
        }
        if (field_num.isEmpty()) {
            et_numOfParticipants.setError("Must be filled");
            return;
        }
        if(!android.text.TextUtils.isDigitsOnly(field_num)){
            et_numOfParticipants.setError("Must contain only digits");
            return;
        }
        switch (spin_Parking.getSelectedItemPosition()){
            case 0:
                field_fac ="n";
                break;
            case 1:
                field_fac ="s";
                break;
            case 2:
                field_fac ="m";
                break;
            case 3:
                field_fac ="l";
        }
        int chipVnum=0;
        if(cb_water.isChecked()) field_fac +="y";
        else{
            field_fac +="n";}
        if(cb_lights.isChecked()) field_fac +="y";
        else field_fac +="n";
        if(cb_shades.isChecked()) field_fac +="y";
        else field_fac +="n";
        if(cb_seats.isChecked()) field_fac +="y";
        else field_fac +="n";
        if(chip_football.isChecked()){
            field_type +="f";chipVnum++;}
        if(chip_basketball.isChecked()){
            field_type +="b";chipVnum++;}
        if(chip_tennis.isChecked()){
            field_type +="t";chipVnum++;}
        if(chip_gym.isChecked()){
            field_type +="g";chipVnum++;}
        if(chip_volley.isChecked()){
            field_type +="v";chipVnum++;}
        if(chip_swim.isChecked()){
            field_type +="s";chipVnum++;}
        if(chipVnum<1) {
            tv_types.setError("Pick at least one");
            return;
        }
        tv_types.setError(null);
        BrowseAndNotUploadCheck();
    }

    private void getMeaning(){
        //Spinner - parking take care
        spin_Parking = findViewById(R.id.spinner1);
        String[] items = new String[]{"No", "Small", "Medium","Large"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spin_Parking.setAdapter(adapter);
        Button btnChoose = findViewById(R.id.btnChoose);
        Button btnUpload = findViewById(R.id.btnUpload);
        imageView = findViewById(R.id.imgView);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GalleryPermission) {
                    chooseImage();
                }
                else{
                    Toast.makeText(AddLoctaionActivity.this,"Permission is not allowed.",Toast.LENGTH_LONG).show();
                }
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
        et_court_name=findViewById(R.id.et_al_CourtName);
        et_numOfParticipants=findViewById(R.id.et_al_numOfPlayers);
        et_size_width=findViewById(R.id.et_al_Size_width);
        et_size_height=findViewById(R.id.et_al_Size_height);
        cb_lights =findViewById(R.id.et_al_light);
        cb_water =findViewById(R.id.et_al_Water);
        cb_shades =findViewById(R.id.et_al_Shades);
        cb_seats =findViewById(R.id.et_al_Seats);
        chip_basketball=findViewById(R.id.chip_al_type_basketball2);
        chip_football=findViewById(R.id.chip_al_type_football1);
        chip_gym=findViewById(R.id.chip_al_type_gym4);
        chip_tennis=findViewById(R.id.chip_al_type_tennis3);
        chip_volley=findViewById(R.id.chip_al_type_volley5);
        chip_swim =findViewById(R.id.chip_al_type_swim6);
        et_Description=findViewById(R.id.addDescription);
        tv_types=findViewById(R.id.et_al_Types);
    }
    private void BrowseAndNotUploadCheck(){
        if(!Upl_Status){
            new AlertDialog.Builder(this)
                    .setTitle("No photo uploaded")
                    .setMessage("Are you sure you want to upload court without photo?")
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            CheckAtServer();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            CheckAtServer();
        }
    }

    private void CheckAtServer() {
        checkFieldName = pointsDB.orderByChild("name").equalTo(field_name);
        checkFieldName.addListenerForSingleValueEvent(nameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    et_court_name.setError("Field Name exists");
                    et_court_name.requestFocus();
                } else {
                    et_court_name.setError(null);
//                    getMaxIdThenUpload();
                    if(MaxIdNow!=0)UploadToServer();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

//
//
//    private void getMaxIdThenUpload(){
//        pointsDB.addValueEventListener(maxIdListener= new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()) {
//                    for (DataSnapshot d: dataSnapshot.getChildren()){
//                        Log.d("key child",d.getKey());
//                    }
//                    maxid = dataSnapshot.getChildrenCount();
//                    if(maxid==0){
//                        Toast.makeText(AddLoctaionActivity.this,"Could Not found id number of point. Not added !",Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    UploadToServer();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }
    private void UploadToServer(){
        if(!Upl_Status)img_path="NO";
        CourtHelper newCourt=new CourtHelper(field_name,Double.parseDouble(Latitude),Double.parseDouble(Longitude),field_type,MaxIdNow+1,Integer.parseInt(field_num),field_fac,img_path,User_name,field_des,UserMaxLevel,field_width+"X"+field_height);
        pointsDB.child(field_name).setValue(newCourt);
        finish();
    }


    public boolean checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Request")
                        .setMessage("A permission is required to access the gallery,so you can upload a picture of the court. ")
                        .setPositiveButton("I Understand.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(AddLoctaionActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_REQ
                                );}
                        }).create().show();
            } else
            {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_REQ);
            }
            return false;
        }
        else
        {
            //Didnt Need to ask for permission - its was allowed
            GalleryPermission =true;
        }
        return(GalleryPermission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        GalleryPermission =true;
                    }
                }
                else {
                    // permission denied
                    GalleryPermission =false;
                    break;
                }
            }
        }
    }

    @Override
    protected void onStop() {
        if (checkFieldName != null && nameListener != null) {
            checkFieldName.removeEventListener(nameListener);
        }
        if ( maxIdListener != null) {
            pointsDB.removeEventListener(maxIdListener);
        }
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    protected boolean GreaterThanHalfMB(Bitmap data) {
        Log.d("IMG Size"," "+ data.getByteCount());
        return data.getByteCount()>8847360;
    }
    private boolean ImageSizeReasonble(Uri choosen) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), choosen);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        long lengthbmp = imageInByte.length/2100;
        return (lengthbmp<500);
    }
}