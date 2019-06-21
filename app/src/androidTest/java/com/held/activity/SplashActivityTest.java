package com.held.activity;

/**
 * Created by MAHESH on 9/7/2015.
 */
import com.held.activity.SplashActivity;
import android.test.ApplicationTestCase;
import android.content.Intent;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.widget.Button;

public class SplashActivityTest extends ActivityInstrumentationTestCase2 <SplashActivity> {
    private SplashActivity mActivity;
    public SplashActivityTest() {
        super(SplashActivity.class);
    }
    @Override
    public void setUp()throws Exception
    {
        super.setUp();

        Intent intent = new Intent(getInstrumentation().getTargetContext(),SplashActivity.class);
        intent.putExtra("testRunner", true);
        setActivityIntent(null);
        setActivityInitialTouchMode(false);
        mActivity = getActivity();
    }

    public void testStartSecondActivity()
    {
        ActivityMonitor monitor = getInstrumentation().addMonitor(SplashActivity.class.getName(), null, false);
        final Button buttonStart = (Button) mActivity.findViewById(R.id.startBtn);
        assertNotNull(buttonStart);
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                buttonStart.performClick();
            }
        });

        SplashActivity startedActivity = (SplashActivity) monitor.waitForActivityWithTimeout(10000);
        assertNotNull(startedActivity);
       // assertEquals("com.held.activity.SplashActivity.class",startedActivity.getClass().toString());
        assertSame(com.held.activity.SplashActivity.class,startedActivity.getClass());
        this.sendKeys(KeyEvent.KEYCODE_BACK);
        monitor = getInstrumentation().addMonitor(SplashActivity.class.getName(), null, false);

    }
}
