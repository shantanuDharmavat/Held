package com.held.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import com.held.activity.R;
import com.held.activity.RegistrationActivity;

public class CircularImageView extends ImageView {


    private Paint paintCanvas=new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintBorder=new Paint(Paint.ANTI_ALIAS_FLAG);
    private float borderWidth;
    public CircularImageView(Context context) {
        super(context);
        Log.i("CustomView", "Circular image");
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("CustomView", "Circular image");
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i("CustomView", "Circular image");
    }


    @Override
    protected void onDraw(Canvas canvas) {
    int radius;
        int w = getWidth(), h = getHeight();
        radius=Math.min(w, h);
        Drawable drawable = getDrawable();

        if (drawable == null) {
            drawRing(canvas,radius);
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
       // canvas = new Canvas(bitmap.copy(Bitmap.Config.ARGB_8888, true));
        Bitmap roundBitmap= getCroppedBitmap(bitmap, radius);
        canvas.drawBitmap(roundBitmap, 0, 0, null);



    }

    private Bitmap getCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap;

        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius,
                    false);
        else
            finalBitmap = bitmap;
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        final Rect rect = new Rect(0, 0,getWidth(),
                getHeight());
        paintCanvas.setColor(Color.WHITE);
        paintCanvas.setAntiAlias(true);
        paintBorder.setAntiAlias(true);

        paintCanvas.setFilterBitmap(true);
        paintBorder.setFilterBitmap(true);

        paintCanvas.setDither(true);
        paintBorder.setDither(true);


        canvas.drawARGB(0, 0, 0, 0);
        paintCanvas.setColor(Color.WHITE);
        paintBorder.setColor(this.getResources().getColor(R.color.friend_req_color));
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(borderWidth);

        canvas.drawCircle(finalBitmap.getWidth() / 2 + 0.7f,
                finalBitmap.getHeight() / 2 + 0.7f,
                finalBitmap.getWidth() / 2 + 0.1f, paintCanvas);
        canvas.drawBitmap(finalBitmap, rect, rect, paintCanvas);
        paintCanvas.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawCircle(radius / 2 + 0.7f,
                radius / 2 + 0.7f, radius / 2 - paintBorder.getStrokeWidth()/2 +0.1f, paintBorder);
        return output;

    }
    public void setBorderColor(int borderColor){
        if (paintBorder != null)
            paintBorder.setColor(borderColor);
        this.invalidate();

    }
    public void setBorderWidth(float borderWidth){
        this.borderWidth = borderWidth;
        this.invalidate();
    }
    public void drawRing(Canvas canvas,int radius){

        paintCanvas.setAntiAlias(true);
        paintBorder.setAntiAlias(true);
        paintCanvas.setFilterBitmap(true);
        paintBorder.setFilterBitmap(true);
        paintCanvas.setDither(true);
        paintBorder.setDither(true);
        paintCanvas.setColor(Color.WHITE);
        paintBorder.setColor(this.getResources().getColor(R.color.friend_req_color));
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(borderWidth);
        paintCanvas.setStrokeJoin(Paint.Join.ROUND);
        paintCanvas.setStrokeCap(Paint.Cap.ROUND);
        paintBorder.setStrokeJoin(Paint.Join.ROUND);
        paintBorder.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawARGB(0, 0, 0, 0);
//        canvas.drawCircle(getWidth() / 2 + 0.7f,
//                getHeight() / 2 + 0.7f,
//                getWidth() / 2 + 0.1f, paintCanvas);
 //       paintCanvas.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect rect = new Rect(0, 0,getWidth(),
                getHeight());

        canvas.drawCircle(radius / 2 ,
                radius / 2 , radius / 2-borderWidth/2, paintBorder);
    }


}