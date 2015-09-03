package com.macmoim.pang.data;

import com.macmoim.pang.util.Util;

public class FoodItem {
    private int id;
    private String name, image, timeStamp, userId, userName;

    public FoodItem() {
    }

    public FoodItem(int id, String name, String userId, String userName, String image, String timeStamp) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getImge() {
        return image;
    }

    public void setImge(String image) {
        this.image = Util.splitFilename(image);
    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
