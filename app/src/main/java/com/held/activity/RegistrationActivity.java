package com.held.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.held.customview.CustomTextView;
import com.held.receiver.NetworkStateReceiver;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.CreateUserResponse;
import com.held.retrofit.response.ProfilPicUpdateResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.ImageUtils;
import com.held.utils.NetworkUtil;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import timber.log.Timber;

public class RegistrationActivity extends ParentActivity implements View.OnClickListener, NetworkStateReceiver.OnNetworkChangeListener {

    private static final String TAG = "RegistrationActivity";
    private ImageView mBackImg,mAddPicIcon;
    private EditText mUserNameEdt, mPhoneNoEdt;
    private boolean mIsNetworkConnected,flag=false;
    private Button mRegisterBtn;
    private Spinner mCountryCodes;
    private String mCountryCode,tempCode,mphoneNo;
    private int mPin;
    private String mRegKey,mAccessToken,sourceFileName;
    private PreferenceHelper mPreference;
    private CustomTextView mspinnerText;
    private File mFile;
    private Uri mFileUri;
    private ImageView circularImage;
    private com.held.customview.CircularImageView customRing;
    private LinearLayout uploadPicLayout,dummyLinearLayout, registerLayout;
    private TextView loginHeader;
    View statusBar;


private TextView mPolicy;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "starting Registration activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        statusBar=(View)findViewById(R.id.statusBarView);
//        Window w = getWindow();
//
//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//            w.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            w.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            statusBar.setVisibility(View.VISIBLE);
//
//        }else {
//            statusBar.setVisibility(View.GONE);
//        }
        if(getIntent().getExtras()!=null)
            flag=getIntent().getExtras().getBoolean("ForLogin");
        mUserNameEdt = (EditText) findViewById(R.id.REG_user_name_edt);
        mPhoneNoEdt = (EditText) findViewById(R.id.REG_mobile_no_edt);
        mBackImg = (ImageView) findViewById(R.id.REG_back);
        mRegisterBtn = (Button) findViewById(R.id.REG_register_btn);
        mspinnerText=(CustomTextView)findViewById(R.id.spinner_Text);
        mAddPicIcon=(ImageView)findViewById(R.id.addimageIcon);
        customRing=(com.held.customview.CircularImageView)findViewById(R.id.customRing);
        customRing.setBorderWidth(2);
        circularImage = (ImageView)findViewById(R.id.profile_pic);
        loginHeader=(TextView)findViewById(R.id.loginHeaderText);
//        circularImage.setVisibility(View.GONE);
        uploadPicLayout=(LinearLayout)findViewById(R.id.photoUpload_Layout);
        //dummyLinearLayout=(LinearLayout)findViewById(R.id.dummy_Layout);
        registerLayout=(LinearLayout)findViewById(R.id.main_layout);
        mAddPicIcon.setOnClickListener(this);
        mBackImg.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
        mIsNetworkConnected = NetworkUtil.isInternetConnected(getApplicationContext());
        NetworkStateReceiver.registerOnNetworkChangeListener(this);
        mCountryCodes = (Spinner) findViewById(R.id.REG_country_code_edt);
        String countryCodes[] = getResources().getStringArray(R.array.country_codes);
        mCountryCodes.setAdapter(new ArrayAdapter(this, R.layout.row_spinner_item, countryCodes));
        ArrayAdapter myAdap = (ArrayAdapter) mCountryCodes.getAdapter();
//        int spinnerPosition = myAdap.getPosition("+91 (India)");
        mCountryCodes.setSelection(setCountryCode());
//        setCountryCode();
        mPolicy=(TextView)findViewById(R.id.SPLASH_terms_condition_txt);


        setTypeFace(mUserNameEdt,"book");
        setTypeFace(mPhoneNoEdt,"book");
        setTypeFace(mRegisterBtn,"book");
        setTypeFace(mPolicy,"book");
        setTypeFace(loginHeader,"medium");




