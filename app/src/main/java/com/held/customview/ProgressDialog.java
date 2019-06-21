package com.held.customview;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.held.activity.R;
import com.held.utils.HeldApplication;

public class ProgressDialog extends Dialog {

    private ImageView mProgressBar;
    private Animation rotateAnimation;
    boolean dark = false;

    public ProgressDialog(Context context) {
        super(context);
    }

    public ProgressDialog(Context context, boolean dark) {
        super(context);
        this.dark = dark;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(this.dark){
            setContentView(R.layout.progress_dialog_dark);
        }else{
            setContentView(R.layout.progress_dialog);
        }

        mProgressBar = (ImageView) findViewById(R.id.progress_bar);
        rotateAnimation = AnimationUtils.loadAnimation(HeldApplication.getAppContext(), R.anim.progress_rotate_anim);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        setCancelable(false);
        int dividerId = getContext().getResources().getIdentifier("titleDivider", "id", "android");
        View divider = findViewById(dividerId);
        if (divider != null)
            divider.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void show() {
        super.show();
        mProgressBar.startAnimation(rotateAnimation);
    }

    @Override
    public void dismiss() {
        mProgressBar.clearAnimation();
        super.dismiss();
    }
}
