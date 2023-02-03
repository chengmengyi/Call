package com.demo.call.conf

import com.demo.call.util.LimitNumManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tencent.mmkv.MMKV
import org.json.JSONObject

object Fire {

    fun readFireConf(){
//        val remoteConfig = Firebase.remoteConfig
//        remoteConfig.fetchAndActivate().addOnCompleteListener {
//            if (it.isSuccessful){
//                parseAdJson(remoteConfig.getString("maSdia"))
//            }
//        }
    }

    private fun parseAdJson(string: String){
        try {
            val jsonObject = JSONObject(string)
            LimitNumManager.setMax(jsonObject.optInt("max_click"),jsonObject.optInt("max_show"))
            MMKV.defaultMMKV().encode("maSdia",string)
        }catch (e:Exception){

        }
    }

    fun getAdStr():String{
        val s = MMKV.defaultMMKV().decodeString("maSdia") ?: ""
        if (s.isEmpty()) return Local.localAd
        return s
    }
}