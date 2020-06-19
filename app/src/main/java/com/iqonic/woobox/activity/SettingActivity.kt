package com.iqonic.woobox.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.WooBoxApp
import com.iqonic.woobox.adapter.RecyclerViewAdapter
import com.iqonic.woobox.fragments.DashboardListFragment
import com.iqonic.woobox.utils.Constants
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.dialog_launguage_selection.*
import kotlinx.android.synthetic.main.spinner_language.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.system.exitProcess

class SettingActivity : AppBaseActivity() {
    private lateinit var lan: String
    private var codes = arrayOf(
            "en",
            "hi",
            "fr",
            "es",
            "de",
            "in",
            "af",
            "pt",
            "tr",
            "ar",
            "vi"
    )
    private var mCountryImg = intArrayOf(
            R.drawable.us,
            R.drawable.india,
            R.drawable.france,
            R.drawable.spain,
            R.drawable.germany,
            R.drawable.indonesia,
            R.drawable.south_africa,
            R.drawable.portugal,
            R.drawable.turkey,
            R.drawable.ar,
            R.drawable.vietnam

    )

    private var mIsSelectedByUser = false
    private var mDashboardListFragment: DashboardListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.title_setting)
        setToolbar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        lan = WooBoxApp.language
        val languages = resources.getStringArray(R.array.language)
        switchNightMode.isChecked = WooBoxApp.appTheme == Constants.THEME.DARK


        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.dialog_launguage_selection)
        val languageAdapter = RecyclerViewAdapter<String>(R.layout.spinner_language, onBind = { view: View, s: String, i: Int ->
            view.ivLogo.setImageResource(mCountryImg[i])
            view.tvName.text = languages[i]
        })
        languageAdapter.onItemClick = { i: Int, view: View, s: String ->
            ivLanguage.loadImageFromDrawable(mCountryImg[i])
            tvLanguage.text = languages[i]
            dialog.dismiss()
            setNewLocale(codes[i])
        }
        dialog.listLanguage.apply {
            setVerticalLayout()
            adapter = languageAdapter
        }
        languageAdapter.addItems(languages.toCollection(ArrayList()))
        llLanguage.onClick {
            dialog.show()
        }
        llDashboard.onClick {
            if (mDashboardListFragment == null) {
                mDashboardListFragment = DashboardListFragment.newInstance()
            }
             if (mDashboardListFragment?.isAdded!!){
                 return@onClick
             }
            mDashboardListFragment?.show(supportFragmentManager, DashboardListFragment.tag)
        }
        codes.forEachIndexed { i: Int, s: String ->
            if (lan == s) {
                ivLanguage.loadImageFromDrawable(mCountryImg[i])
                tvLanguage.text = languages[i]
            }
        }
        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            WooBoxApp.getAppInstance().enableNotification(isChecked)
        }
        switchNightMode.setOnCheckedChangeListener { _, isChecked ->
            WooBoxApp.changeAppTheme(isChecked)
            switchToDarkMode(isChecked)
        }
        Handler().postDelayed({ mIsSelectedByUser = true }, 1000)

    }

    override fun onBackPressed() {
        if (lan != WooBoxApp.language) {
            launchActivityWithNewTask<DashBoardActivity>()
            exitProcess(0)
        } else {
            super.onBackPressed()
        }
    }

    private fun setNewLocale(language: String) {
        WooBoxApp.changeLanguage(language)
        Log.e("lan", language)
        if (lan != language) {
            recreate()
            setResult(Activity.RESULT_OK)
        }
    }
}


