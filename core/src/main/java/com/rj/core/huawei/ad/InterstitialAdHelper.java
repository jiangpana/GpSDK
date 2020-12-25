package com.rj.core.huawei.ad;


import android.app.Activity;
import android.util.Log;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.InterstitialAd;
import com.rj.core.utils.UnityUtil;

public class InterstitialAdHelper {

    private String TAG ="InterstitialAdHelper";
    public InterstitialAd interstitialAd;
    private Activity mActivity= UnityUtil.getActivity();

    public  void showInterstitial(String adId) {
        interstitialAd = new InterstitialAd(mActivity);
        interstitialAd.setAdId(adId); // 设置广告位ID.
        interstitialAd.setAdListener(adListener);
        AdParam adParam = new AdParam.Builder().build();
        interstitialAd.loadAd(adParam);

    }

    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            // 展示插屏广告.
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
        }

        @Override
        public void onAdFailed(int errorCode) {
            Log.d(TAG, "Ad load failed with error code: " + errorCode);
        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "onAdClosed");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "onAdClicked");
            super.onAdClicked();
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "onAdOpened");
            super.onAdOpened();
        }
    };
}
