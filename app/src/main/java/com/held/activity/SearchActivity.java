package com.held.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.adapters.SeenByAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.Engager;
import com.held.retrofit.response.User;
import com.held.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchActivity extends ParentActivity {

    private ImageView mSearchImg;
    private TextView mSearchText,mCancle;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SeenByAdapter mSeenByAdapter;
    private RecyclerView mSearchRecyclerView;
    private Toolbar mHeld_toolbar;
    private LinearLayoutManager mLayoutManager;
    private PreferenceHelper mPreference;
    private String mUserName;
    private Engager searchResult=new Engager();
    private List<Engager> mSearchResultList=new ArrayList<>();
    private Toolbar mSearchToolbar;
    View statusBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        statusBar=(View)findViewById(R.id.statusBarView);
        Window w = getWindow();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            statusBar.setVisibility(View.VISIBLE);

        }else {
            statusBar.setVisibility(View.GONE);
        }
        mSearchToolbar=(Toolbar)findViewById(R.id.search_toolbar);
        mSearchImg=(ImageView) findViewById(R.id.search_img);
        mSearchText=(TextView)findViewById(R.id.search_txt);
        mSearchText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(mSearchText, InputMethodManager.SHOW_IMPLICIT);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        mCancle=(TextView)findViewById(R.id.cancle_txt);
        setTypeFace(mSearchText,"medium");
        setTypeFace(mCancle,"medium");
        hideChatCount();
        hideNotifCount();


        //mUserName
        //mSearchText.setText(mUserName);
        mPreference=PreferenceHelper.getInstance(this);
        mSearchRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSeenByAdapter = new SeenByAdapter(this,mSearchResultList);
        mSearchRecyclerView.setAdapter(mSeenByAdapter);
        mSeenByAdapter.notifyDataSetChanged();
        //callSearchByNameApi();


        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callSearchByNameApi();
                return;
            }
        });
        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mSearchText.setCursorVisible(true);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mUserName = mSearchText.getText().toString();
                    callSearchByNameApi();
                    handled = true;
                }
                return handled;
            }
        });

    }
    public void callSearchByNameApi(){
        HeldService.getService().searchByName(mPreference.readPreference(getString(R.string.API_session_token)),
                mUserName, new Callback<Engager>() {
                    @Override
                    public void success(Engager engager, Response response) {
                        searchResult=engager;
                        /*TODO for multiple object
                        mSearchResultList=searchByNameResponce.getObjects();

                        */
                        User u = engager.getUser();
                        mSearchResultList.clear();
                        mSearchResultList.add(searchResult);
                        mSeenByAdapter.setEngagersList(mSearchResultList);
                        mSeenByAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }

    public void setTypeFace(TextView tv,String type){
        Typeface medium = Typeface.createFromAsset(this.getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(this.getAssets(), "BentonSansBook.otf");
        if(type=="book"){
            tv.setTypeface(book);

        }else {
            tv.setTypeface(medium);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onLeftSwipe() {
        // Do something
        finish();

    }


}
