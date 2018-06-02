package com.example.siddharthkarandikar.hackathon_june.APIHelper.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.siddharthkarandikar.hackathon_june.APIHelper.HackathonService;
import com.example.siddharthkarandikar.hackathon_june.APIHelper.Map.MapActivity;
import com.example.siddharthkarandikar.hackathon_june.R;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText firstnameEditText, lastnameEditText, emailidEditText, passwordEditText, phoneEditText, emergencyContactEditText;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private HackathonService hackathonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstnameEditText = findViewById(R.id.firstName);
        lastnameEditText = findViewById(R.id.lastName);
        emailidEditText = findViewById(R.id.emailid);
        passwordEditText = findViewById(R.id.password);
        phoneEditText = findViewById(R.id.phone);
        emergencyContactEditText = findViewById(R.id.emergencyContact);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstnameEditText.getText().toString();
                String lastName = lastnameEditText.getText().toString();
                String emailId = emailidEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String emergency = emergencyContactEditText.getText().toString();

                if (firstName.equals("") || lastName.equals("") || emailId.equals("") || password.equals("") || phone.equals("") || emergency.equals("")) {
                    Toast.makeText(RegisterActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                } else {
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

                    List<String> emergencyContactList = new ArrayList<>();
                    emergencyContactList.add(emergency);

                    RegistrationBody requestBody = new RegistrationBody();
                    requestBody.setFirstname(firstName);
                    requestBody.setLastname(lastName);
                    requestBody.setEmailid(emailId);
                    requestBody.setPassword(password);
                    requestBody.setPhone(phone);
                    requestBody.setEmergencyContact(emergencyContactList);

                    hackathonService.registration(requestBody)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(mapDataPointResponse -> {
                                        Intent intent = new Intent(RegisterActivity.this, MapActivity.class);
                                        startActivity(intent);
                                    }, throwable -> {
                                        Log.d("ERROR_RESPONSE", " : S : " + throwable.getLocalizedMessage());
                                    }
                            );
                }
            }
        });
    }
}
