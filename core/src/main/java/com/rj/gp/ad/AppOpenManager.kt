package com.rj.gp.ad

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.rj.gp.ad.AdManager.showingInterstitialAd
import com.rj.gp.constant.AdConfigConstant
import com.rj.gp.ext.getManifestMetaData
import java.util.*

class AppOpenManager(
    private val myApplication: Application
) : LifecycleObserver, ActivityLifecycleCallbacks {
    private val mActivityList = LinkedList<Activity>()
    private var appOpenAd: AppOpenAd? = null
    private var loadCallback: AppOpenAdLoadCallback? = null

    companion object {
        private const val LOG_TAG = "AppOpenManager"
        private var AD_UNIT_ID = ""
        private const val AD_UNIT_TEST_ID = "ca-app-pub-3940256099942544/3419835294"
        private var isShowingAd = false
    }

    /**
     * Constructor
     */
    init {
        myApplication.registerActivityLifecycleCallbacks(this)
        AD_UNIT_ID = getManifestMetaData(AdConfigConstant.ADMOB_OPENAD_ID_KEY)
        if (AD_UNIT_ID.isEmpty()) {
            AD_UNIT_ID = AD_UNIT_TEST_ID
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        var show = false
        for (activity in mActivityList) {
            if (activity.javaClass.simpleName == "MainActivity") {
                show = true
                break
            }
        }
        if (show) showAdIfAvailable()
        Log.d(LOG_TAG, "onStart")
    }

    private var loadTime: Long = 0

    /**
     * Request an ad
     */
    fun fetchAd() {
        // We will implement this below.
        // Have unused ad, no need to fetch another.
        if (isAdAvailable) {
            return
        }
        loadCallback = object : AppOpenAdLoadCallback() {
            /**
             * Called when an app open ad has loaded.
             *
             * @param ad the loaded app open ad.
             */
            override fun onAppOpenAdLoaded(ad: AppOpenAd) {
                appOpenAd = ad
                loadTime = Date().time
            }

            /**
             * Called when an app open ad has failed to load.
             *
             * @param loadAdError the error.
             */
            override fun onAppOpenAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the error.
            }
        }
        val request = adRequest
        AppOpenAd.load(
            myApplication, AD_UNIT_ID, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback
        )
    }

    /**
     * Utility method to check if ad was loaded more than n hours ago.
     */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /**
     * Creates and returns ad request.
     */
    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    private val isAdAvailable: Boolean
        get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

    /**
     * Shows the ad if one isn't already showing.
     */
    private fun showAdIfAvailable() {
        if (showingInterstitialAd) return
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable) {
            Log.d(LOG_TAG, "Will show ad.")
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAd = false
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {}
                    override fun onAdShowedFullScreenContent() {
                        isShowingAd = true
                    }
                }
            appOpenAd!!.show(currentActivity, fullScreenContentCallback)
        } else {
            Log.d(LOG_TAG, "Can not show ad.")
            fetchAd()
        }
    }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
        setTopActivity(activity)
    }

    private fun setTopActivity(activity: Activity) {
        if (mActivityList.contains(activity)) {
            if (mActivityList.last != activity) {
                mActivityList.remove(activity)
                mActivityList.addLast(activity)
            }
        } else {
            mActivityList.addLast(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
        Log.v("viclee", activity.toString() + "onActivityStarted")
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle
    ) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
        mActivityList.remove(activity)
    }

    private var currentActivity: Activity? = null


}