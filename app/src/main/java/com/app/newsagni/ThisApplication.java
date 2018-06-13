package com.app.newsagni;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import com.app.newsagni.data.AppConfig;
import com.app.newsagni.utils.Tools;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ThisApplication extends Application {

    private static ThisApplication mInstance;
    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
        // init realm database
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("wordpress.realm")
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        // activate analytics tracker
        getGoogleAnalyticsTracker();

        // get enabled controllers
        Tools.requestInfoApi(this);

    }

    public static synchronized ThisApplication getInstance() {
        return mInstance;
    }

    /**
     * --------------------------------------------------------------------------------------------
     * For Google Analytics
     */
    public synchronized Tracker getGoogleAnalyticsTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(!AppConfig.ENABLE_ANALYTICS);
            tracker = analytics.newTracker(R.xml.app_tracker);
        }
        return tracker;
    }

    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();
        // Set screen name.
        t.setScreenName(screenName);
        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();
            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(new StandardExceptionParser(this, null).getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();
        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    /** ---------------------------------------- End of analytics --------------------------------- */
}
