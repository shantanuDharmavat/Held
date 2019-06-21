package com.held.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.held.adapters.FeedAdapter;


public class PreferenceHelper {

    private static final String TAG = PreferenceHelper.class.getSimpleName();
    private static PreferenceHelper mPrefHelper;
    private SharedPreferences mSharedPrefs;

    private PreferenceHelper(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferenceHelper getInstance(Context context) {
        if (mPrefHelper == null)
            mPrefHelper = new PreferenceHelper(context);
        return mPrefHelper;
    }

    public static void clearPreferences() {
        PreferenceHelper.getInstance(HeldApplication.getAppContext()).mSharedPrefs.edit().clear().commit();
    }

    public void writePreference(String key, String value) {
        Editor e = mSharedPrefs.edit();
        e.putString(key, value);
        e.commit();
    }

    public void writePreference(String key, int value) {
        Editor e = mSharedPrefs.edit();
        e.putInt(key, value);
        e.commit();
    }

    public void writePreference(String key, boolean value) {
        Editor e = mSharedPrefs.edit();
        e.putBoolean(key, value);
        e.commit();
    }

    public String readPreference(String key) {
        return mSharedPrefs.getString(key, "");
    }

    public String readPreference(String key, String defValue) {
        return mSharedPrefs.getString(key, defValue);
    }

    public int readPreference(String key, int defaultValue) {
        return mSharedPrefs.getInt(key, defaultValue);
    }

    public boolean readPreference(String key, boolean defaultValue) {
        return mSharedPrefs.getBoolean(key, defaultValue);
    }


}

