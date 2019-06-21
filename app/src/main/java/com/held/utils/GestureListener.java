package com.held.utils;


import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    public GestureListener(Activity activity) {

    }

    @Override
    public boolean onDown(MotionEvent e) {

        Log.d("Down Tap", "Tapped at: )");
        return true;
    }

    // event when double tap occurs
    @Override
    public boolean onDoubleTap(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();

        Log.d("Double Tap", "Tapped at: (" + x + "," + y + ")");

        return true;
    }
}
