package com.macmoim.pang.data;

public class FoodCommentItem {
    private int postId;
    private String postUserId, commentUserId, commentUserName, timeStamp, comment, profileImgUrl;

    public FoodCommentItem() {
    }

    public FoodCommentItem(int postId, String postUserId, String commentUserId, String commentUserName, String comment, String timeStamp, String profileImageUrl) {
        super();
        this.postId = postId;
        this.postUserId = postUserId;
        this.commentUserId = commentUserId;
        this.timeStamp = timeStamp;
        this.profileImgUrl = profileImageUrl;
        this.commentUserName = commentUserName;
    }


    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getPostUserId() {
        return postUserId;
    }

    public void setPostUserId(String postUserId) {
        this.postUserId = postUserId;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getCommentUserName() {
        return commentUserName;
    }

    public void setCommentUserName(String commentUserName) {
        this.commentUserName = commentUserName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        if (profileImgUrl == null || "null".equals(profileImgUrl)) {
            this.profileImgUrl = null;
        } else {
            this.profileImgUrl = profileImgUrl;
        }
    }
}