        mPreference=PreferenceHelper.getInstance(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(flag)
        {
            updateToLoginUI();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.REG_back:
                onBackPressed();
                break;
            case R.id.REG_register_btn:
                Utils.hideSoftKeyboard(this);
                if(getNetworkStatus()) {
                    if (flag) {
                        loginValidation();
                    } else {
                        validateInput();
                    }
                }
                else
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), Utils.getString(R.string.error_offline_msg));
                break;
            case R.id.addimageIcon:
                openImageIntent();

        }
    }

    private void validateInput() {

        boolean validate= false;
        /*if(validateUserName())
            if(validatePhoneNo())
                if (validateProfilePic())
                    validate=true;

        if (validate) {
            String cc[] = mCountryCodes.getSelectedItem().toString().split(" ");
            mCountryCode = cc[0];
            tempCode=mCountryCode;
            mCountryCode=tempCode.substring(1);
            if (mNetWorStatus) {
                DialogUtils.showProgressBar();
                    callCreateUserApi();
            } else {
                UiUtils.showSnackbarToast(findViewById(R.id.root_view), "You are not connected to internet.");
            }

*/
        if (!TextUtils.isEmpty(mUserNameEdt.getText().toString().trim()))
        {
            if (mUserNameEdt.getText().toString().trim().length() < 6 || mUserNameEdt.getText().toString().trim().length() > 15) {
                mUserNameEdt.setError("Please enter between 6 to 15 chars.");
                mUserNameEdt.requestFocus();
                return;

            }

            if (!containsChar(mUserNameEdt.getText().toString())) {
                mUserNameEdt.setError("Please enter Alphabet and digits.");
                mUserNameEdt.requestFocus();
                return;
            }

        }else {
            mUserNameEdt.setError("Please enter user name");
            mUserNameEdt.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(mPhoneNoEdt.getText().toString())){
            mPhoneNoEdt.setError("Please enter valid phone no.");
            mUserNameEdt.requestFocus();
            return;
        }



        if(mPhoneNoEdt.getText().toString().trim().length() < 10)
        {
            mPhoneNoEdt.setError("Please enter valid phone no.");
            mUserNameEdt.requestFocus();
            return;
        }


        String cc[] = mCountryCodes.getSelectedItem().toString().split(" ");
        mCountryCode = cc[0];
        tempCode=mCountryCode;
        mCountryCode=tempCode.substring(1);
        if (mFile==null) {
            UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Please, select or capture an image for profile picture");
            return;

        }

        if (mIsNetworkConnected) {
            DialogUtils.showDarkProgressBar();
                callCreateUserApi();
        } else {
            UiUtils.showSnackbarToast(findViewById(R.id.root_view), "You are not connected to internet.");
        }

    }

    private boolean containsChar(String s) {
        return s.matches("[a-zA-Z0-9]*");
    }

    private void callCreateUserApi() {
        HeldService.getService().createUser(mUserNameEdt.getText().toString().trim().toLowerCase(), mCountryCode + mPhoneNoEdt.getText().toString().trim(), "", new Callback<CreateUserResponse>() {
            @Override
            public void success(CreateUserResponse createUserResponse, Response response) {
                if (createUserResponse == null) {
                    return;
                }
                mPreference.writePreference(getString(R.string.API_phone_no), mCountryCode + mPhoneNoEdt.getText().toString().trim());
                mPreference.writePreference(getString(R.string.API_user_name), mUserNameEdt.getText().toString().trim());

                mPin = createUserResponse.getPin();
                mRegKey = createUserResponse.getRid();
                mAccessToken = createUserResponse.getAccessToken();
                PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_session_token), mAccessToken);
                PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.REG_Session_token), mAccessToken);
                PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.REG_RID), mRegKey);

                callUpdateProfileImageApi();
                Log.i("RegistrationActivity", "Profile PIN" + createUserResponse.toString());

            }

            @Override
            public void failure(RetrofitError error) {
                DialogUtils.stopProgressDialog();
                if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                } else
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Some Problem Occurred");

            }
        });
    }

    private void launchVerificationActivity() {
        Intent intent = new Intent(RegistrationActivity.this, VerificationActivity.class);
        intent.putExtra("username", mUserNameEdt.getText().toString().trim());
        intent.putExtra("phoneno", mCountryCode + mPhoneNoEdt.getText().toString().trim());
        intent.putExtra("pin", mPin);
        intent.putExtra("regId",mRegKey);
        Log.d(TAG, "sending registration id : " + mRegKey);
        Log.d(TAG, "sending access tokes to verify activity: " + mAccessToken);
        intent.putExtra("accessToken",mAccessToken);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNetworkStatusChanged(boolean isEnabled) {
        mIsNetworkConnected = isEnabled;
    }

    public void updateToLoginUI(){

        registerLayout.setPadding(0, 140, 0, 0);
        uploadPicLayout.setVisibility(View.GONE);
        mUserNameEdt.setVisibility(View.GONE);
        mRegisterBtn.setText("Login");
        loginHeader.setVisibility(View.VISIBLE);
    }

    public void  launchLoginVerificationActivity(){

        Intent intent = new Intent(RegistrationActivity.this, VerificationActivity.class);
        intent.putExtra("phoneno", mCountryCode + mPhoneNoEdt.getText().toString().trim());
        intent.putExtra("ForLogin", true);
        startActivity(intent);
        finish();
    }
    public void launchSplashScreen(){
        Intent intent = new Intent(RegistrationActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
    public void openImageIntent() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Source");
        CharSequence charSequence[] = {"Camera ", "Gallery"};
        builder.setItems(charSequence,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case 0:
                                // GET IMAGE FROM THE CAMERA
                                Intent getCameraImage = new Intent(
                                        "android.media.action.IMAGE_CAPTURE");
                                getCameraImage.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                File cameraFolder;
                                cameraFolder = new File(Environment
                                        .getExternalStorageDirectory(), "/HELD");
                                if (!cameraFolder.exists())
                                    cameraFolder.mkdirs();
                                sourceFileName = "/IMG_"
                                        + System.currentTimeMillis() + ".jpg";
                                File photo = new File(cameraFolder, sourceFileName);
                                getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photo));
                                startActivityForResult(getCameraImage, AppConstants.REQUEST_CAMEAR);
                                break;

                            case 1:
                                Intent intent;
                                intent = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                Intent chooser = Intent.createChooser(intent,
                                        "Choose a Picture");
                                startActivityForResult(chooser, AppConstants.REQUEST_GALLERY);
                                break;

                            default:
                                break;
                        }
                    }
                });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AppConstants.REQUEST_CAMEAR:
                    File photo = new File(Environment.getExternalStorageDirectory(),
                            "/HELD" + sourceFileName);

                    if (!photo.exists())
                        photo.mkdir();

                    Uri photoUri = Uri.fromFile(photo);
                    doCrop(photoUri);
                    mFileUri=photoUri;
                    mFile = new File(photoUri.getPath());
                    //PicassoCache.getPicassoInstance(this).load(mFile).noFade().into(circularImage);
                    updateImageview();
                    break;

                case AppConstants.REQUEST_GALLERY:
                    Uri PhotoURI = data.getData();
                    doCrop(PhotoURI);
                    mFileUri=PhotoURI;
                    mFile = new File(getRealPathFromURI(PhotoURI));
                   // PicassoCache.getPicassoInstance(this).load(mFile).noFade().into(circularImage);
                    updateImageview();
                    break;
                case  AppConstants.REQUEST_CROP:
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = bundle.getParcelable("data");
                    mFile = new File(getRealPathFromURI(getImageUri(this,bitmap)));
                    updateImageview();
            }
        }

        if (requestCode == AppConstants.REQUEST_CROP) {
            File photo = new File(Environment.getExternalStorageDirectory(),
                    "/HELD" + sourceFileName);

            if (resultCode != Activity.RESULT_OK) {
                photo.delete();
                return;
            }

        }
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result = "";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(filePathColumn[0]);//MediaStore.Images.ImageColumns.DATA
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }



    public void callUpdateProfileImageApi(){
        Timber.d("Resizing image");
        String newFpath = ImageUtils.resizeImage(mFile.getPath(), 200);
        Timber.d("resize done");
        mFile = new File(newFpath);
        HeldService.getService().updateProfilePic(mAccessToken, mRegKey, new TypedFile("multipart/form-data", mFile), new Callback<ProfilPicUpdateResponse>() {
            @Override
            public void success(ProfilPicUpdateResponse profilPicUpdateResponse, Response response) {
                DialogUtils.stopProgressDialog();
                Timber.i(TAG, "Profile pic updated..");
                callResendSmsApi();
            }

            @Override
            public void failure(RetrofitError error) {
                DialogUtils.stopProgressDialog();
                if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                } else
                    UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), "Some Problem Occurred");

            }
        });
    }
    public void updateImageview(){
        mAddPicIcon.setVisibility(View.GONE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        UiUtils.calculateInSampleSize(options,
                UiUtils.dpToPx(getResources(), 350),
                UiUtils.dpToPx(getResources(), 350));
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        Bitmap mAttachment;


////        PicassoCache.getPicassoInstance(this).load(mFileUri).noFade().into(mProfileimg);
//
        try {
            circularImage.setImageURI(Uri.fromFile(mFile));
            circularImage.setImageBitmap(BitmapFactory.decodeFile(mFile.getAbsolutePath(), options));
//            Timber.i(TAG, mFileUri.getPath().toString());
//            Timber.i(TAG, mFile.getAbsolutePath().toString());
//            mAttachment = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mFileUri);
////         mAttachment = BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
//           circularImage.setImageBitmap(mAttachment);
//
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        customRing.setVisibility(View.GONE);
//        circularImage.setVisibility(View.VISIBLE);
    }
    private void doCrop(Uri mCurrentPhotoPath) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(mCurrentPhotoPath, "image/*");

        try {
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 320);
            cropIntent.putExtra("outputY", 320);
            cropIntent.putExtra("return-data", true);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
        }
        File cameraFolder;

        cameraFolder = new File(Environment.getExternalStorageDirectory(),
                "/HELD");

        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs();
        }

        sourceFileName = "/IMG_" + System.currentTimeMillis() + ".jpg";

        File photo = new File(cameraFolder, sourceFileName);
        try {
            photo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mFile =new File(photo.getPath());

        Uri mCropImageUri = Uri.fromFile(photo);

        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImageUri);
        startActivityForResult(cropIntent, AppConstants.REQUEST_CROP);

    }

    @Override
    public void onBackPressed() {
       this.finish();
    }
    public void setTypeFace(TextView tv,String type){
        Typeface medium = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansBook.otf");
        if(type=="book"){
            tv.setTypeface(book);

        }else {
            tv.setTypeface(medium);
        }
    }
    public void setTypeFace(Button tv,String type){
        Typeface medium = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(this.getResources().getAssets(), "BentonSansBook.otf");
        if(type=="book"){
            tv.setTypeface(book);

        }else {
            tv.setTypeface(medium);
        }
    }
    public void callLoginResendSmsApi(){
        DialogUtils.showProgressBar();
        String cc[] = mCountryCodes.getSelectedItem().toString().split(" ");
        mCountryCode = cc[0];
        tempCode=mCountryCode;
        mCountryCode=tempCode.substring(1);
        mPreference.writePreference(getString(R.string.API_phone_no), mCountryCode + mPhoneNoEdt.getText().toString().trim());
        HeldService.getService().loginSessionSendPinSmsApi(mCountryCode + mPhoneNoEdt.getText().toString().trim(), "",
                new Callback<CreateUserResponse>() {
                    @Override
                    public void success(CreateUserResponse createUserResponse, Response response) {
                        launchLoginVerificationActivity();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            String strError="";
                            strError="Invalid User..";
                            UiUtils.showSnackbarToast(findViewById(R.id.root_view),strError);
                        } else
                            UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Some Problem Occurred");
                    }
                });
    }
    private void callResendSmsApi() {
        HeldService.getService().resendSms(mPreference.readPreference(getString(R.string.API_session_token)), mRegKey, new Callback<CreateUserResponse>() {
            @Override
            public void success(CreateUserResponse createUserResponse, Response response) {
                DialogUtils.stopProgressDialog();
                launchVerificationActivity();
                // mPreference.writePreference(getString(R.string.API_pin), createUserResponse.getPin());
            }

            @Override
            public void failure(RetrofitError error) {
                DialogUtils.stopProgressDialog();
                if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                } else
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Some Problem Occurred");
            }
        });
    }
    public int setCountryCode(){
        TelephonyManager telephonyManager=(TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String contry_code=telephonyManager.getNetworkCountryIso();
        Locale locale=new Locale("en",contry_code);
        CharSequence countryNameDevice = locale.getDisplayCountry();
       // StringBuffer stringBuffer =new StringBuffer(countryNameDevice);
        String countryCodes[] = getResources().getStringArray(R.array.country_codes);
        int position=0;
        String countrycode;
        for(int i=0;i<countryCodes.length;i++)
        {
            countrycode=countryCodes[i];
            if(countrycode.contains(countryNameDevice))
            {
                Log.i(TAG, "@@@@@" +countryCodes[i]);
                position= i;
                break;
            }
        }

        return position;
//        TelephonyManager telephonyManager=(TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
//        String contry_code=telephonyManager.getNetworkCountryIso();
//        Locale locale=new Locale("en",contry_code);
//        Timber.i(TAG,"@@@@@"+contry_code);
//        Log.i(TAG, "@@@@@" + contry_code);
//        Log.i(TAG, "@@@@@" + locale.getDisplayCountry());

    }
    void loginValidation(){
        if(validatePhoneNo())
            callLoginResendSmsApi();

    }
    boolean validateUserName(){
        boolean validate=false;
        Timber.i("Inside Validate Username");
        if(TextUtils.isEmpty(mUserNameEdt.getText().toString().trim())){
            UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Please enter valid user name.");
            mUserNameEdt.requestFocus();
        }
        else if (!TextUtils.isEmpty(mUserNameEdt.getText().toString().trim()))
        {
            if (mUserNameEdt.getText().toString().length() < 6) {
                UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Please enter between 6 to 15 chars.");
                mUserNameEdt.requestFocus();
            }
            else {
                if (!containsChar(mUserNameEdt.getText().toString())) {
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Please enter Alphabet and digits.");
                    mUserNameEdt.requestFocus();
                }
                else
                    validate=true;
            }
        }
        return validate;
    }
    boolean validatePhoneNo(){
        boolean validate=false;
        Timber.i("Inside Validate Phoneno");
        if(TextUtils.isEmpty(mPhoneNoEdt.getText().toString())){
            UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Please enter valid phone no.");
            mPhoneNoEdt.requestFocus();
        }
        else if (!TextUtils.isEmpty(mPhoneNoEdt.getText().toString()))
        {
            if(mPhoneNoEdt.getText().toString().trim().length() < 10)
            {
                UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Please enter valid phone no.");
                mPhoneNoEdt.requestFocus();
            }
            else
                validate=true;

        }
        return validate;
    }
    boolean validateProfilePic(){
        boolean validate=false;
        Timber.i("Inside Validate ProfilePic");
        if(mFile==null) {
            Timber.i("Inside Validate check pic ");
            UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Please, select or capture an image for profile image");
        }
        else
            validate=true;

        return validate;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}