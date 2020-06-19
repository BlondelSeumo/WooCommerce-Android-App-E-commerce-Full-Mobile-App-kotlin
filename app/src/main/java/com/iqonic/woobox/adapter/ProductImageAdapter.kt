package com.iqonic.woobox.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.iqonic.woobox.R
import com.iqonic.woobox.utils.extensions.loadImageFromUrl
import kotlinx.android.synthetic.main.layout_itemimage.view.*

class ProductImageAdapter(private var mImg: ArrayList<String>) : PagerAdapter() {

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_itemimage, parent, false)

        view.imgSlider.loadImageFromUrl(mImg.get(position))

        parent.addView(view)
        return view
    }

    override fun isViewFromObject(v: View, `object`: Any): Boolean = v === `object` as View

    override fun getCount(): Int = mImg!!.size

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) = parent.removeView(`object` as View)

}