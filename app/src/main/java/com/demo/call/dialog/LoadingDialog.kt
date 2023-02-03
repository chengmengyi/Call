package com.demo.call.dialog

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.demo.call.R
import com.demo.call.base.BaseDialog
import kotlinx.android.synthetic.main.loading_dialog.*

class LoadingDialog(private val complete:()->Unit):BaseDialog() {
    private var animator: ValueAnimator?=null

    override fun layoutId(): Int = R.layout.loading_dialog

    override fun onView() {
        startAnimator()
    }

    private fun startAnimator(){
        animator=ValueAnimator.ofInt(0, 100).apply {
            duration = 3000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                loading_progress.progress = progress
                tv_Loading.text="Applying…$progress%"
            }
            doOnEnd {
                dismiss()
                complete.invoke()
            }
            start()
        }
    }


    private fun stopAnimator(){
        animator?.removeAllUpdateListeners()
        animator?.cancel()
        animator=null
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