package com.demo.call.admob

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.SizeUtils
import com.demo.call.R
import com.demo.call.base.BaseAc
import com.demo.call.conf.Local
import com.demo.call.util.LimitNumManager
import com.demo.call.util.callLog
import com.demo.call.util.show
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.*

class ShowNativeAd(
    private val type:String,
    private val baseAc: BaseAc
) {
    private var checkJob:Job?=null
    private var lastNativeAd: NativeAd?=null
    private val showDesc= listOf(Local.RESULT_BOTTOM)

    fun checkNativeAd(){
        LoadAdManager.load(type)
        stopCheck()
        checkJob= GlobalScope.launch(Dispatchers.Main) {
            delay(300L)
            if (!baseAc.resume){
                return@launch
            }
            while (true) {
                if (!isActive) {
                    break
                }

                val ad = LoadAdManager.getAdByType(type)
                if(baseAc.resume && null!=ad && ad is NativeAd){
                    cancel()
                    lastNativeAd?.destroy()
                    lastNativeAd=ad
                    starShowNativeAd(ad)
                }

                delay(1000L)
            }
        }
    }

    private fun starShowNativeAd(nativeAd:NativeAd){
        "start show $type ad".callLog()
        val viewNative = baseAc.findViewById<NativeAdView>(R.id.ad_view)
        viewNative.iconView=baseAc.findViewById(R.id.ad_logo)
        (viewNative.iconView as ImageFilterView).setImageDrawable(nativeAd.icon?.drawable)

        viewNative.callToActionView=baseAc.findViewById(R.id.ad_install)
        (viewNative.callToActionView as AppCompatTextView).text=nativeAd.callToAction

        viewNative.mediaView=baseAc.findViewById(R.id.ad_media)
        nativeAd.mediaContent?.let {
            viewNative.mediaView?.apply {
                setMediaContent(it)
                setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View?, outline: Outline?) {
                        if (view == null || outline == null) return
                        outline.setRoundRect(
                            0,
                            0,
                            view.width,
                            view.height,
                            SizeUtils.dp2px(8F).toFloat()
                        )
                        view.clipToOutline = true
                    }
                }
            }
        }


        if (showDesc.contains(type)){
            viewNative.bodyView=baseAc.findViewById(R.id.ad_desc)
            (viewNative.bodyView as AppCompatTextView).text=nativeAd.body
        }

        viewNative.headlineView=baseAc.findViewById(R.id.ad_title)
        (viewNative.headlineView as AppCompatTextView).text=nativeAd.headline

        viewNative.setNativeAd(nativeAd)
        baseAc.findViewById<AppCompatImageView>(R.id.ad_cover).show(false)

        LimitNumManager.addShow()
        LoadAdManager.removeAdByType(type)
        LoadAdManager.load(type)
        LimitNumManager.setRefreshBool(type,false)
    }

    fun stopCheck(){
        checkJob?.cancel()
        checkJob=null
    }
}