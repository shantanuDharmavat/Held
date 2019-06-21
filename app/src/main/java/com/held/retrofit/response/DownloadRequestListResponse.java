package com.held.retrofit.response;


import java.util.List;

public class DownloadRequestListResponse {

    private List<DownloadRequestData> objects;
    private boolean lastPage;
    private long nextPageStart;
    private String imageUri;

    public String getImageUri() {
        return imageUri;
    }

    public List<DownloadRequestData> getObjects() {
        return objects;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public long getNextPageStart() {
        return nextPageStart;
    }
}
