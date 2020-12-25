package com.rj.core.vk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.rj.core.vk.requests.VKCommand;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;
import com.vk.api.sdk.exceptions.VKApiExecutionException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.rj.core.utils.UnityUtil.callUnity;


/**
 * 包名:com.rj.googlesdk.vk
 */
public class VkLoginActivity extends Activity {

    private static String TAG = "VkLoginActivity";

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (VK.isLoggedIn()) {
            //已经登录过了
            Log.d(TAG, "onLoginFailed: 已经登录过了 " );
            finish();
            return;
        }
        List<VKScope> list = new ArrayList<>();
        list.add(VKScope.WALL);
        list.add(VKScope.PHOTOS);
        VK.login(this, list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKAuthCallback vkAuthCallback = new VKAuthCallback() {

            @Override
            public void onLoginFailed(int i) {
                Log.d(TAG, "onLoginFailed: 登录失败 " +i);
                finish();
            }

            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                onLogged(vkAccessToken);

            }
        };
        if (!VK.onActivityResult(requestCode, resultCode, data, vkAuthCallback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * 已登录
     */
    private void onLogged(@NotNull VKAccessToken vkAccessToken) {
        Log.d(TAG, "onLoginFailed: 登录成功 "+vkAccessToken.getAccessToken() + "," + vkAccessToken.getSecret() + "," + vkAccessToken.getUserId() );
        callUnity("androidCallBack", "VK_LoginSuccessCallback", vkAccessToken.getAccessToken() + "," + vkAccessToken.getSecret() + "," + vkAccessToken.getUserId());
        HashMap<String, String> map= new HashMap<>();
        map.put("user_ids", String.valueOf(vkAccessToken.getUserId()));
        map.put("fields","photo_200");
        VK.execute(new VKCommand("users.get", map), new MyVKApiCallback());
        finish();
    }

    private static class MyVKApiCallback implements VKApiCallback<String> {

        @Override
        public void success(String o) {
            Log.d(TAG, "success: 登录成功 用户信息 -> " + o );
            callUnity("androidCallBack", "VK_LoginSuccessCallback_UserInfo", o );
        }

        @Override
        public void fail(@NotNull VKApiExecutionException e) {
            Log.d(TAG, "success: 登录成功 获取用户信息失败 -> " + e.toString() );
        }
    }

}
