package com.iqonic.woobox.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.iqonic.woobox.BR
import java.util.*

abstract class IExpandableListAdapter<G, C, GV : ViewDataBinding, CV : ViewDataBinding> : BaseExpandableListAdapter {

    private var context: Context? = null
    private var mGroupItems: ArrayList<G> = ArrayList()
    private var mChildItemsMap = HashMap<G, ArrayList<C>>()

    /*get child layout xml file*/
    abstract val childItemResId: Int

    /*get group layout xml file*/
    abstract val groupItemResId: Int

    override fun getGroupCount(): Int = mGroupItems.size

    constructor(context: Context) {
        this.context = context
    }

    protected constructor(
        context: Context, mGroupItems: ArrayList<G>,
        mChildItemsMap: HashMap<G, ArrayList<C>>
    ) {
        this.context = context
        addExpandableItems(mGroupItems, mChildItemsMap)
    }


    /* set custom data to child layout*/
    abstract fun bindChildView(view: CV, childObject: C, groupPosition: Int, childPosition: Int): CV

    /* set custom data to group layout*/
    abstract fun bindGroupView(view: GV, groupObject: G, groupPosition: Int): GV


    override fun getChild(groupPosition: Int, childPosition: Int): C {
        return getChildItems(groupPosition)!![childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        var binding: ViewDataBinding = if (convertView == null) {
            DataBindingUtil.inflate(inflater(), childItemResId, parent, false) as CV
        } else {
            convertView.tag as ViewDataBinding
        }
        binding.setVariable(BR.model, getChild(groupPosition, childPosition))
        binding.root.tag = binding

        return bindChildView(binding as CV, getChild(groupPosition, childPosition), groupPosition, childPosition).root
    }

    private fun getViewBinding(view: View?, itemResId: Int, parent: ViewGroup): ViewDataBinding {
        return DataBindingUtil.inflate(inflater(), itemResId, parent, false) as ViewDataBinding

/*
        return if (view == null) {
            DataBindingUtil.inflate(inflater(), itemResId, parent, false) as ViewDataBinding?
        }else{
            view.tag as ViewDataBinding?
        }
*/
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return getChildItems(groupPosition)!!.size

    }

    override fun getGroup(groupPosition: Int): G {
        return mGroupItems[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var binding: ViewDataBinding = if (convertView == null) {
            DataBindingUtil.inflate(inflater(), groupItemResId, parent, false) as CV
        } else {
            convertView.tag as ViewDataBinding
        }
        binding.setVariable(BR.model, getGroup(groupPosition))
        binding.root.tag = binding
        return bindGroupView(binding as GV, getGroup(groupPosition), groupPosition).root
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

/*
    private fun inflateView(resId: Int): View {
        return inflater().inflate(resId, null)
    }
*/

    private fun inflater(): LayoutInflater {
        return (context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
    }


    private fun getChildItems(groupPosition: Int): ArrayList<C>? {
        return if (mChildItemsMap[mGroupItems[groupPosition]] != null) {
            mChildItemsMap[mGroupItems[groupPosition]]
        } else ArrayList()
    }

    fun addExpandableItems(mGroupItems: ArrayList<G>, mChildItemsMap: HashMap<G, ArrayList<C>>) {
        this.mGroupItems = mGroupItems
        this.mChildItemsMap = mChildItemsMap
        notifyDataSetChanged()
    }

    fun removeAllItems() {
        this.mGroupItems.clear()
        this.mChildItemsMap.clear()
        notifyDataSetChanged()
    }

    fun removeGroupItem(groupPosition: Int) {
        if (groupPosition < 0 || groupPosition >= mGroupItems.size) {
            return
        }
        this.mChildItemsMap.remove(mGroupItems[groupPosition])
        this.mGroupItems.removeAt(groupPosition)
        notifyDataSetChanged()
    }

    fun removeChildItem(groupPosition: Int, childPosition: Int) {
        if (groupPosition < 0 || groupPosition >= mGroupItems.size) {
            return
        }
        if (childPosition < 0 || childPosition >= getChildItems(groupPosition)!!.size) {
            return
        }
        getChildItems(groupPosition)!!.removeAt(childPosition)
        notifyDataSetChanged()
    }


}
