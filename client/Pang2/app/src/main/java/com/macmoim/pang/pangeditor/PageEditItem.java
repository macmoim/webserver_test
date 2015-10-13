package com.macmoim.pang.pangeditor;

import android.net.Uri;

import java.io.File;

/**
 * Created by P14983 on 2015-10-02.
 */
public class PageEditItem extends PageItem {

    private String contents;
    private Uri imageUri;
    private File file;

    public PageEditItem() {
    }

    public PageEditItem(String contents, Uri imageUri) {
        super(contents, imageUri);
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
