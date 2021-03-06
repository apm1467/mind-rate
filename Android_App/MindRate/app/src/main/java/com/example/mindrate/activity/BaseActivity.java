package com.example.mindrate.activity;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.example.mindrate.util.PreferenceUtil;

import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Super class for all activities
 *
 * Project: MindRate
 * Package: com.example.mindrate.activity
 * Author: Ecko Tan
 * E-mail: ecko0804@gmail.com
 * Created at 2017/1/10:17:26
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        PreferenceUtil.init(this);
        switchLanguage(PreferenceUtil.getString("language", "en"));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Change the language according to <code>language</code>
     *
     * @param language
     */
    public void switchLanguage(String language) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else if (language.equals("de")) {
            // TODO: improve because locale is deprecated
            config.locale = Locale.GERMANY;
        }
        // TODO: improve because updateConfiguration is deprecated
        resources.updateConfiguration(config, displayMetrics);

        PreferenceUtil.commitString("language", language);
    }


}
