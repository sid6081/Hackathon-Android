package com.example.siddharthkarandikar.hackathon_june.APIHelper.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.siddharthkarandikar.hackathon_june.APIHelper.HackathonService;
import com.example.siddharthkarandikar.hackathon_june.APIHelper.Map.MapActivity;
import com.example.siddharthkarandikar.hackathon_june.APIHelper.Registration.RegisterActivity;
import com.example.siddharthkarandikar.hackathon_june.APIHelper.Registration.RegistrationBody;
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

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private TextView notRegisteredTextView;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private HackathonService hackathonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emaiIdLogin);
        passwordEditText = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        notRegisteredTextView = findViewById(R.id.notRegisteredTextView);

        emailEditText.setText("sid@y.com");
        passwordEditText.setText("test");

        notRegisteredTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(email.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
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

                    LoginBody requestBody = new LoginBody();
                    requestBody.setEmailid(email);
                    requestBody.setPassword(password);

                    hackathonService.login(requestBody)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(mapDataPointResponse -> {
                                        Intent intent = new Intent(LoginActivity.this, MapActivity.class);
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
