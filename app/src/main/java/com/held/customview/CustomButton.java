package com.held.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.held.utils.CustomFontHelper;


/**
 * Created by YMediaLabs on 20/11/14.
 */
public class CustomButton extends Button {


    public CustomButton(Context context) {
        super(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

}