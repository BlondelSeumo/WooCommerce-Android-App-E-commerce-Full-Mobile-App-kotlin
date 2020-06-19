package com.iqonic.woobox.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.base.BaseRecyclerAdapter
import com.iqonic.woobox.databinding.ItemBlogBinding
import com.iqonic.woobox.models.Blog
import com.iqonic.woobox.utils.Constants.KeyIntent.DATA
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.activity_blog.*
import kotlinx.android.synthetic.main.layout_nodata.*
import kotlinx.android.synthetic.main.toolbar.*

class BlogActivity : AppBaseActivity() {

    private var countLoadMore = 1
    private lateinit var mBlogAdapter: BaseRecyclerAdapter<Blog, ItemBlogBinding>
    private var mOrderData = ArrayList<Blog>()

    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)

        title = getString(R.string.lbl_blog)
        setToolbar(toolbar)

        mBlogAdapter = object : BaseRecyclerAdapter<Blog, ItemBlogBinding>() {
            override fun onItemLongClick(view: View, model: Blog, position: Int) {

            }

            override fun onItemClick(view: View, model: Blog, position: Int, dataBinding: ItemBlogBinding) {
                if (view.id == R.id.rlMainOrder) {
                    launchActivity<BlogDetailActivity>
                    {
                        putExtra(DATA, model)
                    }
                }
            }

            override val layoutResId: Int = R.layout.item_blog

            override fun onBindData(model: Blog, position: Int, dataBinding: ItemBlogBinding) {
                if (model.image!=null){
                    dataBinding.ivProduct.loadImageFromUrl(model.image)
                }
            }
        }

        val linearLayoutManager = LinearLayoutManager(this)
        rvBlog.layoutManager = linearLayoutManager

        rvBlog.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()

                val isLastPosition = countItem.minus(1) == lastVisiblePosition

                if (!isLoading && isLastPosition) {
                    isLoading = true

                    progressBar.show()
                    countLoadMore = countLoadMore.plus(1)
                    loadOrder(countLoadMore)
                }
            }
        })
        rvBlog.adapter = mBlogAdapter
        loadOrder(countLoadMore)

    }

    private fun loadOrder(page: Int) {
        progressBar.show()
        callApi(getRestApis().getBlogs(page), onApiSuccess = {
            isLoading = it.size != 10; mOrderData.addAll(it); progressBar.hide()

            if (mOrderData.size == 0) rlNoData.show() else rlNoData.hide()
            mBlogAdapter.addItems(it)

        }, onApiError = {
            progressBar.hide(); snackBarError(it)
        }, onNetworkError = {
            progressBar.hide(); noInternetSnackBar()
        })
    }
}