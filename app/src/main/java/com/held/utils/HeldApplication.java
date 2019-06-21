package com.held.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;


public class HeldApplication extends Application {
    private static final String TAG = HeldApplication.class.getSimpleName();
    public static boolean IS_APP_FOREGROUND;
    public static boolean IS_CHAT_FOREGROUND;
    private static HeldApplication mInstance;
    private static Context mAppContext;
    private Activity mCurrentActivity = null;

    public static HeldApplication getInstance() {
        return mInstance;
    }

    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        mAppContext = getApplicationContext();

    }

}
