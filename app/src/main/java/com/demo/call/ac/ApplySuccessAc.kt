package com.demo.call.ac

import com.demo.call.R
import com.demo.call.admob.LoadAdManager
import com.demo.call.admob.ShowNativeAd
import com.demo.call.admob.ShowOpenAd
import com.demo.call.base.BaseAc
import com.demo.call.conf.Local
import com.demo.call.util.LimitNumManager
import kotlinx.android.synthetic.main.activity_apply_success.*
import org.greenrobot.eventbus.EventBus

class ApplySuccessAc:BaseAc() {
    private val showNativeAd by lazy { ShowNativeAd(Local.RESULT_BOTTOM,this) }
    private val showBackAd by lazy { ShowOpenAd(Local.RESULT_BACK) }

    override fun layoutId(): Int = R.layout.activity_apply_success

    override fun onView() {
        LoadAdManager.load(Local.RESULT_BACK)
        iv_back.setOnClickListener { onBackPressed() }
        val index = intent.getIntExtra("index", 0)
        iv_cover.setImageResource(getBgResByIndex(index))
        EventBus.getDefault().post("success")
    }

    private fun getBgResByIndex(index:Int)=when(index){
        2->R.drawable.cover2
        3->R.drawable.cover3
        4->R.drawable.cover4
        else->R.drawable.cover1
    }

    override fun onBackPressed() {
        showBackAd.showOpenAd(this,callback = true, showing = {}, closeAd = { finish() })
    }

    override fun onResume() {
        super.onResume()
        if (LimitNumManager.checkCanRefresh(Local.RESULT_BOTTOM)){
            showNativeAd.checkNativeAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showNativeAd.stopCheck()
        LimitNumManager.setRefreshBool(Local.RESULT_BOTTOM,true)
    }
}