package com.rj.gp.ad

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.ads.*
import com.facebook.ads.AdSize.BANNER_HEIGHT_50
import com.rj.gp.SdkInitializer
import com.rj.gp.constant.AdConfigConstant
import com.rj.gp.ext.getManifestMetaData

import java.lang.ref.WeakReference


object FaceBookAdHelper {

    private val mHandler = Handler(Looper.getMainLooper())

    private val mBannerId by lazy {
        val id = getManifestMetaData(AdConfigConstant.FACEBOOK_BANNER_ID_KEY)
        if (id.isNotEmpty()) id else "IMG_16_9_APP_INSTALL#$id"
    }
    private val mInterstitialId by lazy {
        val id = getManifestMetaData(AdConfigConstant.FACEBOOK_INTERSTITIAL_ID_KEY)
        if (id.isNotEmpty()) id else "PLAYABLE#${id}"
    }
    private val mRewardedVideoId by lazy {
        val id = getManifestMetaData(AdConfigConstant.FACEBOOK_REWARDEDVIDEO_ID_KEY)
        if (id.isNotEmpty()) id else "ca-app-pub-3940256099942544/5224354917"
    }
    private val mContext by lazy {
        SdkInitializer.sContext
    }
    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedVideoAd: RewardedVideoAd? = null
    private var bannerAd: AdView? = null

    fun init(
    ) {
        initFaceBookAd()
    }

    private fun initFaceBookAd() {
        //预加载广告
        loadInterstitialAd()
//        loadRewardedVideoAd()
    }

    //Gravity.TOP or Gravity.BOTTOM
    @Synchronized
    fun showBannerAd(activity: Activity, location: Int = Gravity.BOTTOM) {
        bannerAd?.visibility = View.VISIBLE
        val rootView = activity.findViewById<FrameLayout>(android.R.id.content);
        if (rootView.tag == activity.javaClass.simpleName) {
            return
        }
        rootView.tag = activity.javaClass.simpleName
        mHandler.post {
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = location
            AdView(
                activity,
                mBannerId, BANNER_HEIGHT_50
            ).apply {
                setAdListener(object : com.facebook.ads.AdListener {
                    var flag = 1
                    override fun onAdClicked(p0: Ad?) {
                    }

                    override fun onError(p0: Ad?, p1: AdError?) {
                        if (flag >= 20) return
                        mHandler.postDelayed({ loadAd() }, ((flag++) * 15 * 1000).toLong())
                    }

                    override fun onAdLoaded(p0: Ad?) {
                        flag = 1
                    }

                    override fun onLoggingImpression(p0: Ad?) {
                    }
                })
                loadAd()
                rootView.addView(this, layoutParams)
                bannerAd = this
            }
        }
    }

    @Synchronized
    fun showBannerAd(activity: Activity, vp: ViewGroup, location: Int = Gravity.BOTTOM) {
        bannerAd?.visibility = View.VISIBLE
        val rootView = activity.findViewById<FrameLayout>(android.R.id.content);
        if (rootView.tag == activity.javaClass.simpleName) {
            return
        }
        rootView.tag = activity.javaClass.simpleName
        mHandler.post {
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = location
            AdView(
                activity,
                mBannerId, BANNER_HEIGHT_50
            ).apply {
                setAdListener(object : com.facebook.ads.AdListener {
                    var flag = 1
                    override fun onAdClicked(p0: Ad?) {
                    }

                    override fun onError(p0: Ad?, p1: AdError?) {
                        if (flag >= 20) return
                        mHandler.postDelayed({ loadAd() }, ((flag++) * 15 * 1000).toLong())
                    }

                    override fun onAdLoaded(p0: Ad?) {
                        flag = 1
                    }

                    override fun onLoggingImpression(p0: Ad?) {
                    }
                })
                loadAd()
                vp.addView(this, layoutParams)
                bannerAd = this
            }
        }
    }

    fun dismissBanner() {
        bannerAd?.visibility = View.GONE
    }

    @Synchronized
    private fun loadInterstitialAd() {
        mInterstitialAd?.apply {
            if (isAdLoaded && !isAdInvalidated) {
                return
            }
        }
        mInterstitialAd = InterstitialAd(
            mContext,
            mInterstitialId
        );
        mInterstitialAd?.apply {
            loadAd(
                buildLoadAdConfig()
                    .withAdListener(interstitialAdListener)
                    .build()
            )
        }
    }

