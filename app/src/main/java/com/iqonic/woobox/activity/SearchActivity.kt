package com.iqonic.woobox.activity

import android.os.Bundle
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.fragments.SearchFragment
import com.iqonic.woobox.utils.extensions.addFragment

class SearchActivity : AppBaseActivity() {

    private val mSearchFragment = SearchFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search2)
        addFragment(mSearchFragment, R.id.fragmentContainer)
    }

}