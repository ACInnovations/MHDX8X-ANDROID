/*
 *  @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 *   @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *  All Rights Reserved.
 *  Proprietary and confidential :  All information contained herein is, and remains
 *  the property of ToXSL Technologies Pvt. Ltd. and its partners.
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.dx8_exchange.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.dx8_exchange.R
import com.dx8_exchange.adapter.ViewPagerAdapter
import com.dx8_exchange.databinding.ActivityLoginBinding
import com.dx8_exchange.ui.base.BaseActivity
import com.dx8_exchange.ui.fragment.WelcomeOneFragment
import com.dx8_exchange.ui.fragment.WelcomeThreeFragment
import com.dx8_exchange.ui.fragment.WelcomeTwoFragment
import com.dx8_exchange.ui.fragment.authentication.CreatePasswordFragment
import com.dx8_exchange.ui.fragment.authentication.LoginFragment
import com.dx8_exchange.ui.fragment.authentication.RegistrationCompletedFragment
import com.dx8_exchange.utils.Const
import com.dx8_exchange.utils.extensions.replaceFragment


class LoginSignUpActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    var value: Int = 0
    private var viewPagerAdapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        updateStatusBarColor(R.color.deepBlack, true)
        initToolBar()
        onBackPressedDispatcherHandling()
        val extras = intent.extras
        if (extras != null) {
            value = extras.getInt("keyValue")
            when (value) {
                Const.REGISTER-> {
                    gotoLoginFragment(Const.REGISTER)
                }
               Const.LOGIN -> {
                    gotoLoginFragment(Const.LOGIN)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolBar() {

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun gotoLoginFragment(str: Int) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        when (str) {
            Const.REGISTER -> {
                initViewPager()
            }
            Const.LOGIN -> replaceFragment(LoginFragment())
        }
    }

    private fun initViewPager() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter!!.add(WelcomeOneFragment())
        viewPagerAdapter!!.add(WelcomeTwoFragment())
        viewPagerAdapter!!.add(WelcomeThreeFragment())
        binding.viewpager.adapter = viewPagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        when (fragment) {
            is WelcomeOneFragment, is WelcomeTwoFragment, is WelcomeThreeFragment -> {
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                setToolBar(isShowToolbar = false)
            }
            is LoginFragment, is RegistrationCompletedFragment -> {
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                setToolBar(isShowToolbar = false)
            }
            else -> {
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setHomeAsUpIndicator(R.mipmap.ic_back)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun onBackPressedDispatcherHandling() {
        when {
            Build.VERSION.SDK_INT >= 33 -> {
                onBackInvokedDispatcher.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT
                ) {
                    onBackPress()
                }
            }
            else -> {
                onBackPressedDispatcher.addCallback(
                    this,
                    object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            onBackPress()
                        }
                    })
            }
        }
    }

    fun onBackPress() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        val onboardingFragment = supportFragmentManager.findFragmentById(R.id.viewpager)
        if (onboardingFragment is WelcomeOneFragment || onboardingFragment is WelcomeTwoFragment || onboardingFragment is WelcomeThreeFragment) {
            goToSplash()
        }
        when {
            fragment is LoginFragment || fragment is CreatePasswordFragment -> {
                goToSplash()
            }
            supportFragmentManager.backStackEntryCount > 0 -> {
                supportFragmentManager.popBackStack()
            }
        }
    }
    private fun goToSplash() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }

    fun setCurrentFragment() {
       binding.viewpager.setCurrentItem(binding.viewpager.currentItem + 1, true)
    }
}
