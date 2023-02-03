package com.demo.call.ac

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.demo.call.R
import com.demo.call.base.BaseAc
import com.demo.call.dialog.LoadingDialog
import com.demo.call.util.AcRegister
import com.demo.call.util.getBgResByIndex
import com.demo.call.util.hasOverlayPermission
import com.demo.call.util.toast
import com.tencent.mmkv.MMKV
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_apply.*

class ApplyAc:BaseAc() {
    private var index=0
    override fun layoutId(): Int = R.layout.activity_apply

    override fun onView() {
        immersionBar.statusBarView(top).init()
        index=intent.getIntExtra("index",0)
        root_layout.setBackgroundResource(getBgResByIndex(index))

        updateApplyBtn()
        iv_back.setOnClickListener { finish() }
        llc_apply.setOnClickListener { apply() }
    }

    private fun apply(){
        if (MMKV.defaultMMKV().decodeInt("index",-1)==index){
            return
        }
        if (!hasOverlayPermission(this)){
            AcRegister.banHotLoad=true
            val intent= Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${this.packageName}"))
            startActivityForResult(intent, 101)
            return
        }
        AndPermission.with(this)
            .runtime()
            .permission(
                Permission.READ_PHONE_STATE,
                Permission.CALL_PHONE,
                Permission.ANSWER_PHONE_CALLS,
                Permission.READ_CALL_LOG,
            )
            .onGranted {
                LoadingDialog{
                    MMKV.defaultMMKV().encode("index",index)
                    updateApplyBtn()
                    startActivity(Intent(this,ApplySuccessAc::class.java).apply {
                        putExtra("index",index)
                    })
                }.show(supportFragmentManager,"LoadingDialog")
            }
            .onDenied {
                toast("Failed to obtain permissions")
            }
            .start()
    }

    private fun updateApplyBtn(){
        if (MMKV.defaultMMKV().decodeInt("index",-1)==index){
            tv_apply_text.text="Applied"
        }
    }

    override fun onResume() {
        super.onResume()
        AcRegister.banHotLoad=false
    }
}