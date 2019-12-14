package com.example.stree;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private final long Min_Time = 1000;
    private final long Min_Dist= 5;

    private LatLng latLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},PackageManager.PERMISSION_GRANTED);


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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                try {
                    latLng = new LatLng (location.getLatitude(),location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    String Phonenumber = "8605190687";
                    String MyLatitude = String.valueOf(location.getLatitude());
                    String MyLongitude = String.valueOf(location.getLongitude());

                    String message = "Latitude =" +MyLatitude +  "Longitude=" +MyLongitude+ "Hello Vikrant here , I need your help here in emergency";

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(Phonenumber,null,message,null,null);

                }
                catch (Exception e){

                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            assert locationManager != null;
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,Min_Time,Min_Dist,locationListener);
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,Min_Time,Min_Dist,locationListener);
        }
        catch (SecurityException e){

            e.printStackTrace();
        }
    }
}
