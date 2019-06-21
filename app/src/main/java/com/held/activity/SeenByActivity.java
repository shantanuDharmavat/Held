package com.held.activity;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.adapters.SeenByAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.Engager;
import com.held.retrofit.response.EngagersResponse;
import com.held.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SeenByActivity extends ParentActivity {

    private ImageView mChat, mCamera, mNotification;
    private EditText mSearch_edt;
    private TextView mTitle,mInvite,chatNotif;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SeenByAdapter mSeenByAdapter;
   // private List<String []> mList = new ArrayList<>();
    private RecyclerView mSeenRecyclerView;
    private boolean isLastPage,isLoading;
    private LinearLayoutManager mLayoutManager;
    private String username="maheshTest2",img="/user_thumbnails/maheshTest2_1443592731143.jpg";
    private PreferenceHelper mPreference;
    private ArrayList<String[]> mList;
    private String string1[]={"maheshTest2","/user_thumbnails/maheshTest2_1443592731143.jpg","Add as Friend"};
    private String mPostId;
    private int mlimit=10;
    private List<Engager> mEngagersList=new ArrayList<Engager>();
    View statusBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen_by);
        statusBar=(View)findViewById(R.id.statusBarView);
        Window w = getWindow();

        chatNotif = (TextView) findViewById(R.id.tvChatIndicator);
        chatNotif.setVisibility(View.GONE);
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
       // mLayoutManager = new LinearLayoutManager(this);
        mChat=(ImageView)findViewById(R.id.toolbar_chat_img);
        mNotification=(ImageView)findViewById(R.id.toolbar_notification_img);
        mCamera=(ImageView)findViewById(R.id.toolbar_post_img);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);
        mInvite=(TextView)findViewById(R.id.toolbar_invite_txt);
        mInvite.setVisibility(View.GONE);
        mNotification.setVisibility(View.GONE);
        mCamera.setVisibility(View.GONE);
        mSearch_edt.setVisibility(View.GONE);

        //mChat.setPadding(50,20,100,20);
        mChat.setImageResource(R.drawable.back);
        mChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mChat.setVisibility(View.VISIBLE);

        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);
        mTitle.setText("Seen By");
        //setToolbar();
        mPostId=getIntent().getExtras().getString("post_id");
        mPreference=PreferenceHelper.getInstance(this);
        mSeenRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSeenRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        callSeenByApi();

        Timber.d("setting seen by adapter");
        mSeenByAdapter = new SeenByAdapter(this,mEngagersList);
        mSeenByAdapter.setEngagersList(mEngagersList);
        mSeenRecyclerView.setAdapter(mSeenByAdapter);

       // removeCurrentUser();
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mEngagersList.clear();
                mSeenByAdapter.notifyDataSetChanged();
                callSeenByApi();
            }
        });
/*
        mSeenRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!isLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !isLoading) {
return;
                }
            }
        });
*/

    }
    public void callSeenByApi()
    {
        try {
            HeldService.getService().getPostEngagers(mPreference.readPreference(getString(R.string.API_session_token)), mPostId, mlimit, false,
                    new Callback<EngagersResponse>() {
                        @Override
                        public void success(EngagersResponse engagersResponse, Response response) {
                            mEngagersList.addAll(engagersResponse.getObjects());
                            removeCurrentUser();
                            mSeenByAdapter.setEngagersList(mEngagersList);
                            mSeenByAdapter.notifyDataSetChanged();
                            //  Timber.d("Print SeenBy List\n"+mEngagersList.toString());
                            if (mSwipeRefreshLayout.isRefreshing())
                                mSwipeRefreshLayout.setRefreshing(false);

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            if (mSwipeRefreshLayout.isRefreshing())
                                mSwipeRefreshLayout.setRefreshing(false);

                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removeCurrentUser(){
        String currentUser=mPreference.readPreference(getString(R.string.API_user_name));
        int sizeofList=mEngagersList.size();

       for(int i=0;i<sizeofList;i++)
       {
           if(mEngagersList.get(i).getUser().getDisplayName().equals(currentUser))
           {
               mEngagersList.remove(i);
               Timber.i("User Removed");
               break;
           }
       }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
