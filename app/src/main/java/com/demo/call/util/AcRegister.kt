package com.demo.call.util

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.demo.call.ac.HomeAc
import com.demo.call.ac.MainAc
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AcRegister {
    var front=true
    var banHotLoad=false
    private var jumpToMain=false
    private var callJob:Job?=null

    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(callback)
    }

    private val callback=object : Application.ActivityLifecycleCallbacks{
        private var pages=0
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
            pages++
            callJob?.cancel()
            callJob=null
            if (pages==1){
                front=true
                if (jumpToMain&&!banHotLoad){
                    if (ActivityUtils.isActivityExistsInStack(HomeAc::class.java)){
                        activity.startActivity(Intent(activity, MainAc::class.java))
                    }
                }
                jumpToMain=false
            }
        }

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {
            pages--
            if (pages<=0){
                front=false
                callJob= GlobalScope.launch {
                    delay(3000L)
                    jumpToMain=true
                    ActivityUtils.finishActivity(MainAc::class.java)
                    ActivityUtils.finishActivity(AdActivity::class.java)
                }
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }
}