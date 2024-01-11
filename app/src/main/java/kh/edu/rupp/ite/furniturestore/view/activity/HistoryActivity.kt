package kh.edu.rupp.ite.furniturestore.view.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.PorterDuff
import android.util.Log
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
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductIdModel


class HistoryActivity :
    BaseActivity<ActivityHistoryCartBinding>(ActivityHistoryCartBinding::inflate) {

    private val historyViewModel: HistoryViewModel by viewModels()
    private val loadingHistory: ShimmerFrameLayout by lazy { binding.loadingHistory }

    private lateinit var historyData: DynamicAdapter<HistoryModel, ViewHolderProductHistoryBinding>

    private val selectedAll by lazy { binding.selectedAll }
    private val selectedItemsTxt by lazy { binding.selectedItemsTxt }
    private val lytNoData by lazy { binding.lytNoData }
    private val loadingCircle by lazy { binding.loadingCircle }
    private val lytLoading by lazy { binding.lytLoading }



    private var isLongPressActivated = false
    private var checkAll = false
    private var selectedCheckboxes = 0 // Track the number of selected checkboxes

    private lateinit var deleteButton: ImageView
    private lateinit var cancelBtn: ImageView

    private val actionBarView: View by lazy {
        showCustomActionBar(this, R.layout.activity_action_bar)
    }

    override fun initActions() {
        deleteButton = actionBarView.findViewById(R.id.deleteButton)
        cancelBtn = actionBarView.findViewById(R.id.cancelBtn)
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
        historyViewModel.qtySumUp()

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
                        if (it.data.data.isEmpty()){
                            lytNoData.visibility = View.VISIBLE
                        }
                    }
                }

                Status.Failed -> hideLoadingAnimation(loadingHistory)
                else -> {}
            }
        }

        historyViewModel.resMessage.observe(this){
            when(it.status){
               Status.Processing ->{

               }
               Status.Success -> {
                   hideCircleLoading(lytLoading, loadingCircle)
                   historyViewModel.loadHistoryData()
                   deleteButton.visibility = View.GONE
                   cancelBtn.visibility = View.GONE
                   selectedAll.visibility = View.GONE
                   selectedItemsTxt.visibility = View.GONE
               }
               Status.Failed->{}
               else -> {

               }
           }
        }

//        //display qty that sum up all quantity of history product
//        historyViewModel.qtySumUpProducts.observe(this){
//            // Set the total sum to the TextView with ID qty_item_his outside the adapter loop
//            actionBarView.findViewById<TextView>(R.id.qty_item_his).apply {
//                text = getString(R.string.quantity_txt, it.toString())
//                visibility = View.VISIBLE
//            }
//        }
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


        val listProductId: MutableList<ProductIdModel> = mutableListOf()

        historyData =
            DynamicAdapter(ViewHolderProductHistoryBinding::inflate) { view, item, binding ->
//            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked) {
//                    selectedCheckboxes++
//                    selectedItemsTxt.text = getString(R.string.selectedItems, selectedCheckboxes)
//                } else{
//                    if (selectedCheckboxes > 0){
//                        selectedCheckboxes--
//                        selectedItemsTxt.text = getString(R.string.selectedItems, selectedCheckboxes)
//                    }
//                }
//            }
                with(binding) {
                    Picasso.get().load(item.product.imageUrl)
                        .placeholder(loadingImg(this@HistoryActivity))
                        .error(R.drawable.ic_error)
                        .into(img)

                    txtName.text = item.product.name
                    priceTxt.text = getString(R.string.price_txt, item.product.price.toString())
                    qtyTxt.text = getString(R.string.quantity_txt, item.qty.toString())
                    date.text = item.updated_at

                    checkbox.apply {
                        visibility = if (isLongPressActivated) View.VISIBLE else View.GONE
                        isChecked = checkAll
                        if (isChecked){
                            //passing item all from recyclerview to this mutable list
                            listProductId.addAll(listOf(ProductIdModel(item.product_id)))
                        }else  {
                            // If the checkbox is not checked, remove the item from the list
                            val productIdModel = ProductIdModel(item.product_id)
                            listProductId.remove(productIdModel)
                        }
                    }

                    root.setOnLongClickListener {
                        isLongPressActivated = true
                        setVisibilityForLongPress(true)
                        true
                    }

                    //handle cancel checkbox
                    cancelBtn.setOnClickListener {
                        isLongPressActivated = false
                        selectedItemsTxt.visibility = View.GONE
                        checkAll = false
                        setVisibilityForLongPress(false)
                    }

                    selectedAll.setOnCheckedChangeListener { _, isChecked ->
                        checkAll = isChecked
                        selectedCheckboxes = if (isChecked) historyData.itemCount else 0
                        selectedItemsTxt.text = getString(R.string.selectedItems, selectedCheckboxes)
                        historyData.notifyDataSetChanged()
                    }

                    val selectedVisibility = if (selectedCheckboxes > 0) View.VISIBLE else View.GONE
                    deleteButton.visibility = selectedVisibility
                    selectedItemsTxt.visibility = selectedVisibility

                    deleteButton.setColorFilter(
                        ContextCompat.getColor(
                            this@HistoryActivity,
                            R.color.red
                        ), PorterDuff.Mode.SRC_IN
                    )
                }
            }

        //handle click button deleted products
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(listProductId)
        }

        //passing data to adapter
        historyData.setData(data)
        recyclerViewHistory.adapter = historyData
    }

    private fun showDeleteConfirmationDialog(listProductId: List<ProductIdModel>) {
        MaterialAlertDialogBuilder(this,
            R.style.DialogColor)
            .setTitle(resources.getString(R.string.cof_delete_txt))
            .setMessage(resources.getString(R.string.msg_delete))
            .setNegativeButton(resources.getString(R.string.no_txt)) { dialog, which ->

            }
            .setPositiveButton(resources.getString(R.string.yes_txt)) { dialog, which ->
                historyViewModel.deleteProductFromHis(listProductId)
                showCircleLoading(lytLoading, loadingCircle)
            }
            .show()
            .apply {
                // Set color for positive button
                getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this@HistoryActivity, R.color.red))
                // Set color for negative button
                getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this@HistoryActivity, R.color.black))
            }
    }


    //function show and hide of when pressing recycler view
    private fun setVisibilityForLongPress(isLongPress: Boolean) {
        cancelBtn.visibility = if (isLongPress) View.VISIBLE else View.GONE
        selectedAll.visibility = if (isLongPress) View.VISIBLE else View.GONE
        historyData.notifyDataSetChanged()
    }


}
