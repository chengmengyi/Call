package com.demo.call

import android.app.Application
import com.demo.call.util.AcRegister
import com.google.android.gms.ads.MobileAds
import com.tencent.mmkv.MMKV

lateinit var myApp: MyApp
class MyApp:Application() {
    override fun onCreate() {
        super.onCreate()
        myApp=this
        MobileAds.initialize(this)
        MMKV.initialize(this)
        AcRegister.register(this)
    }
}