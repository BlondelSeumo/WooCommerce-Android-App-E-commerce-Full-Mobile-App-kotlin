package com.iqonic.woobox.fragments

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.utils.extensions.color


abstract class BaseFragment : Fragment(), View.OnFocusChangeListener {

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus) {
            (v as EditText).setTextColor(activity!!.color(R.color.colorPrimaryDark))
            v.background = activity!!.getDrawable(R.drawable.bg_ractangle_rounded_active)
        } else {
            (v as EditText).setTextColor(activity!!.color(R.color.textColorPrimary))
            v.background = activity!!.getDrawable(R.drawable.bg_ractangle_rounded_inactive)
        }
    }

    fun hideProgress() {
        if (activity != null)
            (activity!! as AppBaseActivity).showProgress(false)
    }

    fun showProgress() {
        if (activity != null)
            (activity!! as AppBaseActivity).showProgress(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    object biggerDotTranformation : PasswordTransformationMethod() {

        override fun getTransformation(source: CharSequence, view: View): CharSequence {
            return PasswordCharSequence(super.getTransformation(source, view))
        }

        private class PasswordCharSequence(val transformation: CharSequence) : CharSequence by transformation {
            override fun get(index: Int): Char = if (transformation[index] == DOT) {
                BIGGER_DOT
            } else {
                transformation[index]
            }
        }

        private const val DOT = '\u2022'
        private const val BIGGER_DOT = '●'
    }
}