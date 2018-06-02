package com.example.siddharthkarandikar.hackathon_june.APIHelper.Map;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.siddharthkarandikar.hackathon_june.APIHelper.HackathonService;
import com.example.siddharthkarandikar.hackathon_june.MapHelper.Map;
import com.example.siddharthkarandikar.hackathon_june.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MapActivity extends FragmentActivity implements PlaceSelectionListener {

    private static SharedPreferences sp;
    private static boolean locationPermissionGranted = false, isMapSet = false;
    private SupportMapFragment mapFragment;
    private Map map;
    private FloatingActionButton floatingActionButton;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private HackathonService hackathonService;
    private PopupWindow popWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestLocationPermission(MapActivity.this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        floatingActionButton = findViewById(R.id.fab);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(this);

        sp = getSharedPreferences("Location", MODE_PRIVATE);

        if (locationPermissionGranted & !isMapSet) {
            isMapSet = true;
            map = new Map(getApplicationContext(), mapFragment, sp);

        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.goToLocation(false);
            }
        });
    }

    private static void requestLocationPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            locationPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                    if (!isMapSet) {
                        isMapSet = true;
                        map = new Map(getApplicationContext(), mapFragment, sp);
                    }
                } else {
                    Toast.makeText(MapActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onPlaceSelected(Place place) {

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

        MapBody requestBody = new MapBody();
        requestBody.setName(place.getName().toString());
        requestBody.setLatitude(place.getLatLng().latitude+"");
        requestBody.setLongitude(place.getLatLng().longitude+"");
        requestBody.setSafetyRating("3");

        hackathonService.safetyRating(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mapDataPointResponse -> {
                    map.goToLocationWithLatLong(place.getLatLng().latitude, place.getLatLng().longitude);
                    map.createGeofenceLatLong(place.getLatLng().latitude, place.getLatLng().longitude, 3);
                        }, throwable -> {
                            Log.d("ERROR_RESPONSE", " : S : " + throwable.getLocalizedMessage());
                        }
                );
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }
}
