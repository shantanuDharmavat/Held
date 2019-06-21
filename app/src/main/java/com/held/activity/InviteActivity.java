package com.held.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.held.adapters.InvitedAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.InviteError;
import com.held.retrofit.response.InviteListResponse;
import com.held.retrofit.response.InviteResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.CustomContact;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by swapnil on 30/3/16.
 */
public class InviteActivity  extends ParentActivity{

    View statusBar;
    private static final int PICK_CONTACT =1 ;
    int  max_pic_contact = 5;
    private InvitedAdapter mInvitedAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    List<InviteResponse> mInvitedList;
    private PreferenceHelper mPreference;
    private Button mInviteButton;
    private int mlimit=10;
    private TextView mInviteCount;
    private long mStart;
    private boolean mIsLastPage = false, mIsLoading = false;
    CustomContact personContact=new CustomContact();
    ArrayList<CustomContact> inviteContactList=new ArrayList<>(max_pic_contact);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

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

        mLayoutManager = new LinearLayoutManager(this);


        mPreference=PreferenceHelper.getInstance(this);
        mStart = System.currentTimeMillis();
        mInviteButton = (Button)findViewById(R.id.bt_invite);
        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mInviteButton.setTypeface(medium);

        mInvitedList = new ArrayList<InviteResponse>();
        mInvitedAdapter = new InvitedAdapter(this, mInvitedList);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mInvitedAdapter);
        mInviteCount = (TextView)findViewById(R.id.tvInviteCount);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                //Timber.i("item Count :"+totalItemCoount);
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                //Timber.i("Last Item :"+lastVisibleItemPosition);
                if (!mIsLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !mIsLoading) {
                    callInvitesApi();
                    mInvitedAdapter.notifyDataSetChanged();
                }
            }
        });
        mInviteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                inviteNewUser();

            }
        });
        callAvailableInvites();



    }



    public void callAvailableInvites(){


        DialogUtils.resetDialog(this);
        DialogUtils.showDarkProgressBar();
        String selfID = PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_regId));
        HeldService.getService().searchUser(mPreference.readPreference(getString(R.string.API_session_token)),
                selfID, new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                        int invites = searchUserResponse.getUser().getAvailableInvites();
                        mInviteCount.setText("" + invites + " Invites left");
                        callInvitesApi();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();

                        UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), "Error in retrieving invites list");

                    }
                });
    }

    public void callInvitesApi() {

        HeldService.getService().getInvitationList(mPreference.readPreference(getString(R.string.API_session_token)),
                mStart, mlimit,
                new Callback<InviteListResponse>() {

                    @Override
                    public void success(InviteListResponse inviteListResponse, Response response) {

                        DialogUtils.stopProgressDialog();
                        mInvitedList.addAll(inviteListResponse.getObjects());
                        mInvitedAdapter.setInviteList(mInvitedList);
                        mInvitedAdapter.notifyDataSetChanged();
                        mIsLastPage = inviteListResponse.isLastPage();
                        mStart = inviteListResponse.getNext();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        UiUtils.showSnackbarToast(getWindow().getDecorView().getRootView(), "Error in retrieving invites list");

                    }

                });
    }

    void inviteNewUser(){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }


    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        String phn_no="",name;
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id=c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone=c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            phn_no = phones.getString(phones.getColumnIndex("data1"));
                            phn_no=phn_no.replaceAll(" ","");
                        }
                        name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME));
                        Timber.i("Contact Picked :" + name + ":" + phn_no);
                        personContact.setName(name);
                        personContact.setPhone_no(phn_no);
                        callInviteUser(personContact.getPhone_no());


                    }
                }
        }
    }

    void callInviteUser(String phone){

        if(phone.startsWith("+")){
            phone = phone.substring(1);
            Timber.d("stripping + from phone number");
        }

        HeldService.getService().sendInvitation(mPreference.readPreference(getString(R.string.API_session_token)), phone, "",
                new Callback<InviteResponse>() {
                    @Override
                    public void success(InviteResponse inviteResponse, Response response) {
                        Timber.i("Invittion Sent To :" + inviteResponse.getPhone() + ":Successfully..");
                        Toast.makeText(InviteActivity.this, "Invite sent successfully.", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (error.getResponse() != null) {

                            InviteError err = (InviteError) error.getBodyAs(InviteError.class);
                            String errorMsg = err.getError();
                            Toast.makeText(InviteActivity.this, errorMsg, Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }


}
