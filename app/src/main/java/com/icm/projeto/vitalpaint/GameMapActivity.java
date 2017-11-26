package com.icm.projeto.vitalpaint;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icm.projeto.vitalpaint.Data.GameDataManager;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GameMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;


    private List<String> blueTeamPlayers;
    private List<String> redTeamPlayers;
    private Map<String, Marker> lastestPlayerMarkers  = new HashMap<>();
    private String myName;
    private String gameName;
    private String myTeam = "";
    private String enemyTeam = "";
    private int duration;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private GameDataManager dbManager;
    private DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//nao bloquear o ecra

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gameName = getIntent().getStringExtra("gameName");
        duration = getIntent().getIntExtra("duration", 0);
        myTeam = getIntent().getStringExtra("myTeam");
        Log.i("gamemap", myTeam + ", " + "duration:" + duration + ", gm " + gameName);
        myName = getIntent().getStringExtra("userName");
        if (myTeam.equals("Equipa Azul"))
            enemyTeam = "Equipa Vermelha";
        else
            enemyTeam = "Equipa Azul";
        Log.i("equipa", myTeam);
        dbRef = FirebaseDatabase.getInstance().getReference("Games").child(gameName);
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
                double lat;
                double longt;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getKey().equals("score") && !data.getKey().equals(UserDataManager.encodeUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())) && data.hasChild("lat") && data.hasChild("long")) {
                        lat = data.child("lat").getValue(Double.class);
                        longt = data.child("long").getValue(Double.class);
                        LatLng coord = new LatLng(lat, longt);
                        if(!lastestPlayerMarkers.containsKey(data.getKey())){
                            Marker playerMarker = mMap.addMarker(new MarkerOptions()
                                    .position(coord).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_player_pointer))
                                    .title(data.getKey()));
                            lastestPlayerMarkers.put(data.getKey(), playerMarker);
                        }else{
                            lastestPlayerMarkers.get(data.getKey()).setPosition(coord);
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
                double lat;
                double longt;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getKey().equals("score") && !data.getKey().equals(UserDataManager.encodeUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())) && data.hasChild("lat") && data.hasChild("long")) {
                        lat = data.child("lat").getValue(Double.class);
                        longt = data.child("long").getValue(Double.class);
                        LatLng coord = new LatLng(lat, longt);
                        if(!lastestPlayerMarkers.containsKey(data.getKey())){
                            Marker playerMarker = mMap.addMarker(new MarkerOptions()
                                    .position(coord).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_player_pointer))
                                    .title(data.getKey()));
                            lastestPlayerMarkers.put(data.getKey(), playerMarker);
                        }else{
                            lastestPlayerMarkers.get(data.getKey()).setPosition(coord);
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
        dbRef.child(myTeam).child(UserDataManager.encodeUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child("lat").setValue(location.getLatitude());
        dbRef.child(myTeam).child(UserDataManager.encodeUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child("long").setValue(location.getLongitude());
        Marker m =
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .snippet(
                                "Lat:" + latLng.latitude + "Lng:"
                                        + latLng.longitude)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_player_pointer))
                        .title("position"));
        //move map camera
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(22)
                .tilt(50)
                .bearing(location.getBearing())
                .build();

        updateCameraBearing(mMap, location.getBearing());

        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

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
