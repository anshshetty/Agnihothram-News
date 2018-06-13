package com.app.newsagni;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.app.newsagni.connection.API;
import com.app.newsagni.connection.RestAdapter;
import com.app.newsagni.connection.callbacks.CallbackDevice;
import com.app.newsagni.data.Constant;
import com.app.newsagni.data.SharedPref;
import com.app.newsagni.model.DeviceInfo;
import com.app.newsagni.utils.NetworkCheck;
import com.app.newsagni.utils.Tools;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    private Call<CallbackDevice> callbackCall = null;
    private SharedPref sharedPref;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPref = new SharedPref(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get token
        String token = "";
        for (int i = 0; i < 10; i++) {
            token = FirebaseInstanceId.getInstance().getToken();
            if(!TextUtils.isEmpty(token)) break;
        }

        String msg = getString(R.string.msg_token_fmt, token);
        Log.d("FCM_TOKEN", msg);

        if (NetworkCheck.isConnect(this) && !TextUtils.isEmpty(token) && sharedPref.isOpenAppCounterReach()) {
            sendRegistrationToServer(token);
        }else{
            progressBar.setVisibility(View.GONE);
            startNextActivity();
        }
    }


    private void sendRegistrationToServer(String token) {
        API api = RestAdapter.createAPI();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.regid = token;
        deviceInfo.device_name = Tools.getDeviceName();
        deviceInfo.serial = Build.SERIAL;
        deviceInfo.os_version = Tools.getAndroidVersion();

        callbackCall = api.registerDevice(deviceInfo);
        callbackCall.enqueue(new Callback<CallbackDevice>() {
            @Override
            public void onResponse(Call<CallbackDevice> call, Response<CallbackDevice> response) {
                CallbackDevice resp = response.body();
                if (resp != null && resp.status.equals("success")) {
                    sharedPref.setOpenAppCounter(0);
                }
                startNextActivity();
            }

            @Override
            public void onFailure(Call<CallbackDevice> call, Throwable t) {
                startNextActivity();
            }
        });
    }

    // make a delay to start next activity
    private void startNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivitySplash.this.finish();
                Intent i = new Intent(getApplicationContext(), ActivityMain.class);
                startActivity(i);
            }
        }, Constant.DELAY_TIME_SPLASH);
    }
}
