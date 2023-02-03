package com.demo.call.admob

import com.demo.call.base.BaseAc
import com.demo.call.util.callLog
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd


class ShowOpenAd(private val type:String, ) {
    fun showOpenAd(baseAc: BaseAc,callback:Boolean=false,showing:()->Unit,closeAd:()->Unit){
        val adByType = LoadAdManager.getAdByType(type)
        if (null!=adByType){
            if (LoadAdManager.fullAdShowing||!baseAc.resume){
                return
            }
            "start show $type ad".callLog()
            showing.invoke()
            when(adByType){
                is InterstitialAd ->{
                    adByType.fullScreenContentCallback=FullCallback(baseAc,type,closeAd)
                    adByType.show(baseAc)
                }
                is AppOpenAd ->{
                    adByType.fullScreenContentCallback=FullCallback(baseAc,type,closeAd)
                    adByType.show(baseAc)
                }
            }
        }else{
            if (callback){
                closeAd.invoke()
            }
        }
    }

}