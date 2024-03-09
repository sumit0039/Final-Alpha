package com.softwill.alpha.dashboard

import android.view.View
import android.widget.FrameLayout

object LoaderUtil {
    fun showLoader(frameLayout: FrameLayout, loader: View) {
        frameLayout.addView(loader)
    }

    fun hideLoader(frameLayout: FrameLayout, loader: View) {
        frameLayout.removeView(loader)
    }
}
