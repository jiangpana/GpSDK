package com.rj.gp.ext

import android.content.Context
import android.content.pm.PackageManager
import com.rj.gp.SdkInitializer


fun Any.getManifestMetaData(key: String): String {
    try {
        val packageManager: PackageManager = SdkInitializer.sContext.packageManager
        val applicationInfo = packageManager.getApplicationInfo(
            SdkInitializer.sContext
                .packageName, PackageManager.GET_META_DATA
        )
        return applicationInfo.metaData.getString(key)?:""
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}