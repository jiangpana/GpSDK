package com.rj.core.vk;

import android.app.Activity;
import android.content.Intent;


import com.rj.core.utils.UnityUtil;
import com.vk.api.sdk.VK;

/**
 * 包名:com.rj.googlesdk.vk
 */
public class VkLoginHelper {

    private Activity mActivity= UnityUtil.getActivity();
    public static String userId;

    public void login(){
        mActivity.startActivity(new Intent(mActivity, VkLoginActivity.class));
    }

    public void logout(){
        VK.logout();
    }
}
