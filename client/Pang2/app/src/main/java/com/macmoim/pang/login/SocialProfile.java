package com.macmoim.pang.login;

/**
 * Created by P11872 on 2015-08-16.
 */
public class SocialProfile {

    public static final String FACEBOOK = "facebook";
    public static final String KAKAO = "kakao";
    public static final String GOOGLE = "google";

    String id;
    String name;
    String email;
    String image;
    String cover;
    String network;

    public String getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getCover() {
        return cover;
    }

    public String getEmail() {
        return email;
    }

    public String getNetwork() {
        return network;
    }
}
