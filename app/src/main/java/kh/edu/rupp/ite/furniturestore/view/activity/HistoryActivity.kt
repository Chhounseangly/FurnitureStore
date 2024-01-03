package kh.edu.rupp.ite.furniturestore.view.activity

import android.annotation.SuppressLint
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.ActivityHistoryCartBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductHistoryBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.HistoryModel
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.HistoryViewModel

class HistoryActivity :
    BaseActivity<ActivityHistoryCartBinding>(ActivityHistoryCartBinding::inflate) {

    private val historyViewModel: HistoryViewModel by viewModels()
    private val loadingHistory: ShimmerFrameLayout by lazy { binding.loadingHistory }

    override fun initActions() {
        historyViewModel.loadHistoryData()

        //handle back to prev activity
        prevBack(binding.backBtn)
    }

    override fun setupListeners() {

    }

    override fun setupObservers() {
        historyViewModel.historyData.observe(this) {
            when (it.status) {
                Status.Processing -> showLoadingAnimation(loadingHistory)

                Status.Success -> {
                    hideLoadingAnimation(loadingHistory)
                    if (it.data != null) {
                        displayHistoryData(it.data.data)
                    }
                }
                Status.Failed -> hideLoadingAnimation(loadingHistory)
                else -> {}
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun displayHistoryData(data: List<HistoryModel>) {
        val recyclerViewHistory = findViewById<RecyclerView>(R.id.recyclerHistory)
        val linearLayout =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewHistory.layoutManager = linearLayout
        // Add a divider using ItemDecoration
        val dividerItemDecoration =
            DividerItemDecoration(recyclerViewHistory.context, DividerItemDecoration.VERTICAL)
        recyclerViewHistory.addItemDecoration(dividerItemDecoration)
        val historyData = DynamicAdapter<HistoryModel, ViewHolderProductHistoryBinding>(
            ViewHolderProductHistoryBinding::inflate
        ) { _, item, binding ->

            with(binding) {
                Picasso.get().load(item.product.imageUrl)
                    .placeholder(loadingImg(this@HistoryActivity))
                    .error(R.drawable.ic_error)
                    .into(img)

                txtName.text = item.product.name
                priceTxt.text = getString(R.string.price_txt, item.product.price.toString())
                qtyTxt.text = getString(R.string.quantity_txt, item.qty)
                date.text = item.updated_at

            }
        }
        historyData.setData(data)
        recyclerViewHistory.adapter = historyData
    }
}
