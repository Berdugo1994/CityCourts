package com.example.mycitycourts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycitycourts.ObserverDesignPattern.MaxIdClass;
import com.example.mycitycourts.ObserverDesignPattern.MyObserverImpl;
import com.example.mycitycourts.ObserverDesignPattern.SubjectObserverable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, MyObserverImpl {
    Location bestLocation;
    private GoogleMap mMap;
    final int FINE_REQ_CODE = 99;
    private ChildEventListener childEventListener;
    private Marker LastPoint;
    private String user_name;
    private LocationManager locationManager;
    private boolean FineLocationPermission;
    int MaxUserLevel, MaxIdNow;
    Spinner SpinnerSport;
    int SportLastChoose;
    int UserLastChoose;
    Boolean FirstEnterToResume;
    private LinearLayout LL_DownBar;
    boolean HidePartBar;
    int childCounter;
    private Map<Marker, String> Map_MarkerAndStatusAndType;
    private MaxIdClass maxIdClass;
    private DatabaseReference pointsDB;
    private TextView tv_NumOfPoints;
    private boolean FirstLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        pointsDB=FirebaseDatabase.getInstance().getReference("points");
        Intent receivedIntent = getIntent();
        Bundle extras = receivedIntent.getExtras();
        assert extras != null;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        MaxUserLevel = extras.getInt("level", 0);
        user_name = extras.getString("username", "");
        Toast.makeText(this,"Welcome "+user_name+" !",Toast.LENGTH_LONG).show();
        maxIdClass = new MaxIdClass();
        maxIdClass.register(this);
        childCounter = 0;
        FirstLoad=true;
        Log.d("Happens", " On Create has happend");
        FineLocationPermission = false;
        FirstEnterToResume = true;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        HidePartBar = false;
        mapFragment.getMapAsync(this);
        bestLocation = null;
        checkLocationPermission();
        SportLastChoose = 0;
        UserLastChoose = 2;
        getMeaning();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Happens", " OnMapReady has happend");
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        FindMyLocation();
        if (bestLocation != null) {
            LatLng CurPos = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CurPos, 12.50f));
        } else {
            LatLng Haifa = new LatLng(32.794044, 34.989571);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Haifa, 12.50f));
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final String Court_Name = marker.getTitle();
                final Query checkCourt = pointsDB.orderByChild("name").equalTo(Court_Name);
                checkCourt.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            CourtHelper point = dataSnapshot.child(Court_Name).getValue(CourtHelper.class);
                            if (point == null) return;
                            Intent i = new Intent(getApplicationContext(), FieldActivity.class);
                            i.putExtra("Level",MaxUserLevel);
                            i.putExtra("Point",point);
                            startActivity(i);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                return false;
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng arg0) {
                if (LastPoint != null) {
                    LastPoint.remove();
                }
                LastPoint = mMap.addMarker(new MarkerOptions()
                        .position(
                                new LatLng(arg0.latitude,
                                        arg0.longitude))
                        .draggable(true).visible(true));

            }
        });
        if (MaxUserLevel == 1) setAdminPriviliges();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * SetData gets string of all data points , creats a list and take care each point.
     */


    public void AddCourt(View view) {
        if (LastPoint != null) {
            Intent i = new Intent(getApplicationContext(), AddLoctaionActivity.class);
            i.putExtra("Latitude", Double.toString(LastPoint.getPosition().latitude));
            i.putExtra("Longitude", Double.toString(LastPoint.getPosition().longitude));
            i.putExtra("UserName", user_name);
            i.putExtra("UserMaxLevel", MaxUserLevel);
            i.putExtra("MaxId", MaxIdNow);
            LastPoint.remove();
            LastPoint = null;
            startActivity(i);
        }
    }

    private long lastPressTime;

    @SuppressLint("MissingPermission")
    //Requested before entering Here - Permission Checked
    public Location FindMyLocation() {
        if (!FineLocationPermission) {
            Toast.makeText(this, "Permessioned didn't given", Toast.LENGTH_LONG).show();
            return null;
        }
        if (LastPoint != null) {
            LastPoint.remove();
            LastPoint = null;
        }
        return getLastKnownLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider.equals("gps")) checkLocationPermission();
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals("gps")) {
            FineLocationPermission = false;
        }
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Request")
                        .setMessage("We need yourlocation permession to enable add field in your current position")
                        .setPositiveButton("I Understand.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        FINE_REQ_CODE);
                            }
                        }).create().show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_REQ_CODE);
            }
        } else {
            FineLocationPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FINE_REQ_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        FineLocationPermission = true;
                        FindMyLocation();
                        if (bestLocation != null) {
                            LatLng CurPos = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CurPos, 12.50f));
                        }
                    }
                } else {
                    // permission denied
                    FineLocationPermission = false;
                    break;
                }
            }
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        if (locationManager == null) {
            return bestLocation;
        }
        List<String> providers = locationManager.getProviders(false);
        for (String provider : providers) {
            if (provider.equals("passive") || provider.equals("gps") || provider.equals("network")) {
                @SuppressLint("MissingPermission")
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                Log.d("provider", "provider :" + provider + " accurate:" + l.getAccuracy());
                //TODO :: <100 new rule added. need to check
                if ((bestLocation == null || (l.getAccuracy() < bestLocation.getAccuracy())) && l.getAccuracy() < 100) {
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }

    private void getMeaning() {
        tv_NumOfPoints=findViewById(R.id.tvNumOfPoints);
        SpinnerSport = findViewById(R.id.SpinnerfilterSport);
        LL_DownBar = findViewById(R.id.LL_DownBar);
        final String[] years = {"Show all", "Football", "Basketball", "Tennis", "Gym", "Volleyball", "Pools"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_text_sport, years);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        SpinnerSport.setAdapter(langAdapter);
        SpinnerSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (SportLastChoose != position) {
                    SportLastChoose = position;
                    ShowRelevantPoints();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }


    private void setAdminPriviliges() {
        UserLastChoose = 0;
        Spinner SpinnerAdmin = findViewById(R.id.SpinnerfilterAdmin);
        SpinnerAdmin.setVisibility(View.VISIBLE);
        SpinnerAdmin.setClickable(true);
        final String[] status = {"Show all", "Wait", "Approved"};
        ArrayAdapter<CharSequence> langAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_text_admin, status);
        langAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        SpinnerAdmin.setAdapter(langAdapter);
        SpinnerAdmin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (UserLastChoose != position) {
                    UserLastChoose = position;
                    ShowRelevantPoints();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
    }


    public void onNavButtonClick(View view) {
        Location location = FindMyLocation();
        if (location == null) return;
        long pressTime = System.currentTimeMillis();
        if (pressTime - lastPressTime <= 250) {//Double Clicked !
            if (bestLocation != null) {
                LatLng CurPos = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(CurPos, 12.50f));
            }
        } else {//Single Click or On Creation
            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            LastPoint = mMap.addMarker(new MarkerOptions().position(myLatLng).draggable(true).visible(true));
        }
        lastPressTime = pressTime;
    }


    private void Listening() {
        if (MaxIdNow == 0) return;
        //PART OF OnCreate !
        //Called Once
        Map_MarkerAndStatusAndType = new HashMap<>();
        pointsDB.addChildEventListener(childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("Child Added", Objects.requireNonNull(snapshot.getKey()));
                CourtHelper point = snapshot.getValue(CourtHelper.class);
                if (point == null) {
                    Log.d("Null", "Null point OnChildAdded Listener");
                    return;
                }
                childCounter++;
                if (point.getStatus() == 1 || (point.getStatus() == 0 && MaxUserLevel == 1)) {
                    LatLng p = new LatLng(point.getLatitude(), point.getLongitude());
                    Marker curMarker = mMap.addMarker(new MarkerOptions().position(p).title(point.getName()).visible(false));
                    String type = point.getType();
                    int status = point.getStatus();
                    if (type.length() > 1) {
                        curMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconmulti));
                    } else {
                        switch (type) {
                            case ("f"):
                                curMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconfootball));
                                break;
                            case ("b"):
                                curMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconbasketball));
                                break;
                            case ("g"):
                                curMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icongym));
                                break;
                            case ("t"):
                                curMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icontennis));
                                break;
                            case ("v"):
                                curMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconvolley));
                                break;
                            case ("s"):
                                curMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.iconswim));
                                break;
                        }
                    }
                    Map_MarkerAndStatusAndType.put(curMarker, point.getStatus().toString() + type);

                    if (childCounter >= MaxIdNow) {//On Load
                        if(FirstLoad) {
                            Log.d("SUCCESS", "child counter = maxid " + MaxIdNow);
                            ShowRelevantPoints();
                            FirstLoad = false;
                        }
                     else ShowSinglePoint(type,status,curMarker);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                CourtHelper point = snapshot.getValue(CourtHelper.class);
                if (point == null) {
                    Log.d("Child Remove", "point null");
                    return;
                }
                for (Marker marker : Map_MarkerAndStatusAndType.keySet())
                {
                    if (marker.getTitle().equals(point.getName())) {//The Point That should be removed
                        Map_MarkerAndStatusAndType.put(marker, point.getStatus().toString() + Objects.requireNonNull(Map_MarkerAndStatusAndType.get(marker)).substring(1));
                        Log.d("Point Status Changed", " To "+point.getStatus().toString());
                        ShowSinglePoint(point.getType(),point.getStatus(),marker);
                        break;

                    }
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                CourtHelper point = snapshot.getValue(CourtHelper.class);
                if (point == null) {
                    Log.d("Child Remove", "point null");
                    return;
                }
                String courtName = point.getName();
                if (point.getStatus() == 1 || (point.getStatus() == 0 && MaxUserLevel == 1)) {//Point Should be in Dictionary Map_MarkerAndStatusAndType
                    for (Marker marker : Map_MarkerAndStatusAndType.keySet()) {
                        if (marker.getTitle().equals(courtName)) {//The Point That should be removed
                            marker.remove();
                            Map_MarkerAndStatusAndType.remove(marker);
                            Log.d("Point Remove", courtName);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void ShowRelevantPoints() {
        String StatusAndType;
        for (Marker mo : Map_MarkerAndStatusAndType.keySet()) {
            StatusAndType = Map_MarkerAndStatusAndType.get(mo);
            assert StatusAndType != null;
            if (UserLastChoose != 1 && StatusAndType.charAt(0) == '1') {//Wanted View
                if (SportLastChoose == 0) {
                    mo.setVisible(true);
                } else if (SportLastChoose == 1 && StatusAndType.contains("f")) mo.setVisible(true);
                else if (SportLastChoose == 2 && StatusAndType.contains("b")) mo.setVisible(true);
                else if (SportLastChoose == 3 && StatusAndType.contains("t")) mo.setVisible(true);
                else if (SportLastChoose == 4 && StatusAndType.contains("g")) mo.setVisible(true);
                else if (SportLastChoose == 5 && StatusAndType.contains("v")) mo.setVisible(true);
                else if (SportLastChoose == 6 && StatusAndType.contains("s")) mo.setVisible(true);
                else {
                    mo.setVisible(false);
                }
            }
            else if(UserLastChoose != 2 && StatusAndType.charAt(0) == '0'){
                if (SportLastChoose == 0)mo.setVisible(true);
                else if (SportLastChoose == 1 && StatusAndType.contains("f")) mo.setVisible(true);
                else if (SportLastChoose == 2 && StatusAndType.contains("b")) mo.setVisible(true);
                else if (SportLastChoose == 3 && StatusAndType.contains("t")) mo.setVisible(true);
                else if (SportLastChoose == 4 && StatusAndType.contains("g")) mo.setVisible(true);
                else if (SportLastChoose == 5 && StatusAndType.contains("v")) mo.setVisible(true);
                else if (SportLastChoose == 6 && StatusAndType.contains("s")) mo.setVisible(true);
                else mo.setVisible(false);
            }
            else{
                mo.setVisible(false);
            }
        }
    }

    private void ShowSinglePoint(String type,int status,Marker curMarker){
        if (UserLastChoose != 1 && status == 1) {//Approved View
            if (SportLastChoose == 0)curMarker.setVisible(true);
            else if (SportLastChoose == 1 && type.equals("f")) curMarker.setVisible(true);
            else if (SportLastChoose == 2 && type.equals("b")) curMarker.setVisible(true);
            else if (SportLastChoose == 3 && type.equals("g")) curMarker.setVisible(true);
            else if (SportLastChoose == 4 && type.equals("t")) curMarker.setVisible(true);
            else if (SportLastChoose == 5 && type.equals("v")) curMarker.setVisible(true);
            else if (SportLastChoose == 6 && type.equals("s")) curMarker.setVisible(true);
            else {
                curMarker.setVisible(false);
            }
        }
        else if(UserLastChoose != 2 && status==0){
            if (SportLastChoose == 0)curMarker.setVisible(true);
            else if (SportLastChoose == 1 && type.equals("f")) curMarker.setVisible(true);
            else if (SportLastChoose == 2 && type.equals("b")) curMarker.setVisible(true);
            else if (SportLastChoose == 3 && type.equals("g")) curMarker.setVisible(true);
            else if (SportLastChoose == 4 && type.equals("t")) curMarker.setVisible(true);
            else if (SportLastChoose == 5 && type.equals("v")) curMarker.setVisible(true);
            else if (SportLastChoose == 6 && type.equals("s")) curMarker.setVisible(true);
            else curMarker.setVisible(false);
        }
        else{
            curMarker.setVisible(false);
        }
    }

    @Override
    public void update(int x) {
        if (MaxIdNow == 0) {
            MaxIdNow = x;
            Listening();
        } else {
            MaxIdNow = x;
        }
        tv_NumOfPoints.setText("Points: "+MaxIdNow);
    }

    @Override
    public void setSubject(SubjectObserverable sub) {
        sub.register(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Happens", " Stop has happend");
    }

    @Override
    public void finish() {
        super.finish();
        Log.d("Happens", " finish has happend");
        FirebaseDatabase.getInstance().getReference("points").removeEventListener(childEventListener);
        maxIdClass.DeleteListener();
    }
}
//
//    private void setMargins(View view, int left, int top, int right, int bottom) {
//        if (view == null) return;
//        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
//            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
//            p.setMargins(left, top, right, bottom);
//            view.requestLayout();
//        }
//    }