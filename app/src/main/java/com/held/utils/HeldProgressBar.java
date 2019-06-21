package com.held.utils;


import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.held.activity.R;

public class HeldProgressBar extends ImageView {
    private Animation rotateAnimation;

    public HeldProgressBar(Context context) {
        super(context);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setImageResource(R.drawable.progress);
        
        rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.progress_rotate_anim);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
    }

    public void showProgressBar() {
        startAnimation(rotateAnimation);
    }

    public void stopProgressBar() {
        clearAnimation();
    }
}
