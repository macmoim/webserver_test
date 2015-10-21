package com.macmoim.pang.pangeditor;

import android.net.Uri;

/**
 * Created by P14983 on 2015-10-02.
 */
public class PageItem {
    private String contents;
    private Uri imageUri;
    private boolean isThumbImg;

    public PageItem() {
    }

    public PageItem(String contents, Uri imageUri) {
        super();
        this.contents = contents;
        this.imageUri = imageUri;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public boolean getIsThumbImg() {
        return isThumbImg;
    }

    public void setIsThumbImg(boolean isThumbImg) {
        this.isThumbImg = isThumbImg;
    }
}
