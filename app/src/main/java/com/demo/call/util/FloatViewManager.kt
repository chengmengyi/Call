package com.demo.call.util

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.ImageViewCompat
import com.demo.call.R
import android.util.Log

import android.content.Intent
import android.media.session.MediaSessionManager
import android.os.IBinder
import android.telecom.TelecomManager

import android.view.KeyEvent
import java.lang.Exception
import java.lang.reflect.Method
import androidx.core.content.ContextCompat.getSystemService

import android.telephony.TelephonyManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.tencent.mmkv.MMKV


@SuppressLint("StaticFieldLeak")
object FloatViewManager {
    private var showing=false
    private var coverImageView: ConstraintLayout?=null
    private var phoneNumTextView:AppCompatTextView?=null
    private lateinit var view: View
    private lateinit var windowManager: WindowManager
    private lateinit var layoutParams: WindowManager.LayoutParams

    fun initFloat(context: Context){
        initView(context)
    }

    private fun initView(context: Context){
        windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        //        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.gravity = Gravity.LEFT or Gravity.TOP
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                or WindowManager.LayoutParams.FLAG_FULLSCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT


        context.height()
        view = LayoutInflater.from(context).inflate(R.layout.view_float, null)
        phoneNumTextView=view.findViewById(R.id.tv_num)
        coverImageView=view.findViewById(R.id.root_layout)
        view.findViewById<AppCompatImageView>(R.id.iv_accept).setOnClickListener {
            answerRingingCall(context)
        }
        view.findViewById<AppCompatImageView>(R.id.iv_hangup).setOnClickListener {
            endCall(context)
        }
    }

    fun setPhoneNum(num:String?){
        phoneNumTextView?.text=num?:"Unknown number"
        val index = MMKV.defaultMMKV().decodeInt("index",1)
        coverImageView?.setBackgroundResource(getBgResByIndex(index))
    }

    fun show(){
        if (!showing){
            showing=true
            windowManager.addView(view, layoutParams)
        }
    }

    fun hide(){
        if (showing){
            showing=false
            windowManager.removeView(view)
        }
    }

    private fun Context.height(){
        val metrics: DisplayMetrics = resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
    }


    @SuppressLint("MissingPermission")
    private fun answerRingingCall(context: Context) {
        try {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecomManager.acceptRingingCall()
        }catch (e:Exception){ }
    }

    private fun endCall(context: Context){
        try {
            tryEndCall(context)
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            val classTelephony = Class.forName(telephonyManager!!.javaClass.name)
            val methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony")
            methodGetITelephony.isAccessible = true
            val telephonyInterface = methodGetITelephony.invoke(telephonyManager)
            val telephonyInterfaceClass = Class.forName(telephonyInterface.javaClass.name)
            val methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall")
            methodEndCall.invoke(telephonyInterface)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("MissingPermission")
    private fun tryEndCall(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager?
                if (telecomManager != null) {
                    telecomManager.endCall()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}