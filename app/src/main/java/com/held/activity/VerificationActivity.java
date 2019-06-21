package com.held.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.gcm.GCMControlManager;
import com.held.gcm.GcmCallback;
import com.held.receiver.NetworkStateReceiver;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.CreateUserResponse;
import com.held.retrofit.response.LoginUserResponse;
import com.held.retrofit.response.ProfilPicUpdateResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.retrofit.response.VerificationResponse;
import com.held.retrofit.response.VoiceCallResponse;
import com.held.utils.DialogUtils;
import com.held.utils.NetworkUtil;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

/**
 * Created by jay on 1/8/15.
 */
public class VerificationActivity extends ParentActivity implements View.OnClickListener, NetworkStateReceiver.OnNetworkChangeListener {

    public static String TAG = VerificationActivity.class.getSimpleName();

    private EditText mFirstEdt, mSecondEdt, mThirdEdt, mForthEdt;
    private TextView mResendSmsTxt, mVoiceCallTxt, mUserNameTxt, mIndicationTxt,mPhoneTxt;
    private boolean mNetWorkStatus, isBackPressed,flag;
    private String mPhoneNo, mUserName, mPin;
    private String mRegId,mAuth,mSessonToken,mNewRegId;
    private ImageView mback;
    private PreferenceHelper mPreference;
    View statusBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("Activity", "VerificationActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (getIntent().getExtras() != null) {
            mUserName = getIntent().getExtras().getString("username");
            mPhoneNo = getIntent().getExtras().getString("phoneno");
            mRegId=getIntent().getExtras().getString("regId");
            mAuth=getIntent().getExtras().getString("accessToken");
            flag=getIntent().getExtras().getBoolean("ForLogin");
          //  mPin=getIntent().getExtras().getInt("pin");
        }

        mFirstEdt = (EditText) findViewById(R.id.VERIFICATION_code_one);
        mSecondEdt = (EditText) findViewById(R.id.VERIFICATION_code_two);
        mThirdEdt = (EditText) findViewById(R.id.VERIFICATION_code_three);
        mForthEdt = (EditText) findViewById(R.id.VERIFICATION_code_four);
        mResendSmsTxt = (TextView) findViewById(R.id.VERIFICATION_sms_resend_txt);
        mVoiceCallTxt = (TextView) findViewById(R.id.VERIFICATION_voice_call_txt);
        mUserNameTxt = (TextView) findViewById(R.id.VERIFICATION_username_txt);
        mIndicationTxt = (TextView) findViewById(R.id.VERIFICATION_code_sent_txt);
        mPhoneTxt=(TextView)findViewById(R.id.VERIFICATION_phone_txt);


        mUserNameTxt.setText("Hi, " + mUserName + "!");
        mIndicationTxt.setText("verification code sent to " );
        mback=(ImageView) findViewById(R.id.VER_back);
        mPhoneTxt.setText(""+ mPhoneNo);
        Context ctx = getApplicationContext();
        if (ctx != null) {
            // todo: user custom text view instead
            Typeface type = Typeface.createFromAsset(ctx.getAssets(),
                    "BentonSansBook.otf");
            mUserNameTxt.setTypeface(type);
            mIndicationTxt.setTypeface(type);
            mFirstEdt.setTypeface(type);
            mSecondEdt.setTypeface(type);
            mThirdEdt.setTypeface(type);
            mFirstEdt.setTypeface(type);
            mVoiceCallTxt.setTypeface(type);
            mResendSmsTxt.setTypeface(type);
            Typeface type2 = Typeface.createFromAsset(ctx.getAssets(),
                    "BentonSansMedium.otf");
            mPhoneTxt.setTypeface(type2);

        }

        mResendSmsTxt.setOnClickListener(this);
        mVoiceCallTxt.setOnClickListener(this);
        mback.setOnClickListener(this);
        mNetWorkStatus = NetworkUtil.isInternetConnected(getApplicationContext());
        NetworkStateReceiver.registerOnNetworkChangeListener(this);
        mPreference =PreferenceHelper.getInstance(this);

