package com.example.mycitycourts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FieldActivity extends AppCompatActivity {
    DatabaseReference pointsDB;

    long Court_id;
    int MaxLevelUser;
    ImageView court_img;
    int Status;
    TextView tv_courtName, tv_size, tv_players, tv_description;
    Chip chip_fac1, chip_fac2, chip_fac3, chip_fac4, chip_fac5, chip_sport1, chip_sport2, chip_sport3, chip_sport4, chip_sport5, chip_sport6;
    double latitude, longitude;
    Chip[] SportChips;
    CourtHelper point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);
        Intent receivedIntent = getIntent();
        Bundle extras = receivedIntent.getExtras();
        assert extras != null;
        point =(CourtHelper) receivedIntent.getSerializableExtra("Point");
        MaxLevelUser = extras.getInt("Level", 0);
        getMeaning();
        setDetails(point);
    }

    private void FetchImageToIV(String path) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageReference.child("images/" + path);
        GlideApp.with(this).load(photoReference).into(court_img);
    }

    public void setDetails(CourtHelper point) {
        String Description = point.getDescription();
        String fac = point.getFac();
        String imagePath = point.getImg();
        String sports = point.getType();
        String size = point.getSize();
        int num = point.getNum();
        latitude = point.getLatitude();
        longitude = point.getLongitude();
        Status = point.getStatus();
        Court_id = point.getCourtId();
        tv_courtName.setText(point.getName());
        tv_size.setText(size);
        tv_players.setText(String.valueOf(num));
        // 4 ,5 long and latitude
        switch (fac.charAt(0)) {
            case ('n'):
                chip_fac1.setText(R.string.no);
                break;
            case ('s'):
                chip_fac1.setText(R.string.small);
                break;
            case ('m'):
                chip_fac1.setText(R.string.med);
                break;
            case ('l'):
                chip_fac1.setText(R.string.lar);
                break;
        }
        switch (fac.charAt(2)) {
            case ('n'):
                chip_fac2.setText(R.string.no);
                break;
            case ('y'):
                chip_fac2.setText(R.string.yes);
                break;
        }
        switch (fac.charAt(1)) {
            case ('n'):
                chip_fac3.setText(R.string.no);
                break;
            case ('y'):
                chip_fac3.setText(R.string.yes);
                break;
        }
        switch (fac.charAt(3)) {
            case ('n'):
                chip_fac4.setText(R.string.no);
                break;
            case ('y'):
                chip_fac4.setText(R.string.yes);
                break;
        }
        switch (fac.charAt(4)) {
            case ('n'):
                chip_fac5.setText(R.string.no);
                break;
            case ('y'):
                chip_fac5.setText(R.string.yes);
                break;
        }
        int sport_chip_num = 0;
        if (sports.contains("f")) {
            SportChips[sport_chip_num].setChipIcon(ContextCompat.getDrawable(this, R.drawable.iconfootball));
            SportChips[sport_chip_num].setVisibility(View.VISIBLE);
            sport_chip_num++;
        }
        if (sports.contains("b")) {
            SportChips[sport_chip_num].setChipIcon(ContextCompat.getDrawable(this, R.drawable.iconbasketball));
            SportChips[sport_chip_num].setVisibility(View.VISIBLE);
            sport_chip_num++;
        }
        if (sports.contains("t")) {
            SportChips[sport_chip_num].setChipIcon(ContextCompat.getDrawable(this, R.drawable.icontennis));
            SportChips[sport_chip_num].setVisibility(View.VISIBLE);
            sport_chip_num++;
        }
        if (sports.contains("g")) {
            SportChips[sport_chip_num].setChipIcon(ContextCompat.getDrawable(this, R.drawable.icongym));
            SportChips[sport_chip_num].setVisibility(View.VISIBLE);
            sport_chip_num++;
        }
        if (sports.contains("v")) {
            SportChips[sport_chip_num].setChipIcon(ContextCompat.getDrawable(this, R.drawable.iconvolley));
            SportChips[sport_chip_num].setVisibility(View.VISIBLE);
            sport_chip_num++;
        }
        if (sports.contains("s")) {
            SportChips[sport_chip_num].setChipIcon(ContextCompat.getDrawable(this, R.drawable.iconswim));
            SportChips[sport_chip_num].setVisibility(View.VISIBLE);
        }
        if (imagePath.equals("NO")) {
            FetchImageToIV("notyet.png");
        } else {
            FetchImageToIV(imagePath);
        }
        tv_description.setText(Description);
        if (MaxLevelUser == 1) GiveAdminPermission();
    }

    private void getMeaning() {
        pointsDB = FirebaseDatabase.getInstance().getReference("points");
        tv_courtName = findViewById(R.id.tv_af_CourtName);
        tv_size = findViewById(R.id.tv_af_Size);
        tv_players = findViewById(R.id.tv_af_NumOfParticipants);
        chip_fac1 = findViewById(R.id.fac1);
        chip_fac2 = findViewById(R.id.fac2);
        chip_fac3 = findViewById(R.id.fac3);
        chip_fac4 = findViewById(R.id.fac4);
        chip_fac5 = findViewById(R.id.fac5);
        chip_sport1 = findViewById(R.id.chip_sport1);
        chip_sport2 = findViewById(R.id.chip_sport2);
        chip_sport3 = findViewById(R.id.chip_sport3);
        chip_sport4 = findViewById(R.id.chip_sport4);
        chip_sport5 = findViewById(R.id.chip_sport5);
        chip_sport6 = findViewById(R.id.chip_sport6);
        SportChips = new Chip[]{chip_sport1, chip_sport2, chip_sport3, chip_sport4, chip_sport5, chip_sport6};
        court_img = findViewById(R.id.IV_af);
        tv_description = findViewById(R.id.tv_af_description);
    }

    //Manager Funcs
    private void GiveAdminPermission() {
        Button Confirm = findViewById(R.id.confirm_button);
        Button Cancel = findViewById(R.id.cancel_button);
        if (Status == 0) Confirm.setVisibility(View.VISIBLE);
        Cancel.setVisibility(View.VISIBLE);
        Confirm.setClickable(true);
        Cancel.setClickable(true);
        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FieldActivity.this)
                        .setTitle("Confrim Court")
                        .setMessage("Are you sure you want to confirm the court and the details?")
                        .setPositiveButton("Yes,I do.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                pointsDB.getRef().child(point.getName()).child("status").setValue(1);
                                finish();
                            }
                        })
                        .setNegativeButton("No,I don't.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {//Nothing
                            }
                        }).show();
            }

        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(FieldActivity.this)
                        .setTitle("Cancel Court")
                        .setMessage("Are you sure you want to Delete the court and the details?")
                        .setPositiveButton("Yes,I do.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                pointsDB.getRef().child(point.getName()).removeValue();
                                finish();
                            }
                        })
                        .setNegativeButton("No,I don't.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {//Nothing
                            }
                        }).show();
            }
        });
    }

    public void NavToPoint(View view) {
        String url = "https://www.google.com/maps/dir/?api=1&destination=" + latitude + "," + longitude + "&travelmode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
