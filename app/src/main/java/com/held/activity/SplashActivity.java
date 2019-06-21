package com.held.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.held.gcm.GCMControlManager;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.LoginUserResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.splunk.mint.Mint;
import com.splunk.mint.MintLog;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;
import timber.log.Timber.DebugTree;


public class SplashActivity extends ParentActivity implements View.OnClickListener {

    private Button mGetStartedBtn;
    private TextView mSigninTxt,mHeadLinetxt,mPolicy,mHave;
    private PreferenceHelper mPrefernce;
    private String mphoneNo,mPin;
    private final String TAG = "SplashActivity";
    int postCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Startig splash activity");
        initializeSplunk();
        initializeTimber();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mGetStartedBtn = (Button) findViewById(R.id.startBtn);
        mSigninTxt=(TextView)findViewById(R.id.signinTxt);
        mHeadLinetxt=(TextView)findViewById(R.id.text1);
        mPolicy=(TextView)findViewById(R.id.text3);
        mHave=(TextView)findViewById(R.id.text2);
        mGetStartedBtn.setOnClickListener(this);
        mSigninTxt.setOnClickListener(this);
        mSigninTxt.setPaintFlags(mSigninTxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Context ctx = getApplicationContext();
        if (ctx != null) {
            Typeface type = Typeface.createFromAsset(ctx.getAssets(),
                    "BentonSansBook.otf");
            mGetStartedBtn.setTypeface(type);
            mSigninTxt.setTypeface(type);
            mHeadLinetxt.setTypeface(type);
            mPolicy.setTypeface(type);
            mHave.setTypeface(type);


        }
        mPrefernce=PreferenceHelper.getInstance(getApplicationContext());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mphoneNo=mPrefernce.readPreference(getString(R.string.API_phone_no));
        mPin=mPrefernce.readPreference(getString(R.string.API_pin));
        Log.d(TAG, "phone number: " + mphoneNo);
        Log.d(TAG, "pin: "+ mPin);
        if (mphoneNo!=null && mPin!=null &&
                mphoneNo != ""  && mPin != "") {
            if (getNetworkStatus()) {
                DialogUtils.showDarkProgressBar();
                callLoginApi();
            } else {
                UiUtils.showSnackbarToast(findViewById(R.id.root_view), "You are not connected to internet");
            }
        } else if (mphoneNo!=null &&  mPin==null)
        { launchVerificationActivity();}
        else if(mphoneNo==null&&mPin==null)
        {
                return;
        }

    }

    private void initializeSplunk(){
        Mint.initAndStartSession(SplashActivity.this, "332b7321");
    }

    private void initializeTimber(){

        if (BuildConfig.DEBUG) {
            // do something for a debug build
            Timber.plant(new DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {

            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }
            if (t != null) {
                if (priority == Log.ERROR) {
                    MintLog.e(tag, message);
                }
            }

        }

    }

    private void callLoginApi() {
        HeldService.getService().loginUser(mPrefernce.readPreference(getString(R.string.API_phone_no)),
                mPrefernce.readPreference(getString(R.string.API_pin)) + "","", new Callback<LoginUserResponse>() {
            @Override
            public void success(LoginUserResponse loginUserResponse, Response response) {
                DialogUtils.stopProgressDialog();
                Log.i("@@REG KEY in Splash", loginUserResponse.getUser().getRid());
                mPrefernce.writePreference(getString(R.string.API_session_token), loginUserResponse.getSessionToken());
                mPrefernce.writePreference(getString(R.string.API_user_regId), loginUserResponse.getUser().getRid());
                if(mPrefernce.readPreference(getString(R.string.is_first_post),false)==true)
                    launchPostActivity();
                else
                    launchFeedActivity();
//                checkPostCount();
                /*if (loginUserResponse.isLogin()) {
                    PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_session_token), loginUserResponse.getSession_token());
                    callUpdateRegIdApi();
                }*/
            }

            @Override
            public void failure(RetrofitError error) {
                DialogUtils.stopProgressDialog();
                if (error != null && error.getResponse() != null) {

                    Response response = error.getResponse();
                    if (response.getStatus() == 401) {
                        Timber.d("No valid session found");
                    } else {
                        String body = response.getBody().toString();

                        if (!TextUtils.isEmpty(body)) {
                            UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Some Problem Occurred");
                        }
                    }
                }

            }
        });
    }

    private void launchVerificationActivity() {
        Intent intent = new Intent(SplashActivity.this, VerificationActivity.class);
        intent.putExtra("username", PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_name)));
        intent.putExtra("phoneno", PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_phone_no)));
        startActivity(intent);
        finish();
    }

    private void launchPostActivity() {
        Intent intent = new Intent(SplashActivity.this, PostActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchFeedActivity() {
        Intent intent = new Intent(SplashActivity.this, FeedActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startBtn:
                Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
                startActivity(intent);

                break;
            case R.id.signinTxt :
                Intent intent2 = new Intent(SplashActivity.this, RegistrationActivity.class);
                intent2.putExtra("ForLogin",true);
                startActivity(intent2);
                break;
        }
    }



    public void checkPostCount()
    {

        PreferenceHelper mPrefernce=PreferenceHelper.getInstance(this);
        HeldService.getService().searchUser(mPrefernce.readPreference(getString(R.string.API_session_token)),
                mPrefernce.readPreference(getString(R.string.API_user_regId)), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        Log.i("PostFragment", "@@Image Url" + searchUserResponse.getUser().getProfilePic());
                        //TODO Check Post count
                        postCount = Integer.parseInt(searchUserResponse.getUser().getPostCount());
                        if(postCount==0)
                            launchPostActivity();
                        else
                            launchFeedActivity();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

    }
}
