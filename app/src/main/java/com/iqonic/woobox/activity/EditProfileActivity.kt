package com.iqonic.woobox.activity

import android.os.Bundle
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.fragments.ProfileFragment
import com.iqonic.woobox.utils.extensions.addFragment
import kotlinx.android.synthetic.main.toolbar.*

class EditProfileActivity : AppBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setToolbar(toolbar)
        title=getString(R.string.lbl_edit_profile)
        addFragment(ProfileFragment(),R.id.profilecontainer)
    }
}