    fun showFaceBookInterstitialAd() {
        mInterstitialAd?.apply {
            if (isAdLoaded && !isAdInvalidated) {
                show()
            }
        }
    }

    fun isInterstitialAdReady(): Boolean {
        mInterstitialAd?.apply {
            if (isAdLoaded && !isAdInvalidated) {
                return true
            }
        }
        return false
    }

    private var i = 1
    private val interstitialAdListener: InterstitialAdListener =
        object : InterstitialAdListener {

            override fun onInterstitialDisplayed(p0: Ad?) {
                AdManager.showingInterstitialAd = true
                System.out.println("Interstitial ad displayed.")
            }


            override fun onAdClicked(p0: Ad?) {
                System.out.println("onAdClicked");
            }

            override fun onInterstitialDismissed(p0: Ad?) {
                AdManager.showingInterstitialAd = false
                System.out.println("onInterstitialDismissed ");
                loadInterstitialAd()
            }

            override fun onError(p0: Ad?, adError: AdError) {
                System.out.println("  " + adError.getErrorMessage());
                if (i == 20) return
                mHandler.postDelayed({
                    loadInterstitialAd()
                }, ((i++) * 15 * 1000).toLong())
            }

            override fun onAdLoaded(p0: Ad?) {
                System.out.println("onAdLoaded  ");
                i = 1;
            }

            override fun onLoggingImpression(p0: Ad?) {
            }
        }


    @Synchronized
    private fun loadRewardedVideoAd() {
        mRewardedVideoAd?.apply {
            if (isAdLoaded && !isAdInvalidated) {
                return
            }
        }
        mRewardedVideoAd = RewardedVideoAd(
            mContext,
            mRewardedVideoId
        )
        mRewardedVideoAd?.apply {
            loadAd(
                buildLoadAdConfig()
                    .withAdListener(rewardedVideoAdListener)
                    .build()
            )
        }
    }

    var rewardedAction: (() -> Unit)? = null
    fun showFaceBookRewardedVideoAd(action: () -> Unit) {
        rewardedAction = action
        mRewardedVideoAd?.apply {
            if (isAdLoaded && !isAdInvalidated) {
                show()
            }
        }
    }


    private var j = 1
    private val rewardedVideoAdListener: RewardedVideoAdListener =
        object : RewardedVideoAdListener {
            var rewarded = false
            override fun onRewardedVideoClosed() {
                if (rewarded) {
                    rewarded = false
                    rewardedAction?.invoke()
                }
                loadRewardedVideoAd()
            }

            override fun onAdClicked(p0: Ad?) {
            }

            override fun onRewardedVideoCompleted() {
                rewarded = true
            }

            override fun onError(p0: Ad?, adError: AdError) {
                System.out.println("onError  " + adError.getErrorMessage());
                if (j == 20) return
                mHandler.postDelayed({
                    loadInterstitialAd()
                }, ((j++) * 15 * 1000).toLong())
            }

            override fun onAdLoaded(p0: Ad?) {
                System.out.println("onAdLoaded  ");
                j = 1;
            }

            override fun onLoggingImpression(p0: Ad?) {
            }

        }


    fun onDestroy() {
        bannerAd?.destroy()
        mInterstitialAd?.destroy();
        mRewardedVideoAd?.destroy()
    }

    private val localJson = "{\n" +
            "  \"show_interstitial_after_play\": 2,\n" +
            "  \"show_interstitial\": true,\n" +
            "  \"show_banner\": true,\n" +
            "  \"show_banner_after_playeGame\": 1,\n" +
            "  \"ad_resume\": false,\n" +
            "  \"ad_resume_interval\": 15,\n" +
            "  \"ad_req_failure_max\": 10,\n" +
            "  \"ad_strategy\": [\n" +
            "    {\n" +
            "      \"ad_type\": \"admob\",\n" +
            "      \"bannerRate\": 50,\n" +
            "      \"interstitialRate\": 20\n" +
            "    },\n" +
            "    {\n" +
            "      \"ad_type\": \"facebook\",\n" +
            "      \"bannerRate\": 50,\n" +
            "      \"interstitialRate\": 80\n" +
            "    }\n" +
            "  ],\n" +
            "  \"ad_break_type\": [\n" +
            "    \"facebook\",\n" +
            "    \"admob\"\n" +
            "  ],\n" +
            "  \"ad_break_time\": 1,\n" +
            "  \"ad_break\": true\n" +
            "}"
}