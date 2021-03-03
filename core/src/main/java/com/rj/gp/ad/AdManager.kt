package com.rj.gp.ad

import android.app.Activity
import android.app.Application
import android.content.Context
import com.google.android.gms.ads.MobileAds


object AdManager {
    fun init(
        context: Application,
        admob_id: String,
        gp_bannerId: String,
        gp_interstitialId: String,
        gp_videoId: String,
        gp_open_ad_Id: String,
        fb_BannerId: String,
        fb_InterstitialId: String,
        useOpen : Boolean,
        isTest: Boolean
    ) {
        MobileAds.initialize(context.applicationContext, admob_id);
        GpAdHelper.init(context.applicationContext, gp_bannerId, gp_interstitialId, gp_videoId, isTest)
        FaceBookAdHelper.init(context.applicationContext,fb_BannerId,fb_InterstitialId,isTest)
        if (useOpen){
            AppOpenManager(context,gp_open_ad_Id,isTest)
        }
    }

    fun showBanner(activity: Activity){
        GpAdHelper.showBannerAd(activity)
    }

    fun dismissBanner(){

    }

    fun showInterstitialAd(){
        GpAdHelper.showGpInterstitialAd()
    }

    fun showRewardedVideoAd(action:()->Unit){
        GpAdHelper.showRewardedVideoAd(action)
    }

}