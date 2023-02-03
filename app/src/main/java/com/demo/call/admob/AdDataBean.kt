package com.demo.call.admob

class AdDataBean(
    val maSdia_tty:String,
    val maSdia_iid:String,
    val maSdia_tyy:String,
    val maSdia_spo:Int,
) {

    override fun toString(): String {
        return "AdDataBean(maSdia_tty='$maSdia_tty', maSdia_iid='$maSdia_iid', maSdia_tyy='$maSdia_tyy', maSdia_spo=$maSdia_spo)"
    }
}