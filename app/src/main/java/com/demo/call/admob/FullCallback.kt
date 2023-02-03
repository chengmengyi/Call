package com.demo.call.admob

import com.demo.call.base.BaseAc
import com.demo.call.conf.Local
import com.demo.call.util.LimitNumManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FullCallback(
    private val baseAc: BaseAc,
    private val type:String,
    private val closeAd:()->Unit
): FullScreenContentCallback() {
    override fun onAdDismissedFullScreenContent() {
        super.onAdDismissedFullScreenContent()
        LoadAdManager.fullAdShowing=false
        clickCloseAd()
    }

    override fun onAdShowedFullScreenContent() {
        super.onAdShowedFullScreenContent()
        LoadAdManager.fullAdShowing=true
        LimitNumManager.addShow()
        LoadAdManager.removeAdByType(type)
    }

    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
        super.onAdFailedToShowFullScreenContent(p0)
        LoadAdManager.fullAdShowing=false
        LoadAdManager.removeAdByType(type)
        clickCloseAd()
    }


    override fun onAdClicked() {
        super.onAdClicked()
        LimitNumManager.addClick()
    }

    private fun clickCloseAd(){
        if (type!= Local.OPEN||type!=Local.RESULT_BACK){
            LoadAdManager.load(type)
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(200L)
            if (baseAc.resume){
                closeAd.invoke()
            }
        }
    }
}