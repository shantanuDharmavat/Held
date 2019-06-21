package com.held.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import timber.log.Timber;

import static timber.log.Timber.i;


public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        // If user has uninstalled-installed the app.
        // i.e. we were unable to call logout API but user is actually logged out.
//        if (TextUtils.isEmpty(PreferenceHelper.getSessionToken())) return;

        // Explicitly specify that GcmIntentService will handle the intent.
        Timber.d("gcm received");
        Timber.i("Inside GCM onRecive");
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        Timber.i("Inside onRecive");
        setResultCode(Activity.RESULT_OK);
    }
}