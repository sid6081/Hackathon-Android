package com.example.siddharthkarandikar.hackathon_june.APIHelper.Map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.siddharthkarandikar.hackathon_june.APIHelper.HackathonService;
import com.example.siddharthkarandikar.hackathon_june.MapHelper.Map;
import com.example.siddharthkarandikar.hackathon_june.MessageActivity;
import com.example.siddharthkarandikar.hackathon_june.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends FragmentActivity implements PlaceSelectionListener {

    private SharedPreferences sp;
    private static boolean locationPermissionGranted = false, isMapSet = false;
    private static boolean smsPermissionGranted = false;
    private SupportMapFragment mapFragment;
    private Map map;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton shareButton;
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
        shareButton = findViewById(R.id.share_status);

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
                if(map != null) {
                    map.goToLocation(false);
                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smsPermissionGranted) {
                    sendMessage();
                } else {
                    requestSmsPermission(MapActivity.this);
                }
            }
        });
    }

    private void requestSmsPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS},
                    10);
        } else {
            smsPermissionGranted = true;
        }
    }

    private void sendMessage() {
//        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//        smsIntent.setData(Uri.parse("smsto:"));
//        smsIntent.setType("vnd.android-dir/mms-sms");
//        smsIntent.putExtra("address", new String("9740543019"));
//        smsIntent.putExtra("sms_body", "Test");
        Log.d("SMS_SENd", "HERE");

        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        SmsManager sms = SmsManager.getDefault();

        //TODO : BRACES : PHONE_NUMBER_HERE
        sms.sendTextMessage("9886501321", null, "Hi, I have just entered an unsafe area", pi, null);
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
            case 10: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    smsPermissionGranted = true;
                    sendMessage();
                } else {
                    Toast.makeText(MapActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onPlaceSelected(Place place) {

        AlertDialog.Builder builder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder = new AlertDialog.Builder(getApplicationContext(), android.R.style.Theme_Material_Dialog_Alert);
//        } else {
//            builder = new AlertDialog.Builder(getApplicationContext());
//        }
        builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("Rate this place")
                .setMessage("Do you want to rate this place?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            //We need to get the instance of the LayoutInflater, use the context of this activity
                            LayoutInflater inflater = (LayoutInflater) MapActivity.this
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            //Inflate the view from a predefined XML layout
                            View layout = inflater.inflate(R.layout.pop_layout,
                                    (ViewGroup) findViewById(R.id.popup_element));
                            // create a 300px width and 470px height PopupWindow
                            PopupWindow pw = new PopupWindow(layout, 300, 470, true);
                            // display the popup in the center
                            pw.showAtLocation(getCurrentFocus(), Gravity.CENTER, 0, 0);

                            EditText ratingEditText = layout.findViewById(R.id.ratingpopup);
                            Button submitButton = layout.findViewById(R.id.end_data_send_button);
                            submitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
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
                                    requestBody.setLatitude(place.getLatLng().latitude + "");
                                    requestBody.setLongitude(place.getLatLng().longitude + "");
                                    requestBody.setSafetyRating(ratingEditText.getText().toString());

                                    hackathonService.safetyRating(requestBody)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(mapDataPointResponse -> {
                                                        map.goToLocationWithLatLong(place.getLatLng().latitude, place.getLatLng().longitude);
                                                        map.createGeofenceLatLong(place.getLatLng().latitude, place.getLatLng().longitude, Integer.parseInt(ratingEditText.getText().toString()));
                                                    }, throwable -> {
                                                        Log.d("ERROR_RESPONSE", " : S : " + throwable.getLocalizedMessage());
                                                    }
                                            );
                                    pw.dismiss();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        map.goToLocationWithLatLong(place.getLatLng().latitude, place.getLatLng().longitude);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onError(Status status) {
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }
}
