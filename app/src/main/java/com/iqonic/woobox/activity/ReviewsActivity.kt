package com.iqonic.woobox.activity

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import com.iqonic.woobox.AppBaseActivity
import com.iqonic.woobox.R
import com.iqonic.woobox.base.BaseRecyclerAdapter
import com.iqonic.woobox.databinding.ItemReviewBinding
import com.iqonic.woobox.models.ProductDataNew
import com.iqonic.woobox.models.ProductReviewData
import com.iqonic.woobox.models.RequestModel
import com.iqonic.woobox.utils.Constants
import com.iqonic.woobox.utils.extensions.*
import kotlinx.android.synthetic.main.dialog_rate.*
import kotlinx.android.synthetic.main.fragment_itemreview.*
import kotlinx.android.synthetic.main.layout_nodata.*
import kotlinx.android.synthetic.main.toolbar.*

class ReviewsActivity : AppBaseActivity() {
    private  var mProductId: Int=0
    private lateinit var mProductReviewData: ProductReviewData
    private val mReviewAdapter = object : BaseRecyclerAdapter<ProductReviewData, ItemReviewBinding>() {
        override fun onItemLongClick(view: View, model: ProductReviewData, position: Int) {}

        override fun onItemClick(view: View, model: ProductReviewData, position: Int, dataBinding: ItemReviewBinding) {
            mProductReviewData = model
            if (view.id == R.id.ivMenu) {
                val popup = PopupMenu(this@ReviewsActivity, view)
                popup.menuInflater.inflate(R.menu.menu_review, popup.menu)
                popup.setOnMenuItemClickListener { item ->
                    when (item!!.itemId) {
                        R.id.nav_delete -> confirmDialog()

                        R.id.nav_update -> updateReview()
                    }
                    true
                }
                popup.show()
            }
        }

        override val layoutResId: Int = R.layout.item_review

        override fun onBindData(model: ProductReviewData, position: Int, dataBinding: ItemReviewBinding) {
            mProductReviewData = model
            dataBinding.tvProductReviewRating.text = model.rating.toString()
            dataBinding.tvProductReviewSubHeading.text = model.review.getHtmlString()
            dataBinding.tvProductReviewCmt.text = model.name

            dataBinding.tvProductReviewDuration.text = toDate(model.date_created)
            if (model.rating == 1) {
                dataBinding.tvProductReviewRating.changeBackgroundTint(color(R.color.red))
            }
            if (model.rating == 2 || model.rating == 3) {
                dataBinding.tvProductReviewRating.changeBackgroundTint(color(R.color.yellow))
            }
            if (model.rating == 5 || model.rating == 4) {
                dataBinding.tvProductReviewRating.changeBackgroundTint(color(R.color.green))
            }
            if (isLoggedIn() && model.email == getEmail()) {
                dataBinding.ivMenu.show()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)
        setToolbar(toolbar)
        title = getString(R.string.lbl_reviews)
        mProductId = intent.getIntExtra(Constants.KeyIntent.PRODUCT_ID,0)
        ivBackground.setStrokedBackground(color(R.color.favourite_unselected_background), color(R.color.dots_color))
        rvReview.setVerticalLayout()
        rvReview.adapter = mReviewAdapter
        listProductReviews()
        tvMsg.text=getString(R.string.lbl_no_reviews)
        if (isLoggedIn()) {
            btnRateNow.show()
        }
        btnRateNow.onClick {
            createProductReview()
        }
        sb1Star.setOnTouchListener { v, event -> true }
        sb2Star.setOnTouchListener { v, event -> true }
        sb3Star.setOnTouchListener { v, event -> true }
        sb4Star.setOnTouchListener { v, event -> true }
        sb5Star.setOnTouchListener { v, event -> true }    }


    private fun setRating(data: List<ProductReviewData>) {
        if (data.isEmpty()) {
            return
        }
        var fiveStar = 0
        var fourStar = 0
        var threeStar = 0
        var twoStar = 0
        var oneStar = 0
        for (reviewModel in data) {
            when (reviewModel.rating) {
                5 -> fiveStar++
                4 -> fourStar++
                3 -> threeStar++
                2 -> twoStar++
                1 -> oneStar++
            }
        }

        if (fiveStar == 0 && fourStar == 0 && threeStar == 0 && twoStar == 0 && oneStar == 0) {
            return
        }
        sb1Star.max = data.size
        sb2Star.max = data.size
        sb3Star.max = data.size
        sb4Star.max = data.size
        sb5Star.max = data.size

        sb1Star.progress = oneStar
        sb2Star.progress = twoStar
        sb3Star.progress = threeStar
        sb4Star.progress = fourStar
        sb5Star.progress = fiveStar

        tvTotalReview.text = String.format("%d Reviews", data.size)
        tv5Count.text = fiveStar.toString()
        tv4Count.text = fourStar.toString()
        tv3Count.text = threeStar.toString()
        tv2Count.text = twoStar.toString()
        tv1Count.text = oneStar.toString()

        val mAvgRating = (5 * fiveStar + 4 * fourStar + 3 * threeStar + 2 * twoStar + 1 * oneStar) / (fiveStar + fourStar + threeStar + twoStar + oneStar)
        tvReviewRate.text = mAvgRating.toString()
    }
    private fun createProductReview() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(R.layout.dialog_rate)
        dialog.setCanceledOnTouchOutside(false)

        dialog.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        dialog.btnSubmit.onClick {
            val requestModel = RequestModel()
            requestModel.product_id = mProductId.toString()
            requestModel.reviewer = getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_FIRST_NAME, "")
            requestModel.reviewer_email = getSharedPrefInstance().getStringValue(Constants.SharedPref.USER_EMAIL, "")
            requestModel.review = dialog.edtReview.textToString()
            requestModel.rating = dialog.ratingBar.rating.toString()

            if (Constants.isAllowedToCreate) {
                showProgress(true)

                callApi(getRestApis().createProductReview(requestModel), onApiSuccess = {
                    showProgress(false)
                    toast("Successfully")
                    dialog.dismiss()
                    listProductReviews()
                }, onApiError = {
                    showProgress(false)
                    dialog.dismiss()
                    snackBarError(it)
                }, onNetworkError = {
                    showProgress(false)
                    dialog.dismiss()
                    noInternetSnackBar()
                })
            } else {
                toast(getString(R.string.msg_not_allowed))
            }
        }
        dialog.viewCloseDialog.onClick {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun confirmDialog() {
        getAlertDialog(getString(R.string.msg_confirmation), onPositiveClick = { dialog, i ->
            val requestModel = RequestModel()
            requestModel.force = true

            if (Constants.isAllowedToCreate) {

                showProgress(true)
                callApi(getRestApis().deleteProductReview(mProductReviewData.id, requestModel), onApiSuccess = {
                    showProgress(false)
                    snackBar(getString(R.string.success))
                    listProductReviews()
                }, onApiError = {
                    showProgress(false)
                    snackBarError(it)

                }, onNetworkError = {
                    openLottieDialog {
                        confirmDialog()
                    }
                    showProgress(false)
                })
            } else {
                toast(getString(R.string.msg_not_allowed))
            }
        }, onNegativeClick = { dialog, i ->
            dialog.dismiss()
        }).show()
    }

    fun updateReview() {
        val dialog = Dialog(this)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(R.layout.dialog_rate)

        dialog.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        dialog.edtReview.setText(mProductReviewData.review.getHtmlString())
        dialog.ratingBar.rating = mProductReviewData.rating.toFloat()

        dialog.viewCloseDialog.onClick {
            dialog.dismiss()
        }
        dialog.btnSubmit.onClick {
            val requestModel = RequestModel()

            requestModel.product_id = mProductId.toString()
            requestModel.reviewer = getFirstName()
            requestModel.reviewer_email = getEmail()
            requestModel.review = dialog.edtReview.textToString()
            requestModel.rating = dialog.ratingBar.rating.toString()

            if (Constants.isAllowedToCreate) {
                showProgress(true)
                callApi(getRestApis().updateProductReview(mProductReviewData.id, requestModel), onApiSuccess = {
                    showProgress(false)
                    snackBar(getString(R.string.update))
                    dialog.dismiss()
                    listProductReviews()
                }, onApiError = {
                    showProgress(false)
                    dialog.dismiss()
                    snackBarError(it)

                }, onNetworkError = {
                    showProgress(false)
                    dialog.dismiss()
                    noInternetSnackBar()
                })
            } else {
                toast(getString(R.string.msg_not_allowed))
            }
        }
        dialog.show()
    }

    private fun listProductReviews() {
        showProgress(true)
        callApi(getRestApis().listProductReviews(mProductId), onApiSuccess = {
            showProgress(false)
            if (it.isEmpty()) {
                showList(false)
            } else {
                showProgress(false)
                it.reverse()
                mReviewAdapter.addItems(it)
                setRating(it)
                showList(true)
            }
        }, onApiError = {
            showProgress(false)
            snackBarError(it)
        }, onNetworkError = {
            showProgress(false)
            openLottieDialog() {
                listProductReviews()
            }
        })
    }

    private fun showList(isVisible:Boolean) {
        if (isVisible){
            rlNoData.hide()
            rvReview.show()
        }else{
            rvReview.hide()
            rlNoData.show()

        }
    }

}
