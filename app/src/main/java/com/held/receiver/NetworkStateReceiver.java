package com.held.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.held.utils.HeldApplication;
import com.held.utils.NetworkUtil;

import java.util.ArrayList;

public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkStateReceiver.class.getSimpleName();

    // list of observer
    private static ArrayList<OnNetworkChangeListener> mOnNetworkChangeListenerList;

    /**
     * Registering client for Network related updates
     * *
     */
    public static synchronized void registerOnNetworkChangeListener(OnNetworkChangeListener listener) {

        if (mOnNetworkChangeListenerList == null) {
            mOnNetworkChangeListenerList = new ArrayList<>();
        }
        mOnNetworkChangeListenerList.add(listener);
    }

    /**
     * Unregister client for Network related updates
     * *
     */
    public static synchronized void unregisterOnNetworkChangeListener(OnNetworkChangeListener listener) {
        mOnNetworkChangeListenerList.remove(listener);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        boolean isEnabled = NetworkUtil.isInternetConnected(HeldApplication.getAppContext());
        notifyObservers(isEnabled);
    }

    private void notifyObservers(boolean isEnabled) {
        if (mOnNetworkChangeListenerList != null) {
            for (OnNetworkChangeListener listener : mOnNetworkChangeListenerList) {
                listener.onNetworkStatusChanged(isEnabled);
            }
        }
    }

    public interface OnNetworkChangeListener {
        void onNetworkStatusChanged(boolean isEnabled);
    }

}
