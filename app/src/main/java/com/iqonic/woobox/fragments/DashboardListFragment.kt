package com.iqonic.woobox.fragments

import android.os.Bundle
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager

import com.iqonic.woobox.R

import com.iqonic.woobox.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iqonic.woobox.adapter.RecyclerViewAdapter
import com.iqonic.woobox.utils.Constants
import kotlinx.android.synthetic.main.dialog_dashboard_selection.*
import kotlinx.android.synthetic.main.item_dashboard.view.*


class DashboardListFragment : BottomSheetDialogFragment() {

    companion object {
        var  tag="DashboardList"
        fun newInstance(): DashboardListFragment = DashboardListFragment()
    }
     var list= arrayListOf<Int>(R.drawable.ic_dashboard1,R.drawable.ic_dashboard2);
    var position = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_dashboard_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDone.onClick {
            getSharedPrefInstance().setValue(Constants.SharedPref.KEY_DASHBOARD,position)
            dismiss()
        }
        position= getSharedPrefInstance().getIntValue(Constants.SharedPref.KEY_DASHBOARD,0)
        var dashboardAdapter = RecyclerViewAdapter(
            R.layout.item_dashboard,
            onBind = { view: View, _: Int, pos: Int ->
                view.ivDashboard.setImageResource(list[pos])
                view.cbDashboard.isChecked = if (pos == position) {
                    view.viewOverlay.show()
                    true
                } else {
                    view.viewOverlay.hide()
                    false
                }
            })
        dashboardAdapter.onItemClick = { pos: Int, _: View, _: Int ->
            position = pos
            dashboardAdapter.notifyDataSetChanged()
        }
        rvDashboard.apply {
            layoutManager = GridLayoutManager(activity, 2)
            adapter = dashboardAdapter
        }
        dashboardAdapter.addItems(list)
    }


}