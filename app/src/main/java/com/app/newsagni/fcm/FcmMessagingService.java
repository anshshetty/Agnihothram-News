package com.app.newsagni.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.app.newsagni.ActivityMain;
import com.app.newsagni.ActivityPostDetails;
import com.app.newsagni.ActivitySplash;
import com.app.newsagni.R;
import com.app.newsagni.data.SharedPref;
import com.app.newsagni.model.FcmNotif;
import com.app.newsagni.model.Post;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FcmMessagingService extends FirebaseMessagingService {

    private static int VIBRATION_TIME = 500; // in millisecond


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

            if (remoteMessage.getData().size() > 0) {
               String title,message,img_url;

               title=remoteMessage.getData().get("title");
                message=remoteMessage.getData().get("messege");
                img_url=remoteMessage.getData().get("img_url");

                Intent intent=new Intent(this,ActivityMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
                Uri sounduri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
               final NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
               builder.setContentTitle(title);
               builder.setContentText(message);
               builder.setContentIntent(pendingIntent);
               builder.setSound(sounduri);
               builder.setSmallIcon(R.drawable.splash_icon);

                ImageRequest imageRequest=new ImageRequest(img_url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response));
                        NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0,builder.build());

                    }
                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                MySingleton.getmInstance(this).addToRequestQue(imageRequest);



            }

    }

    /*private void displayNotificationIntent(FcmNotif fcmNotif) {
        Intent intent = new Intent(this, ActivitySplash.class);
        if (fcmNotif.getPost_id() != -1) {
            intent = new Intent(this, ActivityPostDetails.class);
            Post post = new Post();
            post.title = fcmNotif.getTitle();
            post.id = fcmNotif.getPost_id();
            boolean from_notif = !ActivityMain.active;
            intent.putExtra(ActivityPostDetails.EXTRA_OBJC, post);
            intent.putExtra(ActivityPostDetails.EXTRA_NOTIF, from_notif);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(fcmNotif.getTitle());
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(fcmNotif.getContent()));
        builder.setContentText(fcmNotif.getContent());
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setDefaults(Notification.DEFAULT_LIGHTS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int unique_id = (int) System.currentTimeMillis();
        notificationManager.notify(unique_id, builder.build());
    }*/

}
