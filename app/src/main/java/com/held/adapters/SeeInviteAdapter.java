package com.held.adapters;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.InviteResponse;
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
 * Created by MAHESH on 11/21/2015.
 */
public class SeeInviteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    List<InviteResponse> inviteList=new ArrayList<>();
    ParentActivity mActivity;
    ArrayList<CustomContact>contactListFromDevice=new ArrayList<>();
    private PreferenceHelper mPreference;
    public SeeInviteAdapter(ParentActivity activity, List<InviteResponse> inviteList){
        mActivity = activity;
        this.inviteList=inviteList;
        fetchContacts();
        mPreference=PreferenceHelper.getInstance(mActivity);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_item_layout, parent, false);
        return new SeeInviteHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        SeeInviteHolder viewHolder = (SeeInviteHolder) holder;

        viewHolder.text.setText(inviteList.get(position).getPhone());

//        findContact(inviteList.get(position).getPhone(), viewHolder.text);

        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActivity.getNetworkStatus()) {
                    DialogUtils.showProgressBar();
                    callDeleteInvitation(inviteList.get(position).getRid(),position);
                } else {
                    UiUtils.showSnackbarToast(mActivity.findViewById(R.id.root_view), "You are not connected to internet.");
                }
            }
        });
        Typeface medium = Typeface.createFromAsset(mActivity.getAssets(), "BentonSansMedium.otf");
        viewHolder.text.setTypeface(medium);
        viewHolder.deleteBtn.setTypeface(medium);

    }

    @Override
    public int getItemCount() {
        return inviteList.size();
    }
    public void setInviteList(List<InviteResponse> minviteList){
        inviteList=new ArrayList<>(minviteList);
        notifyDataSetChanged();
    }


    class SeeInviteHolder extends RecyclerView.ViewHolder {
        TextView text;
        Button deleteBtn;

        SeeInviteHolder(View itemView){
            super(itemView);
            text=(TextView)itemView.findViewById(R.id.contact_name);
            deleteBtn=(Button)itemView.findViewById(R.id.deleteBtn);
        }
    }

    public void findContact(String number,TextView mContactName){
        CharSequence cn=number;
        Timber.i("Inside Method");
        for(int i=0;i<contactListFromDevice.size();i++){
            String no=contactListFromDevice.get(i).getPhone_no();
            if(no.contains(cn)){
                Timber.i("Match Found");

            }
        }
    }


    public void fetchContacts(){

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

        CustomContact contact=new CustomContact();


        String _ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String DISPLAY_NAME =  Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Cursor cursor = mActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=?", new String[] { "1" },
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

        System.out.println("contact size.." + cursor.getCount());

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(NUMBER));
                phoneNumber=phoneNumber.replaceAll(" ","");
                contact.setPhone_no(phoneNumber);
                contact.setName(name);
                contactListFromDevice.add(contact);
                System.out.println(" name : "+name);
                System.out.println(" number : "+phoneNumber);

            }
        }
    }
    private void callDeleteInvitation(final String inviteId, final int position){
        HeldService.getService().deleteInvite(mPreference.readPreference(mActivity.getString(R.string.API_session_token)),
                inviteId, new Callback<InviteResponse>() {
                    @Override
                    public void success(InviteResponse inviteResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        inviteList.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
    }
}
