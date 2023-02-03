package com.demo.call.ac

import android.animation.ValueAnimator
import android.content.Intent
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.blankj.utilcode.util.ActivityUtils
import com.demo.call.R
import com.demo.call.admob.LoadAdManager
import com.demo.call.admob.ShowOpenAd
import com.demo.call.base.BaseAc
import com.demo.call.conf.Local
import com.demo.call.util.LimitNumManager
import kotlinx.android.synthetic.main.activity_main.*

class MainAc : BaseAc() {
    private var animator:ValueAnimator?=null
    private val showOpenAd by lazy { ShowOpenAd(Local.OPEN) }

    override fun layoutId(): Int = R.layout.activity_main

    override fun onView() {
        LimitNumManager.readLocalNum()
        LimitNumManager.resetAllBool()
        LoadAdManager.preLoadAllAd()
        startAnimator()
    }

    private fun startAnimator(){
        animator=ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                launch_progress.progress = progress
                val pro = (10 * (progress / 100.0F)).toInt()
                if (pro in 2..9){
                    showOpenAd.showOpenAd(
                        this@MainAc,
                        showing = {
                            stopAnimator()
                            launch_progress.progress=100
                        },
                        closeAd = {
                            toHomeAc()
                        }
                    )
                }else if (pro>=10){
                    toHomeAc()
                }
            }
            start()
        }
    }

    private fun toHomeAc(){
        if (!ActivityUtils.isActivityExistsInStack(HomeAc::class.java)){
            startActivity(Intent(this,HomeAc::class.java))
        }
        finish()
    }

    private fun stopAnimator(){
        animator?.removeAllUpdateListeners()
        animator?.cancel()
        animator=null
    }
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        animator?.resume()
    }

    override fun onPause() {
        super.onPause()
        animator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAnimator()
    }
}