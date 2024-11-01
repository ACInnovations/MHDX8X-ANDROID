/*
 *  @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 *   @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *  All Rights Reserved.
 *  Proprietary and confidential :  All information contained herein is, and remains
 *  the property of ToXSL Technologies Pvt. Ltd. and its partners.
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.dx8_exchange.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.dx8_exchange.R
import com.dx8_exchange.databinding.ActivitySplashBinding
import com.dx8_exchange.ui.base.BaseActivity
import com.dx8_exchange.utils.Const
import com.dx8_exchange.utils.ViewClickHandler
import com.dx8_exchange.utils.extensions.visibleView


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity(), ViewClickHandler {
    private var binding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        updateStatusBarColor(R.color.transparent, true)
        @Suppress("DEPRECATION")
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        init()
    }

    private fun init() {
        binding!!.getStartedTV.visibleView(true)
        binding!!.loginTV.visibleView(true)
        when {
            restFullClient!!.getLoginStatus() != null -> {
                binding!!.getStartedTV.visibleView(false)
                binding!!.loginTV.visibleView(false)
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ initFCM() }, Const.SPLASH_TIMEOUT.toLong())
            }
            else -> {
                binding!!.handleClick = this
            }
        }
    }
    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.getStartedTV -> {
                gotoLoginSignUpActivity(Const.REGISTER)
            }
            R.id.loginTV -> {
                gotoLoginSignUpActivity(Const.LOGIN)
            }
        }
    }
}
