package kh.edu.rupp.ite.furniturestore.view.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductHistoryBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.HistoryModel
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.HistoryViewModel


class HistoryActivity : AppCompatActivity() {

    private val historyViewModel: HistoryViewModel by viewModels()
    private lateinit var lytLoading: View
    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_cart)
        lytLoading  = findViewById(R.id.ltyLoading)
        loading = findViewById(R.id.loadingCircle)

        historyViewModel.historyData.observe(this) {
            when (it.status) {
                Status.Processing -> {
                    showLoading(lytLoading , loading)
                }
                Status.Success -> {
                    hideLoading(lytLoading , loading)
                    if (it.data != null) {
                        displayHistoryData(it.data)
                    }
                }
                Status.Failed -> {
                    hideLoading(lytLoading , loading)
                }

                else -> {}
            }
        }
        historyViewModel.loadHistoryData()

        //handle back to prev activity
        prevBack()
    }

    fun showLoading(lytLoading: View, loading: ProgressBar ){
        lytLoading.visibility = View.VISIBLE
        loading.visibility = View.VISIBLE
    }
    fun hideLoading(ltyLoading: View, loading: ProgressBar){
        lytLoading.visibility = View.GONE
        loading.visibility = View.GONE
    }

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
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_error)
                    .into(img)

                txtName.text = item.product.name
                priceTxt.text = item.product.price.toString()
                qtyTxt.text = item.qty.toString()
                date.text = item.updated_at

            }
        }
        historyData.setData(data)
        recyclerViewHistory.adapter = historyData


    }

    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}