package com.example.siddharthkarandikar.hackathon_june.MapHelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.siddharthkarandikar.hackathon_june.APIHelper.HackathonService;
import com.example.siddharthkarandikar.hackathon_june.GeofenceHelper.GeofenceTransitionsIntentService;
import com.example.siddharthkarandikar.hackathon_june.GeofenceHelper.SimpleGeofence;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by siddharth.karandikar on 27/03/18.
 */

public class Map implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Location location;
    private LocationManager locationManager;
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private final long MIN_TIME_BW_UPDATES = 5000;
    private double sourceLatitude = 999, sourceLongitude = 999, destLatitude = 999, destLongitude = 999;
    private SharedPreferences sp;
    private Context context;
    private boolean editEnabled = false;
    private static GeofencingClient mGeofencingClient;
    private PendingIntent mGeofencePendingIntent;
    private Marker sourceMarker, destMarker;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private HackathonService hackathonService;

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS
            * DateUtils.HOUR_IN_MILLIS;

    public Map(Context context, SupportMapFragment mapFragment, SharedPreferences sp) {
        mapFragment.getMapAsync(this);
        this.sp = sp;
        this.context = context;
    }

    @Override
    public void onLocationChanged(Location location) {
//        Toast.makeText(context, location.getLatitude() + " : " + location.getLongitude() + " : Location changed", Toast.LENGTH_SHORT).show();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        addLocationMarker(latLng, "Source");
        updateSourceLatLng(latLng);
        //goToLocation(false);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Toast.makeText(context, s + " : Status changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(context, s + " : Enabled", Toast.LENGTH_SHORT).show();
        populateMapWithSource(context, sp);
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(context, s + " : Disabled", Toast.LENGTH_SHORT).show();
        mMap.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        populateMapWithSource(this.context, this.sp);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                if (editEnabled) {
                    Toast.makeText(context, "Disabling", Toast.LENGTH_SHORT).show();
                    editEnabled = false;
                } else {
                    Toast.makeText(context, "Enabling", Toast.LENGTH_SHORT).show();
                    editEnabled = true;
                }

                return false;
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });

        okHttpClient = new OkHttpClient();

        okHttpClient.newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://192.168.1.8:8005/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        hackathonService = retrofit.create(HackathonService.class);

        hackathonService.getMapDatPoints()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(mapDataPointResponse -> {
                Log.d("USER_RATING_RESPONSE", " : S : " + mapDataPointResponse.body());
                for(int i=0;i<mapDataPointResponse.body().mapResponse.size();i++) {
                    double latitude =  Double.parseDouble(mapDataPointResponse.body().mapResponse.get(i).latitude);
                    double longitude =  Double.parseDouble(mapDataPointResponse.body().mapResponse.get(i).longitude);
                    int rating = Integer.parseInt(mapDataPointResponse.body().mapResponse.get(i).safetyRating);
                    //goToLocationWithLatLong(latitude, longitude);
                    createGeofenceLatLong(latitude, longitude, rating);
                }
//                            Toast.makeText(getApplicationContext(), "HOLA", Toast.LENGTH_SHORT).show();
                //this.finish();
            }, throwable -> {
                Log.d("ERROR_RESPONSE", " : S : " + throwable.getLocalizedMessage());
//                            Toast.makeText(getApplicationContext(), "Sorry! There was some error:"+throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
            );
    }

    public void populateMapWithSource(Context context, SharedPreferences sp) {
        if (getLatLong(context)) {
            Toast.makeText(context, sourceLatitude + " - " + sourceLongitude, Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sourceLatitude", sourceLatitude + "");
            editor.putString("sourceLongitude", sourceLongitude + "");
            editor.apply();
            Toast.makeText(context, "Location Enabled : " + sourceLatitude + " - " + sourceLongitude, Toast.LENGTH_LONG).show();
        } else {
            String latitudeString = sp.getString("sourceLatitude", "0");
            String longitudeString = sp.getString("sourceLongitude", "0");
            sourceLatitude = Double.parseDouble(latitudeString);
            sourceLongitude = Double.parseDouble(longitudeString);
            Toast.makeText(context, "Location Disabled : " + sourceLatitude + " - " + sourceLongitude, Toast.LENGTH_LONG).show();
        }
        LatLng myLatLng = new LatLng(sourceLatitude, sourceLongitude);
        addLocationMarker(myLatLng, "Source");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16));
    }

    public void addLocationMarker(LatLng myLatLng, String pointer) {
        MarkerOptions markerOptions = new MarkerOptions().position(myLatLng).title(pointer + " Location");
//        if (pointer.equalsIgnoreCase("destination")) {
//            mMap.clear();
//            addLocationMarker(new LatLng(sourceLatitude, sourceLongitude), "Source");
//            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_room_black_24dp));
//        }

        if (pointer.equalsIgnoreCase("source")) {
            if (sourceMarker == null || myLatLng.latitude != sourceLatitude || myLatLng.longitude != sourceLongitude) {
                if (sourceMarker != null) {
                    Log.d("Source Marker", "REMOVING");
                    sourceMarker.remove();
                    Log.d("Source Marker", "REMOVED");
                }
                Log.d("Source Marker", "ADDING");
                sourceMarker = mMap.addMarker(markerOptions);
                Log.d("Source Marker", "ADDED");
                Toast.makeText(context, "Updating marker", Toast.LENGTH_SHORT).show();
            }
        }

        if (pointer.equalsIgnoreCase("destination")) {
            if (destMarker == null || myLatLng.latitude != destLatitude || myLatLng.longitude != destLongitude) {
                if (destMarker != null) {
                    mMap.clear();
                    destMarker = null;
                    sourceMarker = null;
                    addLocationMarker(new LatLng(sourceLatitude, sourceLongitude), "Source");
                }
                destMarker = mMap.addMarker(markerOptions);
            }
        }


    }

    public void goToLocation(boolean destination) {
        LatLng myLatLng;
        if (destination) {
            myLatLng = new LatLng(destLatitude, destLongitude);
        } else {
            myLatLng = new LatLng(sourceLatitude, sourceLongitude);
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16));
    }

    public void goToLocationWithLatLong(double latitude, double longitude) {
        LatLng myLatLng = new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16));
    }

    @SuppressLint("MissingPermission")
    public boolean getLatLong(Context context) {
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGPSEnabled == false && isNetworkEnabled == false) {
            return false;
        } else {
            if (isNetworkEnabled) {
                location = null;
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                setLastKnownLocation(locationManager, LocationManager.NETWORK_PROVIDER);
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                location = null;
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    setLastKnownLocation(locationManager, LocationManager.GPS_PROVIDER);
                }
            }
            return true;
        }
    }

    public void updateDestLatLng(LatLng latLong) {
        destLatitude = latLong.latitude;
        destLongitude = latLong.longitude;
    }

    public void updateSourceLatLng(LatLng latLong) {
        sourceLatitude = latLong.latitude;
        sourceLongitude = latLong.longitude;
    }

    @SuppressLint("MissingPermission")
    private void setLastKnownLocation(LocationManager locationManager, String provider) {
        if (locationManager != null) {
            this.location = locationManager
                    .getLastKnownLocation(provider);
            if (this.location != null) {
                this.sourceLatitude = this.location.getLatitude();
                this.sourceLongitude = this.location.getLongitude();
            }
        }
    }

    public void createGeofence() {

        if (mGeofencingClient == null) {
            mGeofencingClient = LocationServices.getGeofencingClient(context);
        }

        SimpleGeofence geofence = new SimpleGeofence("Destination", destLatitude, destLongitude, 500,
                GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER);
        addGeofences(geofence);
        CircleOptions circleOptions1 = new CircleOptions()
                .center(new LatLng(geofence.getLatitude(), geofence.getLongitude()))
                .radius(geofence.getRadius()).strokeColor(Color.BLACK)
                .strokeWidth(2).fillColor(0x500000ff);
        mMap.addCircle(circleOptions1);
    }

    public void createGeofenceLatLong(double latitude, double longitude, int safetyRating) {
        if (mGeofencingClient == null) {
            mGeofencingClient = LocationServices.getGeofencingClient(context);
        }

        SimpleGeofence geofence = new SimpleGeofence("Destination", latitude, longitude, 1000,
                GEOFENCE_EXPIRATION_IN_MILLISECONDS, Geofence.GEOFENCE_TRANSITION_ENTER);
        addGeofences(geofence);
        int color = Color.YELLOW;
        switch (safetyRating) {
            case 1:
                color = Color.YELLOW;
                break;
            case 2:
                color = Color.MAGENTA;
                break;
            case 3:
                color = Color.RED;
                break;
        }
        CircleOptions circleOptions1 = new CircleOptions()
                .center(new LatLng(geofence.getLatitude(), geofence.getLongitude()))
                .radius(geofence.getRadius()).strokeColor(Color.BLACK)
                .strokeWidth(2).fillColor(color);
        mMap.addCircle(circleOptions1);
    }

    public void addGeofences(SimpleGeofence geofence) {
        if (mGeofencingClient != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mGeofencingClient.addGeofences(geofence.getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {
                            Toast.makeText(context, "Geofence Added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Geofence Addition Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void stopGeofence() {
        if (mGeofencingClient != null) {
            mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Geofence Removed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Geofence Removal Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

}
