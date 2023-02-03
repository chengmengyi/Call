package com.demo.call.ac

import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager
import com.demo.call.R
import com.demo.call.admob.ShowNativeAd
import com.demo.call.base.BaseAc
import com.demo.call.conf.Local
import com.demo.call.service.PhoneReceiver
import com.demo.call.util.FloatViewManager
import com.demo.call.util.LimitNumManager
import com.demo.call.util.hasOverlayPermission
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class HomeAc:BaseAc() {
    private val showNativeAd by lazy { ShowNativeAd(Local.HOME_CENTER,this) }

    override fun layoutId(): Int = R.layout.activity_home

    override fun onView() {
        immersionBar.statusBarView(top).init()
        EventBus.getDefault().register(this)
        FloatViewManager.initFloat(this)
        cl_cover1.setOnClickListener { toApplyAc(1) }
        cl_cover2.setOnClickListener { toApplyAc(2) }
        cl_cover3.setOnClickListener { toApplyAc(3) }
        cl_cover4.setOnClickListener { toApplyAc(4) }

        if(hasOverlayPermission(this)&&
            AndPermission.hasPermissions(
                this,
                Permission.READ_PHONE_STATE,
                Permission.CALL_PHONE,
                Permission.ANSWER_PHONE_CALLS,
                Permission.READ_CALL_LOG,
            )){
            registerReceiver()
        }
    }

    private fun toApplyAc(index:Int){
        startActivity(Intent(this,ApplyAc::class.java).apply {
            putExtra("index",index)
        })
    }

    override fun onResume() {
        super.onResume()
        if (LimitNumManager.checkCanRefresh(Local.HOME_CENTER)){
            showNativeAd.checkNativeAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        showNativeAd.stopCheck()
        LimitNumManager.setRefreshBool(Local.HOME_CENTER,true)
    }

    @Subscribe
    fun onEvent(s: String) {
        if (s=="success"){
            registerReceiver()
        }
    }

    private fun registerReceiver(){
        val filter = IntentFilter()
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(PhoneReceiver(), filter)
    }
}