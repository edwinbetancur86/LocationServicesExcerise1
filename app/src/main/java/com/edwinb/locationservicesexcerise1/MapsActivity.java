package com.edwinb.locationservicesexcerise1;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Circle circle;
    private Marker marker;
    private EditText editTextAddress;
    private Button buttonSearch;
    private Button buttonSatellite;
    private Button buttonHybrid;
    private Button buttonNormal;
    private Marker marker1;
    private Marker marker2;
    private Polyline line;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                }

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editTextAddress = (EditText) findViewById(R.id.editTextSearchAddress);
        buttonHybrid = (Button) findViewById(R.id.hybridButton);
        buttonNormal = (Button) findViewById(R.id.normalButton);
        buttonSatellite = (Button) findViewById(R.id.satelliteButton);

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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // Add a marker in Sydney and move the camera
                LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();



                marker = drawMarker(new LatLng (location.getLatitude(), location.getLongitude()));
                circle = drawCircle(new LatLng (location.getLatitude(), location.getLongitude()));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 10));

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                     List<android.location.Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if (listAddresses != null && listAddresses.size() > 0)
                    {
                        //Log.i("PlaceInfo", listAddresses.get(0).toString());

                        String address = "";

                        if (listAddresses.get(0).getSubThoroughfare() != null)
                        {
                            address += listAddresses.get(0).getSubThoroughfare() + " ";
                        }

                        if (listAddresses.get(0).getThoroughfare() != null)
                        {
                            address += listAddresses.get(0).getThoroughfare() + ", ";
                        }

                        if (listAddresses.get(0).getLocality() != null)
                        {
                            address += listAddresses.get(0).getLocality() + ", ";
                        }

                        if (listAddresses.get(0).getPostalCode() != null)
                        {
                            address += listAddresses.get(0).getPostalCode() + ", ";
                        }

                        if (listAddresses.get(0).getCountryName() != null)
                        {
                            address += listAddresses.get(0).getCountryName();
                        }

                        Toast.makeText(MapsActivity.this, address, Toast.LENGTH_SHORT).show();

                    }

                } catch (IOException e) {
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


        if (Build.VERSION.SDK_INT < 23)
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        }
        else
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
            else
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                // Add a marker in Sydney and move the camera
                LatLng userLoc = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());


                mMap.clear();




                mMap.addMarker(new MarkerOptions().position(userLoc).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory
                                .HUE_VIOLET)));





                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 10));



            }
        }



    }

    private void drawLine()
    {
        PolylineOptions options = new PolylineOptions()
                .add(marker1.getPosition())
                .add(marker2.getPosition())
                .color(Color.BLUE)
                .width(3);


        line = mMap.addPolyline(options);
    }

    private void removeEverything()
    {
        marker1.remove();
        marker1 = null;
        marker2.remove();
        marker2 = null;
        line.remove();
    }


    private Marker drawMarker(LatLng latLng)
    {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));


        return mMap.addMarker(options);
    }

    private Circle drawCircle(LatLng latLng) {

        CircleOptions options = new CircleOptions()
                .center(latLng)
                .radius(5000)
                .fillColor(0x50ffff00)
                .strokeColor(Color.BLACK)
                .strokeWidth(3);

        return mMap.addCircle(options);

    }



    public void searchAddress(View view) {
        String addLocation = editTextAddress.getText().toString();
        List<android.location.Address> listAddresses = null;

        if (addLocation != null || !addLocation.equals(""))
        {
            Geocoder geocoder = new Geocoder(this);
            try {
                listAddresses = geocoder.getFromLocationName(addLocation, 1);

            } catch (Exception e) {
                e.getMessage();
            }

            Address address =  listAddresses.get(0);

            String addressResultOfSearch = "";

            if (listAddresses.get(0).getSubThoroughfare() != null)
            {
                addressResultOfSearch += listAddresses.get(0).getSubThoroughfare() + " ";
            }

            if (listAddresses.get(0).getThoroughfare() != null)
            {
                addressResultOfSearch += listAddresses.get(0).getThoroughfare() + ", ";
            }

            if (listAddresses.get(0).getLocality() != null)
            {
                addressResultOfSearch += listAddresses.get(0).getLocality() + ", ";
            }

            if (listAddresses.get(0).getPostalCode() != null)
            {
                addressResultOfSearch += listAddresses.get(0).getPostalCode() + ", ";
            }

            if (listAddresses.get(0).getCountryName() != null)
            {
                addressResultOfSearch += listAddresses.get(0).getCountryName();
            }

            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(addressResultOfSearch));


            if (marker1 == null)
            {
                marker1 = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory
                                .HUE_ORANGE)));

            }
            else if (marker2 == null)
            {
                marker2 = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory
                                .HUE_AZURE)));
                drawLine();
            }
            else
            {
                removeEverything();
                marker1 = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory
                                .HUE_GREEN)));
            }



            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));



        }


    }

    public void changeToNormal(View view) {

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    public void changeToSatellite(View view) {

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

    }

    public void changeToHybrid(View view) {

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

    }
}
