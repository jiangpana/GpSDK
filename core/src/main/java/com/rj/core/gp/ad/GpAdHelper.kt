package com.rj.core.gp.ad

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import java.lang.ref.WeakReference

/**
 * 包名:com.rj.core.gp.ad
 */
object GpAdHelper {

    private lateinit var mBannerId: String
    private lateinit var mInterstitialId: String
    private lateinit var mVideoId: String
    private lateinit var mActivity: WeakReference<Context>
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private val mHandler = Handler(Looper.getMainLooper())

    fun initGpAd(
        context: Context,
        gpid: String,
        bannerId: String,
        interstitialId: String,
        videoId: String,
        isTest: Boolean
    ) {
        mHandler.post {
            mActivity = WeakReference(context)
            MobileAds.initialize(context, gpid);
            mBannerId = bannerId
            mInterstitialId = interstitialId
            mVideoId = videoId
            //测试
            if (isTest) {
                mBannerId = "ca-app-pub-3940256099942544/6300978111"
                mInterstitialId = "ca-app-pub-3940256099942544/1033173712"
                mVideoId = "ca-app-pub-3940256099942544/5224354917"
            }
            initGpAd()
        }
    }


    private fun initGpAd() {
        mActivity.get()?.let {
            mInterstitialAd = InterstitialAd(it)
            mInterstitialAd.apply {
                adListener = newAdListenerInstance(true)
                adUnitId = mInterstitialId
            }
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(it);
            mRewardedVideoAd.rewardedVideoAdListener = mRewardedVideoAdListener;
        }
        loadInterstitialAd()
        loadRewardedVideoAd()
    }

    private fun loadInterstitialAd() {
        mHandler.post {
            mInterstitialAd.loadAd(AdRequest.Builder().build())

        }
    }

    private fun loadRewardedVideoAd() {
        mHandler.post {
            mRewardedVideoAd.loadAd(
                mVideoId,
                AdRequest.Builder().build()
            )
        }

    }

    fun showGpInterstitialAd() {
        mHandler.post {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            }
        }


    }

    fun showRewardedVideoAd() {
        mHandler.post {
            if (mRewardedVideoAd.isLoaded) {
                mRewardedVideoAd.show()
            }
        }
    }

    //Gravity.TOP or Gravity.BOTTOM
    fun showBannerAd(activity: Activity, location: Int) {
        mHandler.post {
            val rootView = activity.findViewById<FrameLayout>(android.R.id.content);
            if (rootView.findViewWithTag<AdView>(activity.javaClass.simpleName) != null) {
                return@post
            }
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.gravity = location
            AdView(activity).apply {
                tag = activity.javaClass.simpleName
                adUnitId = mBannerId
                adSize = AdSize.SMART_BANNER;
                adListener = newAdListenerInstance()
                loadAd(AdRequest.Builder().build())
                rootView.addView(this, layoutParams)
            }
        }
    }

    private fun newAdListenerInstance(isInterstitial: Boolean = false): AdListener {
        return object : AdListener() {
            private var i = 1
            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                mHandler.postDelayed({
                    if (isInterstitial) {
                        loadInterstitialAd()
                    }
                }, ((i++) * 15000).toLong())
            }

            override fun onAdClosed() {
                super.onAdClosed()
                loadInterstitialAd()
            }
        }
    }


    private val mRewardedVideoAdListener = object : RewardedVideoAdListener {
        private var i = 1
        override fun onRewardedVideoAdClosed() {
            loadRewardedVideoAd()
        }

        override fun onRewardedVideoAdLeftApplication() {
        }

        override fun onRewardedVideoAdLoaded() {
        }

        override fun onRewardedVideoAdOpened() {
        }

        override fun onRewardedVideoCompleted() {
        }

        override fun onRewarded(p0: RewardItem?) {
        }

        override fun onRewardedVideoStarted() {
        }

        override fun onRewardedVideoAdFailedToLoad(p0: Int) {
            mHandler.postDelayed({
                loadRewardedVideoAd()
            }, ((i++) * 15000).toLong())
        }

    }

    //******************* 在activity 生命周期回调 *******************//
    fun onPause() {
        mActivity.get()?.apply {
            mRewardedVideoAd.pause(this)
        }
    }

    fun onResume() {
        mActivity.get()?.apply {
            mRewardedVideoAd.resume(this)
        }
    }

    fun onDestroy() {
        mActivity.get()?.apply {
            mRewardedVideoAd.destroy(this)
        }
    }
}