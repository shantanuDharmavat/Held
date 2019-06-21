package com.held.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.held.activity.R;


/**
 * Created by anil on 26/6/15.
 */
public class CustomFontHelper {

    /**
     * Sets a font on a textview based on the custom com.my.package:font attribute
     * If the custom font attribute isn't found in the attributes nothing happens
     *
     * @param textview
     * @param context
     * @param attrs
     */
    public static void setCustomFont(TextView textview, Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTypeFace);
        String font;
        if (a == null) {
            font = context.getApplicationContext().getString(R.string.freight_sans_medium);

        } else {
            font = a.getString(R.styleable.CustomTypeFace_typeFace);
            if (font == null) {
                font = context.getApplicationContext().getString(R.string.freight_sans_medium);
            }
        }
        setCustomFont(textview, font, context);
        a.recycle();
    }

    /**
     * Sets a font on a textview
     *
     * @param textview
     * @param font
     * @param context
     */
    public static void setCustomFont(TextView textview, String font, Context context) {
        if (font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, context);
        if (tf != null) {
            textview.setTypeface(tf);
        }
    }

    /*private void setCustomTypeFace(AttributeSet attrs) {

        if (!isInEditMode()) {

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTypeFace);
            if (a == null) {
                this.setTypeface(UIUtils.getDefaultTypeFace(getContext()));
                return;
            }
            CharSequence s = a.getString(R.styleable.CustomTypeFace_typeFace);
            if (!TextUtils.isEmpty(s)) {
                this.setTypeface(UIUtils.getTypeFace(getContext(), s.toString()));
            } else {
                this.setTypeface(UIUtils.getDefaultTypeFace(getContext()));
            }
            a.recycle();  //Added here to recycle
        }
    }*/

}