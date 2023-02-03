package com.demo.call.base

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar

abstract class BaseAc:AppCompatActivity() {
    var resume=false
    lateinit var immersionBar: ImmersionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        displayMetrics()
        setContentView(layoutId())
        immersionBar= ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(false)
            init()
        }
        onView()
    }

    abstract fun layoutId():Int

    abstract fun onView()

    private fun displayMetrics(){
        val metrics: DisplayMetrics = resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
    }

    override fun onResume() {
        super.onResume()
        resume=true
    }

    override fun onPause() {
        super.onPause()
        resume=false
    }

    override fun onStop() {
        super.onStop()
        resume=false
    }
}