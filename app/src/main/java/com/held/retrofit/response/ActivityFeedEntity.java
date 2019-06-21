package com.held.retrofit.response;

/**
 * Created by admin on 17-Feb-16.
 */
public class ActivityFeedEntity {

    // Entity for download request
    private String post_image;
    private boolean declined,canDownload;
    private String owner_display_name;
    private String tag;
    private String rid,imageUri;
    private String owner_pic;
    private String views;
    private String displayName;
    private String profilePic;


    //Entity for post:hold
    private String date;
    private long held;
    private User user;
    private PostData post;

    // Entity for friend:message
    private String text;
    private User toUser;
    private User fromUser;


    public String  getText(){
        return text;
    }

    public long getHeld(){return held;}

    public String getDate(){return date;}

    public User getUser(){return  user;}

    public User getFromUser(){return fromUser;}

    public User getToUser(){return toUser;}

    public PostData getPostData(){return post;}

    public String getProfilePic(){ return profilePic; };

    public boolean getCanDownload() {
        return canDownload;
    }

    public PostData getPost() {
        return post;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getPost_image() {
        return post_image;
    }

    public boolean isDeclined() {
        return declined;
    }

    public String getOwner_display_name() {
        return owner_display_name;
    }

    public String getDisplayName(){return displayName;}

    public String getTag() {
        return tag;
    }

    public String getRid() {
        return rid;
    }

    public String getOwner_pic() {
        return owner_pic;
    }

    public String getViews(){ return views; }
}
