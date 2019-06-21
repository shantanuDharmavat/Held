package com.held.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.held.utils.AppConstants;
import com.held.utils.PreferenceHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import timber.log.Timber;

/**
 * Created by admin on 29-Feb-16.
 */
public class CameraSurfaceView extends ParentActivity implements SurfaceHolder.Callback{
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera.PictureCallback rawCallback;
    Camera.ShutterCallback shutterCallback;
    Camera.PictureCallback jpegCallback;
    boolean previewing = false;
    private static boolean flash = false;
    private GestureDetector mGesDetect;
    FileOutputStream outStream = null;
    String IS_CAMERA = "camera";
    final int THUMBSIZE = 128;
    private final String tag = "CAMERASCREEN";
    private static boolean hasCamera = false;
    private static boolean cameraFacingFront = false;
    private int cameraId,cameraNo;
    private Context context = this;

    ImageButton mCaptureButton;
    ImageView mFlashButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            Timber.d("on create of camera called");
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_surface_view);
            final Context context = this;
            surfaceView = (SurfaceView) findViewById(R.id.surface);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mGesDetect = new GestureDetector(this, new GestureListener());
            rawCallback = new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.d("Log", "onPictureTaken - raw");
                }
            };
            mFlashButton = (ImageView) findViewById(R.id.FlashButton);
            if(flash)
                mFlashButton.setImageResource(R.drawable.flash_enabled);
            else
                mFlashButton.setImageResource(R.drawable.flash_disabled);
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
//                if(flash == false)
//                    mFlashButton.setImageResource(R.drawable.flash_disabled);
//                else
//                    mFlashButton.setImageResource(R.drawable.flash_enabled);

                mFlashButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (flash == false) {
                            flash = true;
                            mFlashButton.setImageResource(R.drawable.flash_enabled);
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast,
                                    (ViewGroup) findViewById(R.id.custom_toast_layout_id));

                            // set a message
                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText("Flash mode on");

                            // Toast...
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            //mFlashButton.setImageResource(R.drawable.flash_enabled);

                        } else if (flash == true) {
                            /*Toast.makeText(context, "Flash mode off",
                                    Toast.LENGTH_SHORT).show();
                            Timber.d("FLASH : OFF");*/
                            flash = false;
                            mFlashButton.setImageResource(R.drawable.flash_disabled);
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast,
                                    (ViewGroup) findViewById(R.id.custom_toast_layout_id));

                            // set a message
                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText("Flash mode off");

                            // Toast...
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                            //mFlashButton.setImageResource(R.drawable.flash_disabled);
                        }
                    }
                });
            } else {
                mFlashButton.setVisibility(View.GONE);
            }


            /** Handles data for jpeg picture */
            shutterCallback = new Camera.ShutterCallback() {
                public void onShutter() {
                    Log.i("Log", "onShutter'd");
                }
            };

        /*mGalleryButton= (ImageButton) findViewById(R.id.GalleryButton);
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.e("Gallery on click called");
                openGallery();
            }
        });
*/
            jpegCallback = new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    IS_CAMERA = "camera";
                    Intent intent = new Intent(CameraSurfaceView.this, PostActivity.class);
                    String filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "HELD";
                    String ImagePath = String.format("IMG_%d.jpg", System.currentTimeMillis());
                    File file = new File(filePath, ImagePath);
                    String pathString = Uri.fromFile(file).toString();
                    try {
                        outStream = new FileOutputStream(file);
                        outStream.write(data);
                        outStream.close();

                        intent.putExtra("REQUEST", pathString);
                        intent.putExtra("type", IS_CAMERA);
                        Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                        startActivity(intent);
                        finish();
                        return;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    }
                    Log.d("Log", "onPictureTaken - jpeg");
                }
            };

            surfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mGesDetect.onTouchEvent(event);
                    Timber.e("TAP ON CAMERA");
                    return true;
                }

            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void captureImage() {
        // TODO Auto-generated method stub
        try {
            camera.takePicture(shutterCallback, rawCallback, jpegCallback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        /*CODE 1
        Timber.e("SURFACECHANGED CALLED");
        Camera.Parameters parameters = camera.getParameters();
        camera.setDisplayOrientation(90);
        Camera.Size size = getBestPreviewSize(width, height);
        parameters.setPreviewSize(size.width, size.height);
        camera.setParameters(parameters);
        camera.startPreview();*/


        /*CODE 2
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size previewSize = previewSizes.get(4); //480h x 720w

        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        camera.setParameters(parameters);

        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if(display.getRotation() == Surface.ROTATION_0) {
            camera.setDisplayOrientation(90);
        } else if(display.getRotation() == Surface.ROTATION_270) {
            camera.setDisplayOrientation(180);
        }

        camera.startPreview();*/
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (cameraFacingFront == false){
            openBackCamera(holder);
        }
        else if (cameraFacingFront == true) {
            openFrontCamera(holder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        releaseCameraAndPreview();
        finish();
    }

    public void openBackCamera(SurfaceHolder holder) {
            try {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = displaymetrics.heightPixels;
                int width = displaymetrics.widthPixels;

                camera = Camera.open();
                camera.setDisplayOrientation(90);

                mCaptureButton = (ImageButton) findViewById(R.id.CaptureButton);
                mCaptureButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        captureImage();
                    }
                });

                Camera.Parameters param;
                param = camera.getParameters();

                List<Camera.Size> size = param.getSupportedPreviewSizes();
                List<Camera.Size> size2 = param.getSupportedPictureSizes();

                if (flash == true)
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                else if (flash == false)
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

                param.setPreviewFrameRate(20);
                param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                param.setRotation(90);
                param.setPictureSize(size2.get(1).width, size2.get(1).height);

                for (int i = 0; i < size.size(); i++){
                        Timber.e(i + ": width: " + size.get(i).width + " height: " + size.get(i).height);
                }
                for (int i = 0; i < size.size(); i++){
                    Timber.e(i + ": width: " + size2.get(i).width + " height: " + size.get(i).height);
                }

                Timber.e("Screen width: " + width + " Cam height: " + height);
                for (int i = 0; i < size.size(); i++){
                    if (size.get(i).width == height && size.get(i).height == width) {
                        param.setPreviewSize(size.get(i).width, size.get(i).height);
                        Timber.e(i + ": width: " + size.get(i).width + " height: " + size.get(i).height);
                    }
                }

                for (int i = 0; i < size2.size(); i++){
                    if (size2.get(i).width == height && size2.get(i).height == width) {
                        param.setPreviewSize(size2.get(i).width, size2.get(i).height);
                        Timber.e(i + ": width: " + size2.get(i).width + " height: " + size2.get(i).height);
                    }
                }

                camera.setParameters(param);
                camera.setPreviewDisplay(holder);
                camera.startPreview();


                param.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
                param.setExposureCompensation(param.getMaxExposureCompensation());
                param.setExposureCompensation(param.getMaxExposureCompensation());

                if(param.isAutoExposureLockSupported()) {
                    param.setAutoExposureLock(false);
                }

                camera.setParameters(param);

                //camera.takePicture(shutter, raw, jpeg)
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void openFrontCamera(SurfaceHolder holder){
        try{
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            Timber.e("FRONT CAM");
            camera = Camera.open(1);
            camera.setDisplayOrientation(90);

            mCaptureButton= (ImageButton) findViewById(R.id.CaptureButton);
            mCaptureButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    captureImage();
                }
            });

            Camera.Parameters param;
            param = camera.getParameters();

            List<Camera.Size> sizes = param.getSupportedPreviewSizes();
            List<Camera.Size> size2 = param.getSupportedPictureSizes();

            param.setPreviewFrameRate(20);
            //param.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            param.setRotation(270);

            Camera.Size optimalSize = getOptimalPreviewSize(sizes, width, height);

            param.setPreviewSize(optimalSize.width, optimalSize.height);
            Timber.d("Setting optimal width: " + optimalSize.width + " height: " + optimalSize.height);

           /* for (int i = 0; i < size.size(); i++){
                if (size.get(i).width == height && size.get(i).height == width) {
                    param.setPreviewSize(size.get(i).width, size.get(i).height);
                    Timber.e("front" + i + ": width: " + size.get(i).width + " height: " + size.get(i).height);
                }
            }

            for (int i = 0; i < size2.size(); i++){
                if (size2.get(i).width == height && size2.get(i).height == width) {
                    param.setPreviewSize(size2.get(i).width, size2.get(i).height);
                    Timber.e("front" + i + ": width: " + size2.get(i).width + " height: " + size2.get(i).height);
                }
            }*/

            camera.setParameters(param);
            camera.setPreviewDisplay(holder);
            camera.startPreview();

           // param.setPreviewSize(optimalSize.height, optimalSize.width);
            camera.setParameters(param);
            //camera.takePicture(shutter, raw, jpeg)
        }catch(RuntimeException e){
            Log.e(tag, "init_camera: " + e);
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.9;
        double targetRatio = (double) w/h;
        Timber.d("Targe size: " + w + " X " + h + " target ratio: " + targetRatio);

        if (sizes==null) return null;

        Camera.Size optimalSize = null;

        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Find size
        for (Camera.Size size : sizes) {
            Timber.d("checking size " + size.width + " X " + size.height);
            double ratio = (double)  size.height/ size.width ;
            double diff =  Math.abs(ratio - targetRatio);
            if ( diff > ASPECT_TOLERANCE){
                Timber.d("ratio: " + ratio + " diff: " + diff);
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            Timber.d("optimal size is null");
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Timber.d("returning optimal size: " + optimalSize.width + " X " + optimalSize.height);
        return optimalSize;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("in camerasurface on activity result");

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==AppConstants.REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            // Get the Image from data
            IS_CAMERA = "gallery";
            String selectedImage = data.getData().toString();

            Timber.e("Gallery image path : " + data.getData().toString());

            Intent intent = new Intent(CameraSurfaceView.this, PostActivity.class);
            intent.putExtra("REQUEST", selectedImage);
            intent.putExtra("type", IS_CAMERA);
            Timber.e("Gallery image path : " + selectedImage);
            startActivity(intent);
            releaseCameraAndPreview();
            finish();
        }

    }

    private void releaseCameraAndPreview() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height)
    {
        Camera.Size result=null;
        Camera.Parameters p = camera.getParameters();
        for (Camera.Size size : p.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                } else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseCameraAndPreview();
        finish();
    }

    private void launchProfileScreen(String uid) {
        Intent intent = new Intent(CameraSurfaceView.this, ProfileActivity.class);
        intent.putExtra("user_id", uid);
        startActivity(intent);
    }

    @Override
    public void onLeftSwipe() {
        // Do something
        String selfID = PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_regId));
        launchProfileScreen(selfID);
    }

    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        Timber.d("on save instance of camera called");
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        Timber.d("on restore of camera called");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause(){
        super.onPause();
        Timber.d("camera activity paused");
    }

    @Override
    public void onResume(){
        super.onResume();
        Timber.d("camera on resume called");
    }

    @Override
    public void onDestroy(){
        Timber.d("camera activity destroyed");
        super.onDestroy();
    }

    @Override
    public void onRightSwipe() {
        // Do something
        finish();
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d("TAG", "Double Tap Detected ...");
            if (cameraFacingFront == false) {
                cameraFacingFront = true;
                Intent intent = getIntent();
                intent.putExtra("camera", "back");
                camera.stopPreview();
                releaseCameraAndPreview();
                surfaceHolder.removeCallback(null);
                startActivity(intent);
                finish();
            }
            else if (cameraFacingFront == true) {
                cameraFacingFront = false;
                Intent intent = getIntent();
                intent.putExtra("camera", "front");
                camera.stopPreview();
                releaseCameraAndPreview();
                surfaceHolder.removeCallback(null);
                startActivity(intent);
                finish();
            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);

        }
    }
}

/*

    public void openGallery(){
        Timber.e("Inside Start Activity for result");
        */
/*Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, AppConstants.REQUEST_GALLERY);*//*

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.setType("image*/
/*");
        Intent chooser = Intent.createChooser(intent,
                "Choose a Picture");

        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, AppConstants.REQUEST_GALLERY);
    }

*/
