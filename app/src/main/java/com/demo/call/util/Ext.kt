package com.demo.call.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.demo.call.R

fun String.callLog(){
    Log.e("qwer",this)
}

fun View.show(show:Boolean){
    visibility=if (show)View.VISIBLE else View.GONE
}

fun getBgResByIndex(index:Int)=when(index){
    2-> R.drawable.big2
    3-> R.drawable.big3
    4-> R.drawable.big4
    else-> R.drawable.big1
}

fun Context.toast(string: String){
    Toast.makeText(this,string,Toast.LENGTH_SHORT).show()
}

fun hasOverlayPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return true
    return Settings.canDrawOverlays(context)
}