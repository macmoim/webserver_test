package com.macmoim.pang.data;

public class FoodCommentItem {
    private int postId;
    private String postUserId, commentUserId, timeStamp, comment;

    public FoodCommentItem() {
    }

    public FoodCommentItem(int postId, String postUserId, String commentUserId, String comment, String timeStamp) {
        super();
        this.postId = postId;
        this.postUserId = postUserId;
        this.commentUserId = commentUserId;
        this.timeStamp = timeStamp;
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
}
