package com.rj.core.huawei.ad;

import android.app.Activity;
import android.os.Handler;

import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.reward.Reward;
import com.huawei.hms.ads.reward.RewardAd;
import com.huawei.hms.ads.reward.RewardAdLoadListener;
import com.huawei.hms.ads.reward.RewardAdStatusListener;
import com.rj.core.utils.UnityUtil;

/**
 * 包名:com.rj.hwsdk.ad
 */

public class RewardAdHelper {

    private String TAG = "RewardAdHelper";
    private Activity mActivity= UnityUtil.getActivity();
    private RewardAd rewardAd ;


    private boolean isRewardAdFailure=true;
    private void loadRewardAd() {
        if (rewardAd == null) {
            rewardAd = new RewardAd(mActivity, "testx9dtjwj8hp");
        }
        RewardAdLoadListener rewardAdLoadListener = new RewardAdLoadListener() {
            @Override
            public void onRewardAdFailedToLoad(int errorCode) {
                if(isRewardAdFailure){
                    isRewardAdFailure=false;
                    rewardAd.loadAd(new AdParam.Builder().build(),
                            this);
                    new Handler().postDelayed(() -> {
                        isRewardAdFailure=true;
                    }, 20*1000);
                }
            }

            @Override
            public void onRewardedLoaded() {

            }
        };
        rewardAd.loadAd(new AdParam.Builder().build(), rewardAdLoadListener);
    }




    private void showRewardAd() {
        if (rewardAd.isLoaded()) {
            rewardAd.show(mActivity, new RewardAdStatusListener() {
                @Override
                public void onRewardAdClosed() {
                    loadRewardAd();
                }

                @Override
                public void onRewardAdFailedToShow(int errorCode) {

                }

                @Override
                public void onRewardAdOpened() {
                }

                @Override
                public void onRewarded(Reward reward) {

                    // 推荐立即发放奖励，同时在服务端做校验，奖励是否生效；如果未配置奖励物品信息，则根据实际场景发放奖励。
                    loadRewardAd();
                }

            });
        }
    }
}
