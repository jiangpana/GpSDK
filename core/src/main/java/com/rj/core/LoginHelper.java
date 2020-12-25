package com.rj.core;

import android.app.Activity;
import android.content.Intent;

import com.rj.core.utils.UnityUtil;
import com.rj.core.vk.ui.LoginTransparentActivity;


/**
 * 包名:com.rj.googlesdk.auth
 */
public class LoginHelper {
    private String TAG = "LoginHelper";
    private Activity mActivity= UnityUtil.getActivity();


    public static boolean isLogin = true;
    public static String defaultTokenId ;
    /**
     * 登录
     */
    public void login(String id){
        isLogin=true;
        defaultTokenId=id;
        mActivity.startActivity(new Intent(mActivity, LoginTransparentActivity.class));
    }
    /**
     * 退出登录
     */
    public void logout(String id){
        defaultTokenId=id;
        isLogin=false;
        mActivity.startActivity(new Intent(mActivity, LoginTransparentActivity.class));
    }

}
