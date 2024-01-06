package kh.edu.rupp.ite.furniturestore.view.activity

import android.annotation.SuppressLint
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import androidx.appcompat.view.ActionMode


class HistoryActivity :
    BaseActivity<ActivityHistoryCartBinding>(ActivityHistoryCartBinding::inflate) {

    private val historyViewModel: HistoryViewModel by viewModels()
    private val loadingHistory: ShimmerFrameLayout by lazy { binding.loadingHistory }
    private var myActMode: ActionMode? = null

    private val actionBarView: View by lazy {
        showCustomActionBar(this, R.layout.activity_action_bar)
    }
    override fun initActions() {
        //show action bar
        actionBarView.apply {
            findViewById<TextView>(R.id.title_action_bar)?.apply {
                text = getString(R.string.purchase_history)
            }

            findViewById<ImageView>(R.id.backPrev)?.setOnClickListener {
                prevBack(it)
            }
        }


        historyViewModel.loadHistoryData()

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
                lytTxt.setOnClickListener {
                    startSupportActionMode(callback)
                }
                txtName.text = item.product.name
                priceTxt.text = getString(R.string.price_txt, item.product.price.toString())
                qtyTxt.text = getString(R.string.quantity_txt, item.qty)
                date.text = item.updated_at

            }
        }
        historyData.setData(data)
        recyclerViewHistory.adapter = historyData
    }
    private val callback: ActionMode.Callback = object : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.contextual_action_bar, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.delete -> {
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            myActMode = null
        }
    }


}
