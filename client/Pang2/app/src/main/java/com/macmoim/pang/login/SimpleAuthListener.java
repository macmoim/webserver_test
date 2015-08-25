package com.macmoim.pang.login;

/**
 * Created by P11872 on 2015-08-16.
 */
public abstract class SimpleAuthListener implements Auth.OnAuthListener{

    @Override
    public void onLoginCancel() {}

    @Override
    public void onLoginError(String message) {}

    @Override
    public void onLoginSuccess(SocialProfile profile) {}

    @Override
    public void onRevoke() {}
}