        if(flag)
        {
            updateToLoginUI();
        }

        mFirstEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(mFirstEdt.getText().toString().trim()))
                    mSecondEdt.requestFocus();
            }
        });

        mSecondEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(mSecondEdt.getText().toString().trim()))
                    mThirdEdt.requestFocus();
            }
        });

        mThirdEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(mThirdEdt.getText().toString().trim()))
                    mForthEdt.requestFocus();
            }
        });

        mForthEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!TextUtils.isEmpty(mFirstEdt.getText().toString().trim()) && !TextUtils.isEmpty(mSecondEdt.getText().toString().trim())
                        && !TextUtils.isEmpty(mThirdEdt.getText().toString().trim()) && !TextUtils.isEmpty(mForthEdt.getText().toString().trim())) {
                    mPin = mFirstEdt.getText().toString().trim() + mSecondEdt.getText().toString().trim()
                            + mThirdEdt.getText().toString().trim() + mForthEdt.getText().toString().trim();
                    Log.d(TAG, "Pin No: " + mPin);

                    if (mNetWorkStatus) {
                        mFirstEdt.setText("");
                        mSecondEdt.setText("");
                        mThirdEdt.setText("");
                        mForthEdt.setText("");
                        DialogUtils.showProgressBar();  // todo: this leaks window
                        if (flag)
                            callLoginUserApi(true);
                        else
                            callVerificationApi();
                    } else
                        UiUtils.showSnackbarToast(findViewById(R.id.root_view), Utils.getString(R.string.error_offline_msg));
                }

            }
        });




    }

    private void callVerificationApi() {
        HeldService.getService().verifyUser(mAuth, mRegId, mPin, "", new Callback<VerificationResponse>() {
            @Override
            public void success(VerificationResponse verificationResponse, Response response) {
                mPreference.writePreference(getString(R.string.API_pin), mPin);
                //PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_registration_key), Integer.parseInt(m));
                Log.i("VerificationActivity", "Writting pin and phone no ");

                mPreference.writePreference(getString(R.string.API_phone_no), verificationResponse.getPhone());
                mPreference.writePreference(getString(R.string.API_user_name), verificationResponse.getDisplayName());

                DialogUtils.showProgressBar();
                callLoginUserApi(false);


                if (verificationResponse.isVerified()) {
                    PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_pin), Integer.parseInt(mPin));
                    Log.i("VerificationActivity", "Responce :" + verificationResponse.toString());
                    DialogUtils.showProgressBar();
                    callLoginUserApi(false);
                }
                DialogUtils.stopProgressDialog();
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

    private void callUpdateRegIdApi(String gcmKey) {

        Timber.d("updating gcm key on held server " + gcmKey);
        mPreference = PreferenceHelper.getInstance(this);
        String selfID = mPreference.readPreference(getString(R.string.API_user_regId));
        DialogUtils.showProgressBar();

        HeldService.getService().updateNotificationToken(mPreference.readPreference(getString(R.string.API_session_token)),
                selfID, "notification_token", gcmKey, "", new Callback<ProfilPicUpdateResponse>() {

                    @Override
                    public void success(ProfilPicUpdateResponse picUpdateResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                       // launchComposeScreen();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Some Problem Occurred");

                        //launchComposeScreen();
                    }
                });

    }


    private void setupGCM() {
        GCMControlManager gcmControlManager = new GCMControlManager(this);
        gcmControlManager.setupGCM(new GcmCallback() {

            @Override
            public void onRegister(String gcmKey) {
                callUpdateRegIdApi(gcmKey);


            }

        });
    }


    private void callLoginUserApi(final boolean gotoFeed) {
        Log.i("VerificationActivity","In callLoginUserApi()");
        DialogUtils.showProgressBar();
        HeldService.getService().loginUser(mPhoneNo, mPin, "", new Callback<LoginUserResponse>() {
            @Override
            public void success(LoginUserResponse loginUserResponse, Response response) {
                DialogUtils.stopProgressDialog();
                mPreference.writePreference(getString(R.string.API_pin), mPin);
                mPreference.writePreference(getString(R.string.API_user_name), loginUserResponse.getUser().getDisplayName());
                mPreference.writePreference(getString(R.string.API_session_token), loginUserResponse.getSessionToken());
                mPreference.writePreference(getString(R.string.API_user_regId), loginUserResponse.getUser().getRid());
                setupGCM();

                    mPreference.writePreference(getString(R.string.is_first_post), false);
                    if(gotoFeed)
                        launchFeedScreen();
                    else
                        launchComposeScreen();

               /* if (loginUserResponse.isLogin()) {
                    PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_session_token), loginUserResponse.getSession_token());
                  // PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_registration_key), loginUserResponse.getRid());
                 //   mSessonToken=loginUserResponse.getSession_token();
                //    mNewRegId=loginUserResponse.getRid();
                    Log.i("VerificatonActivity ","Session Tokn"+loginUserResponse.getSession_token());
                  //  Log.i("VerificatonActivity ","New Id"+loginUserResponse.getRid());
                   // callUpdateRegIdApi();
                }*/


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

    private void launchComposeScreen() {
        Intent intent = new Intent(VerificationActivity.this, PostActivity.class);
       // intent.putExtra("session",mSessonToken);
       // intent.putExtra("regId",mNewRegId);
        startActivity(intent);
        finish();
    }

    private void launchFeedScreen() {
        Intent intent = new Intent(VerificationActivity.this, FeedActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.VERIFICATION_sms_resend_txt:
                if (mNetWorkStatus) {
                    if (validatePhoneNo()) {
                        DialogUtils.showProgressBar();
                        if(flag)
                            callLoginResendSmsApi();
                        else
                            callResendSmsApi();
                    }
                } else
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), Utils.getString(R.string.error_offline_msg));
                break;
            case R.id.VERIFICATION_voice_call_txt:
                if (mNetWorkStatus) {
                    if (validatePhoneNo()) {
                        DialogUtils.showProgressBar();
                        if(flag)
                            callLoginCallApi();
                        else
                            callVoiceCallApi();
                    }
                } else {
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), Utils.getString(R.string.error_offline_msg));
                }
                break;
            case R.id.VER_back:
                onBackPressed();
        }
    }

    private void callVoiceCallApi() {
        HeldService.getService().voiceCall(mPreference.readPreference(getString(R.string.API_session_token)),mRegId,new Callback<VoiceCallResponse>() {
                    @Override
                    public void success(VoiceCallResponse voiceCallResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        Timber.d(TAG, "Voice Call Success");
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

    private boolean validatePhoneNo() {
        if (!TextUtils.isEmpty(mPhoneNo)) {
            return true;
        } else return false;
    }

    private void callResendSmsApi() {
        HeldService.getService().resendSms( mPreference.readPreference(getString(R.string.API_session_token)),mRegId,new Callback<CreateUserResponse>() {
            @Override
            public void success(CreateUserResponse createUserResponse, Response response) {
                DialogUtils.stopProgressDialog();
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

    @Override
    public void onNetworkStatusChanged(boolean isEnabled) {
        mNetWorkStatus = isEnabled;
    }

    public void callLoginResendSmsApi(){
        HeldService.getService().loginSessionSendPinSmsApi(mPhoneNo, "",
                new Callback<CreateUserResponse>() {
                    @Override
                    public void success(CreateUserResponse createUserResponse, Response response) {


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
    public void callLoginCallApi(){
        HeldService.getService().loginSessionSendPinCallApi(mPhoneNo, "",
                new Callback<VoiceCallResponse>() {
                    @Override
                    public void success(VoiceCallResponse voiceCallResponse, Response response) {

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }
    public void launchRegistrationActivity(){
        Intent intent = new Intent(VerificationActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        launchRegistrationActivity();
        this.finish();
    }
    public void updateToLoginUI(){
        mUserNameTxt.setVisibility(View.GONE);

    }
}
