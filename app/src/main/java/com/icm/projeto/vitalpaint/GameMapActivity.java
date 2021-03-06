package com.icm.projeto.vitalpaint;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icm.projeto.vitalpaint.Data.GameMode;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final double SMOOTHING_FACTOR_COMPASS = 0.9;
    private GoogleMap mMap;
    private Map<String, Marker> lastestPlayerMarkers  = new HashMap<>();
    private String myName;
    private String gameName;
    private String myTeam = "";
    private String enemyTeam = "";
    private String startDate;
    private String zone;
    private DatabaseReference dbRef;
    private String userEmail;
    public static final float METERSTOSEEENEMIES = 100;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button btnGotKilled;
    private LinearLayout endGame;
    private GameMode gameMode;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);

        user = FirebaseAuth.getInstance().getCurrentUser();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//nao bloquear o ecra

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        gameName = getIntent().getStringExtra("gameName");
        myTeam = getIntent().getStringExtra("myTeam");
        myName = getIntent().getStringExtra("userName");
        startDate = getIntent().getStringExtra("startDate");
        zone = getIntent().getStringExtra("zone");
        //Log.i("gamemode", getIntent().getStringExtra("gameMode")+"");
        gameMode = GameMode.valueOf(getIntent().getStringExtra("gameMode")); //obter  a string do enum e converter para enum
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        btnGotKilled = (Button) findViewById(R.id.got_killed);
        endGame = (LinearLayout) findViewById(R.id.endGame);
        dbRef = FirebaseDatabase.getInstance().getReference("Games").child(gameName);
        Log.i("myTeam", myTeam+"");
        if (myTeam.equals("Equipa Azul"))
            enemyTeam = "Equipa Vermelha";
        else
            enemyTeam = "Equipa Azul";
        /*Timer T=new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        textGameTime.setText(hmsTimeFormatter(count/1000));
                        count++;
                    }
                });
            }
        }, 1000, 1000);*/
        final Activity activity = this;
        btnGotKilled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(activity)
                        .title(R.string.confirm_death)
                        .positiveText(R.string.agree)
                        .negativeText(R.string.disagree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                FirebaseDatabase.getInstance().getReference().child("Games").child(gameName).child(myTeam)
                                        .child(UserDataManager.encodeUserEmail(userEmail)).child("state")
                                        .setValue(LobbyTeamActivity.PLAYERSTATE.DEAD);
                                Marker marker = lastestPlayerMarkers.get(user.getEmail());
                                marker.remove();
                                lastestPlayerMarkers.remove(UserDataManager.encodeUserEmail(user.getEmail()));
                                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) GameMapActivity.this);

                            }
                        }).show();
            }
        });

    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
        return hms;
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
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        //mMap.getUiSettings().setRotateGesturesEnabled(false);
        //mMap.getUiSettings().setScrollGesturesEnabled(false);
        //mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                //mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            //mMap.setMyLocationEnabled(true);
        }

        //Ler valores da minha equipa
        dbRef.child(myTeam).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean allDead = true; //ver se todos os elementos da equipa estao mortos
                double lat;
                double longt;
                String state;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getKey().equals("score")){
                        state = data.child("state").getValue(String.class);
                        if (LobbyTeamActivity.PLAYERSTATE.valueOf(state) == LobbyTeamActivity.PLAYERSTATE.PLAYING){
                            allDead = false;
                        }
                    }
                }
                //Senao houver ninguem vivo na capa chamar a funçao que termina a partida
                if ( allDead==true ){
                    dbRef.child(myTeam).removeEventListener(this);
                    if(myTeam.equals("Equipa Vermelha")){
                        finishGame("Equipa Azul", "Equipa Vermelha");
                        return;
                    } else{
                        finishGame("Equipa Vermelha", "Equipa Azul");
                        return;
                    }
                }
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getKey().equals("score")){
                        if(!data.getKey().equals(UserDataManager.encodeUserEmail(userEmail)) && data.hasChild("lat") && data.hasChild("long")) {
                            lat = data.child("lat").getValue(Double.class);
                            longt = data.child("long").getValue(Double.class);
                            state = data.child("state").getValue(String.class);
                            LatLng coord = new LatLng(lat, longt);
                            if(!lastestPlayerMarkers.containsKey(data.getKey())){
                                Marker playerMarker;
                                if(myTeam.equals("Equipa Vermelha")){
                                    playerMarker = mMap.addMarker(new MarkerOptions()
                                            .position(coord).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_pointer))
                                            .title(data.getKey()));
                                }else{
                                    playerMarker = mMap.addMarker(new MarkerOptions()
                                            .position(coord).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_blue_pointer))
                                            .title(data.getKey()));
                                }
                                lastestPlayerMarkers.put(data.getKey(), playerMarker);
                            }else{
                                if (LobbyTeamActivity.PLAYERSTATE.valueOf(state) == LobbyTeamActivity.PLAYERSTATE.PLAYING)
                                    lastestPlayerMarkers.get(data.getKey()).setPosition(coord);
                                else{
                                    //se o jogador estiver morto, nao incluir o seu marcador no mapa
                                    Marker marker = lastestPlayerMarkers.get(user.getEmail());
                                    if(marker != null){
                                        marker.remove();
                                        lastestPlayerMarkers.remove(data.getKey());
                                    }
                                }
                            }
                        }
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //Ler valores da equipa inimiga
        dbRef.child(enemyTeam).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("equipa", "equipa inimiga");
                Boolean allDead = true; //ver se todos os elementos da equipa estao mortos
                double lat;
                double longt;
                String state;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getKey().equals("score")){
                        state = data.child("state").getValue(String.class);
                        if (LobbyTeamActivity.PLAYERSTATE.valueOf(state) == LobbyTeamActivity.PLAYERSTATE.PLAYING){
                            allDead = false;
                        }
                    }
                }
                //Senao houver ninguem vivo na capa chamar a funçao que termina a partida
                if ( allDead==true ){
                    dbRef.child(enemyTeam).removeEventListener(this);
                    if(myTeam.equals("Equipa Vermelha")){
                        finishGame("Equipa Vermelha", "Equipa Vermelha");
                        return;
                    } else{
                        finishGame("Equipa Azul", "Equipa Azul");
                        return;
                    }
                }
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getKey().equals("score") && !data.getKey().equals(UserDataManager.encodeUserEmail(userEmail)) && data.hasChild("lat") && data.hasChild("long")) {
                        lat = data.child("lat").getValue(Double.class);
                        longt = data.child("long").getValue(Double.class);
                        state = data.child("state").getValue(String.class);
                        LatLng coord = new LatLng(lat, longt);
                        Location enemyLocation = new Location("");
                        enemyLocation.setLatitude(lat);
                        enemyLocation.setLongitude(longt);
                        if( mLastLocation!= null && enemyLocation.distanceTo(mLastLocation)<METERSTOSEEENEMIES) {
                            if (!lastestPlayerMarkers.containsKey(data.getKey())) {
                                Marker playerMarker;
                                if (enemyTeam.equals("Equipa Vermelha")) {
                                    playerMarker = mMap.addMarker(new MarkerOptions()
                                            .position(coord).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_pointer))
                                            .title(data.getKey()));
                                } else {
                                    playerMarker = mMap.addMarker(new MarkerOptions()
                                            .position(coord).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_blue_pointer))
                                            .title(data.getKey()));
                                }
                                lastestPlayerMarkers.put(data.getKey(), playerMarker);
                            } else {
                                if (LobbyTeamActivity.PLAYERSTATE.valueOf(state) == LobbyTeamActivity.PLAYERSTATE.PLAYING)
                                    lastestPlayerMarkers.get(data.getKey()).setPosition(coord);
                                else{
                                    Marker marker = lastestPlayerMarkers.get(user.getEmail());
                                    if(marker != null){
                                        marker.remove();
                                        lastestPlayerMarkers.remove(data.getKey());
                                    }
                                }
                            }
                        }
                        if (LobbyTeamActivity.PLAYERSTATE.valueOf(state) == LobbyTeamActivity.PLAYERSTATE.PLAYING) {
                            allDead=false;
                            break;
                        }
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(200)
                .setMaxWaitTime(500)
                .setFastestInterval(100);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void updateCameraBearing(GoogleMap googleMap, float bearing) {
        if (googleMap == null) return;
        CameraPosition camPos = CameraPosition
                .builder(
                        googleMap.getCameraPosition() // current Camera
                )
                .bearing(bearing)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }

    public Address setCurrentPlaceDetails(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        return addresses.get(0);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        dbRef.child(myTeam).child(UserDataManager.encodeUserEmail(userEmail)).child("lat").setValue(location.getLatitude());
        dbRef.child(myTeam).child(UserDataManager.encodeUserEmail(userEmail)).child("long").setValue(location.getLongitude());
        if(!lastestPlayerMarkers.containsKey(userEmail)){
            Marker playerMarker;
            if(myTeam.equals("Equipa Vermelha")){
                playerMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_pointer))
                        .title(userEmail));
            }else{
                playerMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_blue_pointer))
                        .title(userEmail));
            }
            lastestPlayerMarkers.put(userEmail, playerMarker);
        }else{
            lastestPlayerMarkers.get(userEmail).setPosition(latLng);
        }

        //move map camera
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(23)
                .tilt(50)
                .bearing(location.getBearing())
                .build();

        updateCameraBearing(mMap, location.getBearing());

        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.confirm_leave_game)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public void finishGame(String winnerTeam, String myTeam){
        endGame.setVisibility(View.VISIBLE); //mostrar mensagem de fim de partida
        Intent intent = new Intent(this, GameEndedActivity.class);
        Log.i("winning team", winnerTeam);
        intent.putExtra("winnerTeam", winnerTeam);
        intent.putExtra("myTeam", myTeam);
        intent.putExtra("startDate", startDate);
        intent.putExtra("zone", zone);
        intent.putExtra("gameName", gameName);
        intent.putExtra("gameMode", gameMode.toString());
        intent.putExtra("myName", myName);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        //mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.

        }
    }
}