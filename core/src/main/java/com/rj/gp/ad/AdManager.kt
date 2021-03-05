package com.rj.gp.ad

import android.app.Activity
import android.view.ViewGroup
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONObject


object AdManager {
    var showingInterstitialAd =false

    fun showBanner(activity: Activity) {
        if (!showBanner) return
        passConfigDisplayAD(true, {
            GpAdHelper.showBannerAd(activity)
        }, {
            FaceBookAdHelper.showBannerAd(activity)
        })
    }

    fun showBanner(activity: Activity, vp: ViewGroup) {
        if (!showBanner) return
        passConfigDisplayAD(true, {
            GpAdHelper.showBannerAd(activity, vp)
        }, {
            FaceBookAdHelper.showBannerAd(activity, vp)
        })

    }

    fun dismissBanner() {
        FaceBookAdHelper.dismissBanner()
        GpAdHelper.dismissBanner()
    }

    fun showInterstitialAd() {
        if (!showInterstitial) return
        passConfigDisplayAD(false, {
            if (GpAdHelper.isInterstitialAdReady()) {
                GpAdHelper.showGpInterstitialAd()
            }else{
                FaceBookAdHelper.showFaceBookInterstitialAd()
            }
        }, {
            if (FaceBookAdHelper.isInterstitialAdReady()) {
                FaceBookAdHelper.showFaceBookInterstitialAd()
            }else{
                GpAdHelper.showGpInterstitialAd()
            }
        })
    }

    fun showRewardedVideoAd(rewardedAction: () -> Unit) {
        GpAdHelper.showRewardedVideoAd(rewardedAction)
    }

    fun setRewardLoadAction(loadAction: () -> Unit) {
        GpAdHelper.rewardedLoadedAction = loadAction
    }

    private fun passConfigDisplayAD(
        isBanner: Boolean = true,
        adMob: () -> Unit,
        faceBook: () -> Unit
    ) {
        val admobObj = configJsonObj.getJSONArray("ad_strategy").getJSONObject(0)
        val faceBookObj = configJsonObj.getJSONArray("ad_strategy").getJSONObject(1)
        val bannerTotal = admobObj.getInt("bannerRate") + faceBookObj.getInt("bannerRate")
        val interstitialTotal =
            admobObj.getInt("interstitialRate") + faceBookObj.getInt("interstitialRate")
        if (isBanner) {
            if ((0..bannerTotal).random() <= admobObj.getInt("bannerRate")) adMob() else faceBook()
        } else {
            if ((0..interstitialTotal).random() <= admobObj.getInt("interstitialRate")) adMob() else faceBook()
        }
    }

    private val showBanner by lazy {
        configJsonObj.getBoolean("show_banner")
    }
    private val showInterstitial by lazy {
        configJsonObj.getBoolean("show_interstitial")
    }
    private val configJsonObj by lazy {
        var remoteAdConfig = FirebaseRemoteConfig.getInstance().getString("AdConfig")
        try {
            if (remoteAdConfig.isEmpty()) {
                remoteAdConfig = LOCAL_JSON
                JSONObject(remoteAdConfig)
            }
        } catch (e: Exception) {
            remoteAdConfig = LOCAL_JSON
        }
        JSONObject(remoteAdConfig)
    }

    fun isRewardedVideoAdReady(): Boolean {
        return GpAdHelper.isRewardedVideoAdReady()
    }


    private const val LOCAL_JSON = "{\n" +
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
            "      \"interstitialRate\": 50\n" +
            "    },\n" +
            "    {\n" +
            "      \"ad_type\": \"facebook\",\n" +
            "      \"bannerRate\": 50,\n" +
            "      \"interstitialRate\": 50\n" +
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