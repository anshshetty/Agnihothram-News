package com.app.newsagni.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.newsagni.R;
import com.app.newsagni.connection.API;
import com.app.newsagni.connection.RestAdapter;
import com.app.newsagni.connection.callbacks.CallbackInfo;
import com.app.newsagni.data.SharedPref;
import com.app.newsagni.model.Attachment;
import com.app.newsagni.model.Category;
import com.app.newsagni.model.Post;
import com.squareup.picasso.Picasso;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tools {

    public static boolean isLolipopOrHigher() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public static void rateAction(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public static void aboutAction(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.dialog_about_title));
        builder.setMessage(Html.fromHtml(activity.getString(R.string.about_text)));
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    public static void dialogCommentNeedLogin(final Activity activity, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.direct_to_browser_dialog_title));
        builder.setMessage(activity.getString(R.string.direct_to_browser_dialog_text));
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                directLinkToBrowser(activity, url);
            }
        });
        builder.setNegativeButton("NO", null);
        builder.show();
    }

    public static void directLinkToBrowser(Activity activity, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "Ops, Cannot open url", Toast.LENGTH_LONG).show();
        }
    }

    public static void methodShare(Activity act, Post p) {
        Uri uri = Uri.parse(p.thumbnail);

        // string to share
        StringBuilder sb = new StringBuilder();
        sb.append("Read Article \'" + p.title_plain + "\'\n");
        sb.append("Using app \'" + act.getString(R.string.app_name) + "\'\n");
        sb.append("Source : " + p.url + "");

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, act.getString(R.string.app_name));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        //sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        act.startActivity(Intent.createChooser(sharingIntent, "Share Using"));
    }

    public static String getCategoryTxt(List<Category> categories) {
        String res = "";
        if (categories.size() == 1) {
            res = categories.get(0).title;
        } else if (categories.size() > 1) {
            res = categories.get(0).title;
            for (int i = 1; i < categories.size(); i++) {
                res = res + ", " + categories.get(i).title;
            }
        }
        return res;
    }

    /* we set String default with empty value on model */
    public static boolean isNullOrEmpty(String str) {
        try {
            return str.trim().equals("");
        } catch (Exception e) {
            return true;
        }
    }

    /* we set int default with -1 on model */
    public static boolean isNullOrNegative(int str) {
        try {
            return str == -1;
        } catch (Exception e) {
            return true;
        }
    }


    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getFormatedDate(String date_str) {
        if (date_str != null && !date_str.trim().equals("")) {
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm");
            try {
                String newStr = newFormat.format(oldFormat.parse(date_str));
                return newStr;
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getFormatedDateSimple(String date_str) {
        if (date_str != null && !date_str.trim().equals("")) {
            SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat newFormat = new SimpleDateFormat("MMMM dd, yyyy");
            try {
                String newStr = newFormat.format(oldFormat.parse(date_str));
                return newStr;
            } catch (ParseException e) {
                return "";
            }
        } else {
            return "";
        }
    }

    public static void requestInfoApi(Context ctx) {
        final SharedPref sharedPref = new SharedPref(ctx);
        API api = RestAdapter.createAPI();
        Call<CallbackInfo> callbackCall = api.getInfo();
        callbackCall.enqueue(new Callback<CallbackInfo>() {
            @Override
            public void onResponse(Call<CallbackInfo> call, Response<CallbackInfo> response) {
                CallbackInfo resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    sharedPref.setInfoObject(resp);
                }
            }

            @Override
            public void onFailure(Call<CallbackInfo> call, Throwable t) {
            }
        });
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE + "";
    }


    public static String getDeviceId(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static void hideSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        return dm;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getDefaultStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }

    public static int[] getScreenSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    public static int[] getScreenSize(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);  // api 13
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size); // api 13
            screenWidth = size.x;
        }

        return screenWidth;
    }

    public static void displayImageThumbnail(Context ctx, Post p, ImageView imageView){
        try{
            String url = "";
            if(p.thumbnail !=null && !p.thumbnail.equals("")) {
                url = p.thumbnail;
            }else if(p.attachments.size() > 0){
                for(Attachment a : p.attachments){
                    if(a.mime_type.equals("image/jpeg") || a.mime_type.equals("image/png")){
                        url = a.url;
                        break;
                    }
                }
            }
            if(!TextUtils.isEmpty(url)){
                Picasso.with(ctx).load(url).into(imageView);
            }
        }catch (Exception e){
            Log.e("WORDPRESS", "Failed when display image - "+e.getMessage());
        }
    }
}
