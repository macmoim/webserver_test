package com.macmoim.pang.pangeditor;

import android.net.Uri;

/**
 * Created by P14983 on 2015-10-02.
 */
public class FrontPageItem extends PageItem {

    private String userId;
    private String userName;
    private String star;
    private String like;
    private String title;
    private String profileImgUrl;
    private String userEmail;
    private String commentSum;
    private String pageSum;
    private String uploadDate;

    public FrontPageItem() {
    }

    public FrontPageItem(String contents, Uri imageUri, String userId, String userName, String star, String like, String title, String profileImgUrl, String userEmail, String commentSum, String pageSum, String uploadDate) {
        super(contents, imageUri);
        this.userId = userId;
        this.userName = userName;
        this.star = star;
        this.like = like;
        this.title = title;
        this.profileImgUrl = profileImgUrl;
        this.userEmail = userEmail;
        this.commentSum = commentSum;
        this.pageSum = pageSum;
        this.uploadDate = uploadDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCommentSum() {
        return commentSum;
    }

    public void setCommentSum(String commentSum) {
        this.commentSum = commentSum;
    }

    public String getPageSum() {
        return pageSum;
    }

    public void setPageSum(String pageSum) {
        this.pageSum = pageSum;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}
