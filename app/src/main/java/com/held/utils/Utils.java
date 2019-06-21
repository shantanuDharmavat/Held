package com.held.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.fragment.ParentFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jay on 1/8/15.
 */
public class Utils {

    public static String getString(int resId) {
        return HeldApplication.getAppContext().getString(resId);
    }

    public static Drawable getDrawable(int resId) {
        //noinspection deprecation
        return HeldApplication.getAppContext().getResources().getDrawable(resId);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
//        View focusView = activity.getCurrentFocus();
////        if (focusView != null)
            inputMethodManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }
    public static void showSoftKeyboard(Activity activity) {

            InputMethodManager imm = (InputMethodManager)activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(activity.getWindow().getDecorView(), InputMethodManager.SHOW_IMPLICIT);

    }
    public static ParentFragment getCurrVisibleFragment(ParentActivity activity) {
        if (activity != null) {
            return (ParentFragment) activity.getSupportFragmentManager().findFragmentById(R.id.frag_container);
        }
        return null;
    }

    // Takes a timestamp string and converts it to a human readable 12 hour time string (timeonly. no date)
    public static String convertDate(String stamp){
        long ts = Long.valueOf(stamp);
        Date date = new Date(ts);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String suffix = "AM";
        if(hour > 12){
            hour = hour - 12;
            suffix = "PM";
        }
        int minutes = cal.get(Calendar.MINUTE);
        String min;
        if(minutes < 10){
            min = "0" + minutes;
        }else{
            min = "" + minutes;
        }

        String converted = "" + hour + "." + min + " " + suffix;
        return converted;
    }

}
