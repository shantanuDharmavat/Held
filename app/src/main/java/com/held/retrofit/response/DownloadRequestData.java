package com.held.retrofit.response;


import com.held.activity.PostActivity;

public class DownloadRequestData {

    private String date;
    private String post_image;
    private boolean declined,canDownload;
    private String owner_display_name;
    private String tag;
    private String rid,imageUri;
    private String owner_pic;
    private User user;
    private PostData post;

    public boolean getCanDownload() {
        return canDownload;
    }

    public PostData getPost() {
        return post;
    }

    public String getImageUri() {
        return imageUri;
    }

    public User getUser() {
        return user;
    }

    public String getDate() {
        return date;
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

    public String getTag() {
        return tag;
    }

    public String getRid() {
        return rid;
    }

    public String getOwner_pic() {
        return owner_pic;
    }
}
