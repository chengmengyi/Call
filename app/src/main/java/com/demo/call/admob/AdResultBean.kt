package com.demo.call.admob

class AdResultBean(
    val loadTime:Long=0L,
    val loadAd:Any?=null
) {
    fun expired()=(System.currentTimeMillis() - loadTime) >=3600000L
}