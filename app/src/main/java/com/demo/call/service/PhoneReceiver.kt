package com.demo.call.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.demo.call.util.FloatViewManager

class PhoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.PHONE_STATE" == intent.action) {
            // 如果是来电
            val tManager = context.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            // 来电号码
            val mIncomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            when (tManager.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    if (null!=mIncomingNumber){
                        FloatViewManager.setPhoneNum(mIncomingNumber)
                        FloatViewManager.show()
                    }
                }
                TelephonyManager.CALL_STATE_IDLE,TelephonyManager.CALL_STATE_OFFHOOK -> {
                    FloatViewManager.hide()
                }
            }
        }
    }

}