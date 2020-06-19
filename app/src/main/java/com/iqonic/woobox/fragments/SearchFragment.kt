package com.iqonic.woobox.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.activity.SignInUpActivity
import com.iqonic.woobox.base.BaseRecyclerAdapter
import com.iqonic.woobox.databinding.ItemNewestProductBinding
import com.iqonic.woobox.models.FilterProductRequest
import com.iqonic.woobox.models.ProductDataNew
import com.iqonic.woobox.models.RequestModel
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_color.view.*
import kotlinx.android.synthetic.main.layout_nodata.*
import java.util.*
import kotlin.collections.ArrayList

class SearchFragment : BaseFragment() {

    private lateinit var adapter: BaseRecyclerAdapter<ProductDataNew, ItemNewestProductBinding>
    private var productList = ArrayList<ProductDataNew>()
    private var searchList = ArrayList<ProductDataNew>()
    private var searchQuery = ""
    private var mPage = 1
    private var mSearchQuery = ""
    private val requestModel = RequestModel()
    var isLoading = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppBaseActivity).setToolbar(toolbar)

        adapter = getAdapter()

        searchView.onActionViewExpanded()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mPage = 1
                mSearchQuery = query!!
                requestModel.page = mPage; requestModel.text = mSearchQuery
                adapter.clearData()
                loadProducts(requestModel)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }
        })
        aSearch_rvSearch.adapter = adapter

        val linearLayoutManager = LinearLayoutManager(activity!!)
        aSearch_rvSearch.layoutManager = linearLayoutManager

        aSearch_rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()

                val isLastPosition = countItem.minus(1) == lastVisiblePosition

                if (!isLoading && isLastPosition) {
                    isLoading = true

                    mPage = mPage.plus(1)
                    requestModel.page = mPage; requestModel.text = mSearchQuery

                    loadProducts(requestModel)
                }
            }
        })
        dataNotFound()
        pbLoader.hide()
    }

    private fun filter(newText: String) {
        productList.clear()
        if (newText.toLowerCase(Locale.ENGLISH).isEmpty()) {
            productList.addAll(searchList)
        } else {
            for (data in searchList) {
                if (data.name?.toLowerCase(Locale.ENGLISH)!!.contains(newText.toLowerCase(Locale.ENGLISH))) {
                    productList.add(data)
                }
            }
        }
        if (productList.size > 0) dataFound() else dataNotFound()
        adapter.addItems(productList)
    }

    private fun dataNotFound() {
        rlNoData.show(); aSearch_rvSearch.hide()
    }

    private fun dataFound() {
        rlNoData.hide(); aSearch_rvSearch.show()
    }

    private fun loadProducts(requestModel : RequestModel) {
        pbLoader.show()
        callApi(getRestApis(false).searchProducts(request = requestModel), onApiSuccess = {
            if (activity == null) return@callApi
            pbLoader.hide(); productList.addAll(it); searchList.addAll(it); isLoading = it.size != 10
            if (activity != null) {
                rlNoData.hide(); aSearch_rvSearch.hide(); adapter.clearData()
                if (productList.isNotEmpty()) {
                    adapter.addMoreItems(productList)
                    filter(searchQuery)
                }
            }
        }, onApiError = {
            if (activity == null) return@callApi
            pbLoader.hide()
            activity?.snackBarError(it)
        }, onNetworkError = {
            if (activity == null) return@callApi
            pbLoader.hide()
            activity?.openLottieDialog {
                loadProducts(requestModel)
            }
        })
    }

    private fun getAdapter(): BaseRecyclerAdapter<ProductDataNew, ItemNewestProductBinding> {

        return object : BaseRecyclerAdapter<ProductDataNew, ItemNewestProductBinding>() {

            override fun onItemClick(view: View, model: ProductDataNew, position: Int, dataBinding: ItemNewestProductBinding) {
                when (view.id) {
                    R.id.ivlike -> {
                        if (isLoggedIn()) {
                            dataBinding.ivDislike.show()
                            dataBinding.ivlike.hide()

                            val requestModel = RequestModel(); requestModel.pro_id = model.pro_id.toString()
                            activity?.removeFromWishList(requestModel) {
                                if (it) {
                                    dataBinding.ivDislike.show()
                                    dataBinding.ivlike.hide()
                                } else {
                                    dataBinding.ivDislike.hide()
                                    dataBinding.ivlike.show()
                                }
                            }
                        } else {
                            activity?.launchActivity<SignInUpActivity>()
                        }
                    }
                    R.id.ivDislike -> {
                        if(isLoggedIn()) {
                            dataBinding.ivDislike.hide()
                            dataBinding.ivlike.show()

                            val requestModel = RequestModel(); requestModel.pro_id = model.pro_id.toString()
                            activity?.addToWishList(requestModel) {
                                if (it) {
                                    dataBinding.ivDislike.hide()
                                    dataBinding.ivlike.show()
                                } else {
                                    dataBinding.ivDislike.show()
                                    dataBinding.ivlike.hide()
                                }
                            }
                        } else {
                            activity?.launchActivity<SignInUpActivity>()
                        }
                    }
                    R.id.listProductRaw -> {
                        (activity as AppBaseActivity).showProductDetail(model)
                    }
                }
            }

            override val layoutResId: Int = R.layout.item_newest_product

            override fun onBindData(model: ProductDataNew, position: Int, dataBinding: ItemNewestProductBinding) {
                val mStringBuffer = StringBuilder()

                if (model.full != null) dataBinding.ivProduct.loadImageFromUrl(model.full)

                if (model.sale_price?.isNotEmpty()!!) {
                    dataBinding.tvProductPrice.text = model.sale_price.currencyFormat()
                } else {
                    dataBinding.tvProductPrice.text = model.price.toString().currencyFormat()
                }

                dataBinding.tvProductActualPrice.text = model.regular_price?.currencyFormat()
                dataBinding.tvProductActualPrice.applyStrike()
                dataBinding.llProductColor.removeAllViews()

                if (model.color != null && model.color?.isNotEmpty()!!) {
                    val colors = model.color?.split(",")
                    colors?.forEach {
                        try {
                            if (it.contains("#")) {
                                val view1: View = layoutInflater.inflate(R.layout.layout_color, dataBinding.llProductColor, false)

                                view1.ivColor.changeBackgroundTint(Color.parseColor(it.trim()))
                                dataBinding.llProductColor.addView(view1)
                            }
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                        }
                    }
                    dataBinding.llProductColor.visibility=View.VISIBLE
                    dataBinding.tvProductName.setLines(1)
                }else{
                    dataBinding.tvProductName.setLines(2)
                    dataBinding.llProductColor.visibility=View.GONE
                }

                if (model.size != null && model.size?.isNotEmpty()!!) {
                    val sizes = model.size?.split(",")
                    sizes?.forEach {
                        mStringBuffer.append("$it  ")
                    }
                    dataBinding.tvSize.text = mStringBuffer
                    dataBinding.tvSize.visibility=View.VISIBLE
                }else{
                    dataBinding.tvSize.visibility=View.INVISIBLE
                }
                if (!isExistInWishList(model)) {
                    dataBinding.ivDislike.show()
                    dataBinding.ivlike.hide()
                } else {
                    dataBinding.ivDislike.hide()
                    dataBinding.ivlike.show()
                }
            }

            override fun onItemLongClick(view: View, model: ProductDataNew, position: Int) {}
        }
    }
}