package com.app.newsagni.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import com.app.newsagni.R;
import com.app.newsagni.connection.callbacks.CallbackInfo;

public class SharedPref {

    private Context ctx;
    private SharedPreferences custom_prefence;
    private SharedPreferences default_prefence;

    public static final int MAX_OPEN_COUNTER = 10 ;

    public SharedPref(Context context) {
        this.ctx = context;
        custom_prefence = context.getSharedPreferences("MAIN_PREF", Context.MODE_PRIVATE);
        default_prefence = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private String str(int string_id) {
        return ctx.getString(string_id);
    }

    /**
     * Preference for Fcm register
     */
    public void setFcmRegId(String fcmRegId){
        custom_prefence.edit().putString("FCM_PREF_KEY", fcmRegId).apply();
    }

    public String getFcmRegId(){
        return custom_prefence.getString("FCM_PREF_KEY", null);
    }

    public boolean isFcmRegIdEmpty(){
        return TextUtils.isEmpty(getFcmRegId());
    }

    // when app open N-times it will update fcm RegID at server
    public boolean isOpenAppCounterReach(){
        int counter = custom_prefence.getInt("OPEN_COUNTER_KEY", MAX_OPEN_COUNTER) + 1;
        setOpenAppCounter(counter);
        return (counter >= MAX_OPEN_COUNTER);
    }

    public void setOpenAppCounter(int val){
        custom_prefence.edit().putInt("OPEN_COUNTER_KEY", val).apply();
    }

    /**
     * Preference for User profile
     */

     /*public void setYourName(String name) {
        default_prefence.edit().putString(str(R.string.pref_title_name), name).apply();
    }

   public String getYourName() {
        return default_prefence.getString(str(R.string.pref_title_name), str(R.string.default_your_name));
    }

    public void setYourEmail(String name) {
        default_prefence.edit().putString(str(R.string.pref_title_email), name).apply();
    }

    public String getYourEmail() {
        return default_prefence.getString(str(R.string.pref_title_email), str(R.string.default_your_email));
    }
*/
    /**
     * For notifications flag
     */
    public boolean getNotification(){
        return default_prefence.getBoolean(str(R.string.pref_title_notif), true);
    }

    public String getRingtone(){
        return default_prefence.getString(str(R.string.pref_title_ringtone), "content://settings/system/notification_sound");
    }

    public boolean getVibration(){
        return default_prefence.getBoolean(str(R.string.pref_title_vibrate), true);
    }

    public void setInfoObject(CallbackInfo object_info) {
        String str_info = new Gson().toJson(object_info, CallbackInfo.class);
        custom_prefence.edit().putString("key_info", str_info).apply();
    }

    public CallbackInfo getInfoObject() {
        String str_info = custom_prefence.getString("key_info", null);
        if (str_info != null) {
            CallbackInfo callbackInfo = new Gson().fromJson(str_info, CallbackInfo.class);
            return callbackInfo;
        }
        return null;
    }

    public boolean isRespondEnabled() {
        CallbackInfo callbackInfo = getInfoObject();
        if (callbackInfo != null) {
            return callbackInfo.controllers.contains("respond");
        }
        return false;
    }

}
