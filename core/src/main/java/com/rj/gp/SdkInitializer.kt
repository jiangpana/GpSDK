package com.rj.gp

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.facebook.ads.AudienceNetworkAds
import com.google.android.gms.ads.MobileAds
import com.rj.gp.ad.AppOpenManager
import com.rj.gp.ad.FaceBookAdHelper
import com.rj.gp.ad.GpAdHelper
import com.rj.gp.constant.AdConfigConstant
import com.rj.gp.ext.getManifestMetaData


/**
 * 包名:com.jansir.core
 */
class SdkInitializer : Initializer<Unit> {

    companion object{
        lateinit var sContext:Context
    }
    override fun create(context: Context) {
        sContext=context
        MobileAds.initialize(context.applicationContext, context.getManifestMetaData(AdConfigConstant.ADMOB_APPLICATION_ID_KEY));
        AudienceNetworkAds
            .buildInitSettings(context)
            .withInitListener {
                System.out.println("FaceBook Ad 初始化完成...")
            }
            .initialize()
        GpAdHelper.init()
        FaceBookAdHelper.init()
        val useOpen=getManifestMetaData(AdConfigConstant.ADMOB_OPENAD_ID_KEY).isNotEmpty()
        if (useOpen) AppOpenManager(context as Application)

    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }


}