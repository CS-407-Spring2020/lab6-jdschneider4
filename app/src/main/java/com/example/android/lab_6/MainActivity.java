package com.example.android.lab_6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12; //can be any number

    private FusedLocationProviderClient mFusedLocationProviderClient; //save the instance
    //Somewhere in Australia
    private final LatLng mDestinationLatLng = new LatLng(-33.8523341, 151.2106085);
    private LatLng currentDest = new LatLng(-33.85,151);
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);

        //Obtain a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;
            //code to display marker
            googleMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destination"));

            displayMyLocation();
            googleMap.addMarker(new MarkerOptions().position(currentDest).title("Destination"));
        });
    }

    private void displayMyLocation() {
        //cheeck if permission granted
        int permission= ActivityCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        //if not ask for it
        if (permission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //if permission granted display marker at current location
        else{
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(this,
                    task-> {
                        Location mLastKnownLocation = task.getResult();
                            if(task.isSuccessful() && mLastKnownLocation !=null){
                                //added
                               // currentDest = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                                mMap.addPolyline(new PolylineOptions().add(
                                   new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),mDestinationLatLng));


                            }
                    });
        }

    }

    /**
     * Handles the result of the request for location permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION){
            //if request is cancelle, the result arrays are empty
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                displayMyLocation();
            }
        }
    }
}
