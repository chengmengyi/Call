package com.demo.call.admob

import com.demo.call.conf.Fire
import com.demo.call.conf.Local
import com.demo.call.myApp
import com.demo.call.util.LimitNumManager
import com.demo.call.util.callLog
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import org.json.JSONObject

object LoadAdManager {
    var fullAdShowing=false
    private val loadingAdList= arrayListOf<String>()
    private val loadAdResultMap= hashMapOf<String,AdResultBean>()

    fun load(type:String,loadOpenAgain:Boolean=true){
        if(LimitNumManager.limit()){
            "limit".callLog()
            return
        }

        if (loadingAdList.contains(type)){
            "$type loading".callLog()
            return
        }

        if(loadAdResultMap.containsKey(type)){
            val adResultBean = loadAdResultMap[type]
            if (null!=adResultBean?.loadAd){
                if(adResultBean.expired()){
                    removeAdByType(type)
                }else{
                    "$type has cache".callLog()
                    return
                }
            }
        }

        val parseAdJson = parseAdJson(type)
        if (parseAdJson.isEmpty()){
            "$type ad data is empty".callLog()
            return
        }
        loadingAdList.add(type)
        loop(type,parseAdJson.iterator(),loadOpenAgain)
    }

    private fun loop(type: String, iterator: Iterator<AdDataBean>, loadOpenAgain: Boolean){
        startLoadAd(type,iterator.next()){
            if (null==it){
                if (iterator.hasNext()){
                    loop(type,iterator,loadOpenAgain)
                }else{
                    loadingAdList.remove(type)
                    if (type== Local.OPEN&&loadOpenAgain){
                        load(type,loadOpenAgain = false)
                    }
                }
            }else{
                loadingAdList.remove(type)
                loadAdResultMap[type]=it
            }
        }
    }

    private fun startLoadAd(type:String,adDataBean: AdDataBean,result:(bean:AdResultBean?)->Unit){
        "start load $type,${adDataBean.toString()}".callLog()
        when(adDataBean.maSdia_tyy){
            "op"-> {
                AppOpenAd.load(
                    myApp,
                    adDataBean.maSdia_iid,
                    AdRequest.Builder().build(),
                    AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                    object : AppOpenAd.AppOpenAdLoadCallback(){
                        override fun onAdLoaded(p0: AppOpenAd) {
                            "load $type ad success".callLog()
                            result.invoke(AdResultBean(loadAd = p0, loadTime = System.currentTimeMillis()))
                        }

                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            "load $type fail,${p0.message}".callLog()
                            result.invoke(null)
                        }
                    }
                )
            }
            "it"-> {
                InterstitialAd.load(
                    myApp,
                    adDataBean.maSdia_iid,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback(){
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            "load $type fail,${p0.message}".callLog()
                            result.invoke(null)
                        }

                        override fun onAdLoaded(p0: InterstitialAd) {
                            "load $type ad success".callLog()
                            result.invoke(AdResultBean(loadAd = p0, loadTime = System.currentTimeMillis()))
                        }
                    }
                )
            }
            "nv"-> {
                AdLoader.Builder(
                    myApp,
                    adDataBean.maSdia_iid,
                ).forNativeAd {
                    "load $type ad success".callLog()
                    result.invoke(AdResultBean(loadAd = it, loadTime = System.currentTimeMillis()))
                }
                    .withAdListener(object : AdListener(){
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            super.onAdFailedToLoad(p0)
                            "load $type fail,${p0.message},${p0.code}".callLog()
                            result.invoke(null)
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            LimitNumManager.addClick()
                        }
                    })
                    .withNativeAdOptions(
                        NativeAdOptions.Builder()
                            .setAdChoicesPlacement(
                                NativeAdOptions.ADCHOICES_BOTTOM_LEFT
                            )
                            .build()
                    )
                    .build()
                    .loadAd(AdRequest.Builder().build())
            }
        }
    }

    private fun parseAdJson(type: String):List<AdDataBean>{
        val list= arrayListOf<AdDataBean>()
        try {
            val jsonArray = JSONObject(Fire.getAdStr()).getJSONArray(type)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    AdDataBean(
                        jsonObject.optString("maSdia_tty"),
                        jsonObject.optString("maSdia_iid"),
                        jsonObject.optString("maSdia_tyy"),
                        jsonObject.optInt("maSdia_spo"),
                    )
                )
            }
        }catch (e:Exception){
        }
        return list.filter { it.maSdia_tty == "admob" }.sortedByDescending { it.maSdia_spo }
    }

    fun getAdByType(type: String)= loadAdResultMap[type]?.loadAd

    fun removeAdByType(type: String){
        loadAdResultMap.remove(type)
    }

    fun preLoadAllAd(){
        load(Local.OPEN)
        load(Local.HOME_CENTER)
        load(Local.RESULT_BOTTOM)
        load(Local.RESULT_BACK)
    }
}