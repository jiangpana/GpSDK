package com.rj.gp.ad

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
 * 包名:com.rj.core.ad
 */
object GpAdHelper {

    private lateinit var mBannerId: String
    private lateinit var mInterstitialId: String
    private lateinit var mVideoId: String
    private lateinit var mContext: WeakReference<Context>
    private  var mInterstitialAd: InterstitialAd?=null
    private  var mRewardedVideoAd: RewardedVideoAd?=null
    private val mHandler = Handler(Looper.getMainLooper())


    fun init(
        context: Context,
        gpid: String,
        bannerId: String,
        interstitialId: String,
        videoId: String,
        isTest: Boolean
    ) {
        mHandler.post {
            mContext = WeakReference(context)
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
            loadInterstitialAd()
            loadRewardedVideoAd()
        }
    }



    @Synchronized
    private fun loadInterstitialAd() {
        mInterstitialAd?.apply {
            if (isLoaded)return
        }
        mInterstitialAd = InterstitialAd( mContext.get())
        mInterstitialAd?.apply {
            adListener = interstitialAdListener
            adUnitId = mInterstitialId
        }
        mInterstitialAd?.loadAd(AdRequest.Builder().build())
    }

    @Synchronized
    private fun loadRewardedVideoAd() {
        mRewardedVideoAd?.apply {
            if (isLoaded){
                return
            }
        }
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mContext.get());
        mRewardedVideoAd?.rewardedVideoAdListener = rewardedVideoAdListener
        mRewardedVideoAd?.loadAd(
            mVideoId,
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

    var rewardedAction :(()->Unit)?=null
    fun showRewardedVideoAd(action:()->Unit) {
        rewardedAction=action
        mRewardedVideoAd?.apply {
            if (isLoaded) {
                show()
            }
        }
    }

    //Gravity.TOP or Gravity.BOTTOM
    @Synchronized
    fun showBannerAd(activity: Activity, location: Int =Gravity.BOTTOM) {
        val rootView = activity.findViewById<FrameLayout>(android.R.id.content);
        if (rootView.tag ==activity.javaClass.simpleName) {
            return
        }
        rootView.tag =activity.javaClass.simpleName
        mHandler.post {
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.gravity = location
            AdView(activity).apply {
                adUnitId = mBannerId
                adSize = AdSize.SMART_BANNER;
                adListener = object : AdListener() {
                    var flag=1
                    override fun onAdFailedToLoad(p0: Int) {
                        super.onAdFailedToLoad(p0)
                        if (flag ==20)return
                        mHandler.postDelayed({
                            loadAd(AdRequest.Builder().build())
                        }, ((flag++)*15*1000).toLong())
                    }
                }
                loadAd(AdRequest.Builder().build())
                rootView.addView(this, layoutParams)
            }
        }
    }
    private var j = 1
    private val interstitialAdListener: AdListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                j=1
            }
            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                if (j==20)return
                mHandler.postDelayed({
                        loadInterstitialAd()
                }, ((j++) * 15*1000).toLong())
            }

            override fun onAdClosed() {
                super.onAdClosed()
                loadInterstitialAd()
            }
        }




    private var i = 1
    private val rewardedVideoAdListener= object : RewardedVideoAdListener {
        var rewarded =false
        override fun onRewardedVideoAdClosed() {
            if (rewarded){
                rewardedAction?.invoke()
            }
            loadRewardedVideoAd()
        }

        override fun onRewardedVideoAdLeftApplication() {
        }

        override fun onRewardedVideoAdLoaded() {
            i = 1
        }

        override fun onRewardedVideoAdOpened() {
            rewarded=false
        }

        override fun onRewardedVideoCompleted() {
        }

        override fun onRewarded(p0: RewardItem?) {
            //奖励获得
            rewarded=true
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
        mContext.get()?.apply {
            mRewardedVideoAd?.pause(this)
        }
    }

    fun onResume() {
        mContext.get()?.apply {
            mRewardedVideoAd?.resume(this)
        }
    }

    fun onDestroy() {
        mContext.get()?.apply {
            mRewardedVideoAd?.destroy(this)
        }
    }
}