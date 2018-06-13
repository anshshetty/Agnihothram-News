package com.app.newsagni.fcm;


import android.util.Log;

import com.app.newsagni.data.SharedPref;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FcmInstanceIDService extends FirebaseInstanceIdService{




    @Override
    public void onTokenRefresh() {
        String fcm_token=FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_TOKEN",fcm_token);

    }

    /*private void sendRegistrationToServer(String token) {
        sharedPref.setFcmRegId(token);
        sharedPref.setOpenAppCounter(SharedPref.MAX_OPEN_COUNTER);
    }*/
}
