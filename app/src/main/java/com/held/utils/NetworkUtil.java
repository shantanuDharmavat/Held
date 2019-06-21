package com.held.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public enum NETWORK_TYPE {

        TYPE_MOBILE(ConnectivityManager.TYPE_MOBILE),   //ConnectivityManager.TYPE_MOBILE == 0
        TYPE_WIFI(ConnectivityManager.TYPE_WIFI),     //ConnectivityManager.TYPE_WIFI ==1
        TYPE_NOT_CONNECTED(-1);  //No Connection

        private final int ID;

        NETWORK_TYPE(int id) {
            ID = id;
        }

        public static NETWORK_TYPE getNetworkTypeFromId(int id) {

            if (id == TYPE_WIFI.ID) {
                return TYPE_WIFI;

            } else if (id == TYPE_MOBILE.ID) {
                return TYPE_MOBILE;

            } else {
                return TYPE_NOT_CONNECTED;
            }
        }
    }

    public static Boolean isInternetConnected(Context ctx) {
        ConnectivityManager con = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (con != null) {
            NetworkInfo activeNetwork = con.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isAvailable() && activeNetwork.isConnected()) {
                return true;
            }
        }
        return false;
    }
}
