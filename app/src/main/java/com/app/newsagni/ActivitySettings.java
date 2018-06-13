package com.app.newsagni;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.newsagni.data.SharedPref;
import com.app.newsagni.utils.Tools;

/**
 * ATTENTION : To see where list of setting comes is open res/xml/setting_preferences.xml
 */
public class ActivitySettings extends PreferenceActivity {

    private AppCompatDelegate mDelegate;
    private ActionBar actionBar;
    private SharedPref sharedPref;
    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preferences);
        parent_view = findViewById(android.R.id.content);

        sharedPref = new SharedPref(this);

        /*final EditTextPreference prefName = (EditTextPreference) findPreference(getString(R.string.pref_title_name));
        final EditTextPreference prefEmail = (EditTextPreference) findPreference(getString(R.string.pref_title_email));*/
        final Preference prefTerm = (Preference) findPreference(getString(R.string.pref_title_term));

        //prefName.setSummary(sharedPref.getYourName());
       /* prefName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String s = (String) o;
                if(!s.trim().isEmpty()){
                    prefName.setSummary(s);
                    return true;
                }else{
                    Snackbar snackbar = Snackbar.make(parent_view, "Invalid Name Input", Snackbar.LENGTH_LONG);
                    TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                    return false;
                }
            }
        });

       // prefEmail.setSummary(sharedPref.getYourEmail());
        prefEmail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String s = (String) o;
                if (Tools.isValidEmail(s)) {
                    prefEmail.setSummary(s);
                    return true;
                } else {
                    Snackbar snackbar = Snackbar.make(parent_view, "Invalid Email Input", Snackbar.LENGTH_LONG);
                    TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                    return false;
                }
            }
        });
        */

        prefTerm.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                dialogTerm(ActivitySettings.this);
                return false;
            }
        });

        initToolbar();
    }

    public void dialogTerm(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.pref_title_term));
        builder.setMessage(activity.getString(R.string.content_term));
        builder.setPositiveButton("OK", null);
        builder.show();
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    private void initToolbar() {
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.activity_title_settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



    /*
     * Support for Activity : DO NOT CODE BELOW ----------------------------------------------------
     */

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

}
