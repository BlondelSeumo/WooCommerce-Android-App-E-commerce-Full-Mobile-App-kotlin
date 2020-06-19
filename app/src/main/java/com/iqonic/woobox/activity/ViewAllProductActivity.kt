package com.iqonic.woobox.activity

import android.os.Bundle
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.fragments.ViewAllProductFragment
import com.iqonic.woobox.utils.Constants
import com.iqonic.woobox.utils.Constants.AppBroadcasts.CART_COUNT_CHANGE
import com.iqonic.woobox.utils.extensions.BroadcastReceiverExt
import com.iqonic.woobox.utils.extensions.replaceFragment
import kotlinx.android.synthetic.main.toolbar.*

class ViewAllProductActivity : AppBaseActivity() {

    private var mFragment: ViewAllProductFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all)
        setToolbar(toolbar)

        title = intent.getStringExtra(Constants.KeyIntent.TITLE)
        val mViewAllId = intent.getIntExtra(Constants.KeyIntent.VIEWALLID, 0)
        val mCategoryId = intent.getIntExtra(Constants.KeyIntent.KEYID, -1)

        mFragment = ViewAllProductFragment.getNewInstance(mViewAllId, mCategoryId)
        replaceFragment(mFragment!!, R.id.fragmentContainer)
        BroadcastReceiverExt(this) { onAction(CART_COUNT_CHANGE) { mFragment?.setCartCount() } }
        showBannerAds()
    }
}