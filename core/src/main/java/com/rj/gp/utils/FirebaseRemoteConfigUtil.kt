package com.rj.gp.utils

import com.google.firebase.remoteconfig.FirebaseRemoteConfig


object FirebaseRemoteConfigUtil {

    fun getString(key :String):String{
      return  FirebaseRemoteConfig.getInstance().getString(key)
    }

    fun getBoolean(key :String):Boolean{
        return  FirebaseRemoteConfig.getInstance().getBoolean(key)
    }

    fun getLong(key :String):Long{
        return  FirebaseRemoteConfig.getInstance().getLong(key)
    }

    fun getDouble(key :String):Double{
        return  FirebaseRemoteConfig.getInstance().getDouble(key)
    }
}