package com.demo.call.util

import com.tencent.mmkv.MMKV
import java.text.SimpleDateFormat
import java.util.*

object LimitNumManager {
    private var click=0
    private var show=0

    var maxClick=15
    var maxShow=50

    private val refreshMap= hashMapOf<String,Boolean>()

    fun checkCanRefresh(type:String)=refreshMap[type]?:true

    fun setRefreshBool(type: String,boolean: Boolean){
        refreshMap[type]=boolean
    }

    fun resetAllBool(){
        refreshMap.keys.forEach { refreshMap[it]=true }
    }

    fun setMax(click:Int,show:Int){
        maxClick=click
        maxShow=show
    }

    fun limit() = click > maxClick||show > maxShow

    fun addClick(){
        click++
        MMKV.defaultMMKV().encode(numKey("call_click"), click)
    }

    fun addShow(){
        show++
        MMKV.defaultMMKV().encode(numKey("call_show"), show)
    }

    fun readLocalNum(){
        click= MMKV.defaultMMKV().decodeInt(numKey("call_click"),0)
        show= MMKV.defaultMMKV().decodeInt(numKey("call_show"),0)
    }

    private fun numKey(key:String)="${SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis()))}_$key"
}