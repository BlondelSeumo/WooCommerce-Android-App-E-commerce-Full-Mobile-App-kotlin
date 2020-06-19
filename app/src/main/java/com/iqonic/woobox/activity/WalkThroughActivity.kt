package com.iqonic.woobox.activity

import android.os.Bundle
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.adapter.WalkAdapter
import com.iqonic.woobox.utils.CarouselEffectTransformer
import com.iqonic.woobox.utils.Constants.SharedPref.SHOW_SWIPE
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_walk_through.*

class WalkThroughActivity : AppBaseActivity() {
    private var mCount: Int? = null
    private var mHeading = arrayOf("Hi, We are Woobox!", "Most Unique Styles!", "Shop Till You Drop!")
    private val mSubHeading = arrayOf("We make around your city Affordable,\n easy and efficient.", "Shop the most trending fashion on the biggest shopping website.", "Grab the best seller pieces at bargain prices.")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk_through)

        init()
        val adapter = WalkAdapter()

        ViewPager.adapter = adapter

        dots.attachViewPager(ViewPager)
        dots.setDotDrawable(R.drawable.bg_circle_primary, R.drawable.black_dot)
        mCount = adapter.count

        btnStatShopping.onClick {
            getSharedPrefInstance().setValue(SHOW_SWIPE, true)
            launchActivityWithNewTask<DashBoardActivity>()
        }
        llSignIn.onClick {
            launchActivity<SignInUpActivity>()
        }
    }

    private fun init() {
        ViewPager.apply {
            clipChildren = false
            pageMargin = resources.getDimensionPixelOffset(R.dimen.spacing_small)
            offscreenPageLimit = 3
            setPageTransformer(false, CarouselEffectTransformer(this@WalkThroughActivity))
            offscreenPageLimit = 0

            onPageSelected { position: Int ->
                val animFadeIn = android.view.animation.AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
                tvHeading.startAnimation(animFadeIn)
                tvSubHeading.startAnimation(animFadeIn)
                tvHeading.text = mHeading[position]
                tvSubHeading.text = mSubHeading[position]
            }
        }
    }
}