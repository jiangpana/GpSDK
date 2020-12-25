package com.rj.core.huawei.ad;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.huawei.hms.ads.AdListener;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.banner.BannerView;
import com.rj.core.utils.UnityUtil;

import static com.rj.core.utils.UnityUtil.callUnity;


public class BannerAdHelper {
    private String TAG = "BannerAdHelper";
    private Activity mActivity= UnityUtil.getActivity();
    private BannerView hwBannerView ;


    public void showBanner(final int location, final String hwBannerId) {
        final FrameLayout adRootView = mActivity.findViewById(android.R.id.content);
        adRootView.post(() -> executeShowBanner(location,hwBannerId,adRootView));
    }

    private void executeShowBanner(int location, String hwBannerId, FrameLayout adRootView) {
        if(hwBannerView==null){
            synchronized (BannerAdHelper.class){
                hwBannerView=new BannerView(mActivity);
                hwBannerView.setAdId(hwBannerId);
                hwBannerView.setAdListener(hwBannerAdListener);
                hwBannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_360_57);
            }
        }
        if (hwBannerView.getParent() != null) {
            adRootView.removeView(hwBannerView);
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        if (location == 1) {
            layoutParams.gravity = Gravity.TOP;
        } else {
            layoutParams.gravity = Gravity.BOTTOM;
        }
        adRootView.addView(hwBannerView, layoutParams);

        AdParam adParam = new AdParam.Builder().build();
        hwBannerView.loadAd(adParam);
        hwBannerView.setVisibility(View.VISIBLE);
        hwBannerView.resume();
    }


    public void dismissBanner() {
        if (hwBannerView != null) {
            hwBannerView.post(() -> {
                hwBannerView.pause();
                hwBannerView.setVisibility(View.GONE);
            });
        }
    }
    private AdListener hwBannerAdListener = new AdListener() {
        @Override
        public void onAdClosed() {
            super.onAdClosed();
                callUnity("androidCallBack", "onBannerAdClosed", "");

        }

        @Override
        public void onAdOpened() {
            super.onAdOpened();
                callUnity("androidCallBack", "onBannerAdOpened", "");
        }

        @Override
        public void onAdFailed(int i) {
            super.onAdFailed(i);
                callUnity("androidCallBack", "onBannerAdFailed", "");
        }

        @Override
        public void onAdClicked() {
            super.onAdClicked();
                callUnity("androidCallBack", "onBannerAdClicked", "");
        }
    };
}
