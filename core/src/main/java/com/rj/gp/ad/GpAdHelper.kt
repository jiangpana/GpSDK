package com.rj.gp.ad

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.rj.gp.SdkInitializer
import com.rj.gp.constant.AdConfigConstant
import com.rj.gp.ext.getManifestMetaData
import java.lang.ref.WeakReference

/**
 * 包名:com.rj.core.ad
 */
object GpAdHelper {

    private  val mBannerId by lazy { 
      val id=  getManifestMetaData(AdConfigConstant.ADMOB_BANNER_ID_KEY)
        if (id.isNotEmpty()) id else "ca-app-pub-3940256099942544/6300978111" 
    }
    private val mInterstitialId by lazy {
        val id=  getManifestMetaData(AdConfigConstant.ADMOB_INTERSTITIAL_ID_KEY)
        if (id.isNotEmpty()) id else  "ca-app-pub-3940256099942544/1033173712"
    }
    private val mRewardedVideoId by lazy {
        val id=  getManifestMetaData(AdConfigConstant.ADMOB_REWARDEDVIDEO_ID_KEY)
        if (id.isNotEmpty()) id else  "ca-app-pub-3940256099942544/5224354917"
    }
    private  val mContext  by lazy {
        SdkInitializer.sContext
    }
    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedVideoAd: RewardedVideoAd? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private var bannerAd:AdView? = null

    fun init(
    ) {
        mHandler.post {
            loadInterstitialAd()
            loadRewardedVideoAd()
        }
    }

    @Synchronized
    private fun loadInterstitialAd() {
        mInterstitialAd?.apply {
            if (isLoaded) return
        }
        mInterstitialAd = InterstitialAd(mContext)
        mInterstitialAd?.apply {
            adListener = interstitialAdListener
            adUnitId = mInterstitialId
        }
        mInterstitialAd?.loadAd(AdRequest.Builder().build())
    }

    @Synchronized
    private fun loadRewardedVideoAd() {
        mRewardedVideoAd?.apply {
            if (isLoaded) {
                return
            }
        }
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mContext);
        mRewardedVideoAd?.rewardedVideoAdListener = rewardedVideoAdListener
        mRewardedVideoAd?.loadAd(
            mRewardedVideoId,
            AdRequest.Builder().build()
        )
    }

    fun showGpInterstitialAd() {
        mInterstitialAd?.apply {
            if (isLoaded) {
                show()
            }
        }
    }

    var rewardedAction: (() -> Unit)? = null

    var rewardedLoadedAction: (() -> Unit)? = null

    fun showRewardedVideoAd(action: () -> Unit) {
        rewardedAction = action
        mRewardedVideoAd?.apply {
            if (isLoaded) {
                show()
            }
        }
    }

    fun isRewardedVideoAdReady():Boolean {
        mRewardedVideoAd?.apply {
            if (isLoaded) {
            return  true
            }
        }
        return false
    }

    fun isInterstitialAdReady():Boolean {
        mInterstitialAd?.apply {
            if (isLoaded) {
            return  true
            }
        }
        return false
    }

    //Gravity.TOP or Gravity.BOTTOM
    @Synchronized
    fun showBannerAd(activity: Activity, location: Int = Gravity.BOTTOM) {
        bannerAd?.visibility=View.VISIBLE
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
            AdView(activity).apply {
                bannerAd =this
                adUnitId = mBannerId
                adSize = AdSize.SMART_BANNER;
                adListener = object : AdListener() {
                    var flag = 1
                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                        if (flag == 20) return
                        mHandler.postDelayed({
                            loadAd(AdRequest.Builder().build())
                        }, ((flag++) * 15 * 1000).toLong())
                    }
                }
                loadAd(AdRequest.Builder().build())
                activity.addContentView(this,layoutParams)
            }
        }
    }

    //Gravity.TOP or Gravity.BOTTOM
    @Synchronized
    fun showBannerAd(activity:Activity,vp: ViewGroup, location: Int = Gravity.BOTTOM) {
        bannerAd?.visibility=View.VISIBLE
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
            AdView(activity).apply {
                bannerAd =this
                adUnitId = mBannerId
                adSize = AdSize.SMART_BANNER;
                adListener = object : AdListener() {
                    var flag = 1
                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                        if (flag == 20) return
                        mHandler.postDelayed({
                            loadAd(AdRequest.Builder().build())
                        }, ((flag++) * 15 * 1000).toLong())
                    }
                }
                loadAd(AdRequest.Builder().build())
                vp.addView(this,layoutParams)
            }
        }
    }

    fun dismissBanner(){
       bannerAd?.visibility= View.GONE
    }


    private var j = 1
    private val interstitialAdListener: AdListener = object : AdListener() {
        override fun onAdOpened() {
            super.onAdOpened()
            AdManager.showingInterstitialAd=true
        }

        override fun onAdLoaded() {
            super.onAdLoaded()
            j = 1
        }

        override fun onAdFailedToLoad(p0: Int) {
            super.onAdFailedToLoad(p0)
            if (j == 20) return
            mHandler.postDelayed({
                loadInterstitialAd()
            }, ((j++) * 15 * 1000).toLong())
        }

        override fun onAdClosed() {
            super.onAdClosed()
            AdManager.showingInterstitialAd=false
            loadInterstitialAd()
        }
    }


    private var i = 1
    private val rewardedVideoAdListener = object : RewardedVideoAdListener {
        var rewarded = false
        override fun onRewardedVideoAdClosed() {
            if (rewarded) {
                rewardedAction?.invoke()
            }
            loadRewardedVideoAd()
        }

        override fun onRewardedVideoAdLeftApplication() {
        }

        override fun onRewardedVideoAdLoaded() {
            i = 1
            rewardedLoadedAction?.invoke()
        }

        override fun onRewardedVideoAdOpened() {
            rewarded = false
        }

        override fun onRewardedVideoCompleted() {
        }

        override fun onRewarded(p0: RewardItem?) {
            //奖励获得
            rewarded = true
        }

        override fun onRewardedVideoStarted() {
        }

        override fun onRewardedVideoAdFailedToLoad(p0: Int) {
            if (i == 20) return
            mHandler.postDelayed({
                loadRewardedVideoAd()
            }, ((i++) * 15 * 1000).toLong())
        }
    }


    //******************* 在activity 生命周期回调 *******************//
    fun onPause() {
        mContext.apply {
            mRewardedVideoAd?.pause(this)
        }
    }

    fun onResume() {
        mContext.apply {
            mRewardedVideoAd?.resume(this)
        }
    }

    fun onDestroy() {
        mContext.apply {
            mRewardedVideoAd?.destroy(this)
        }
    }
}