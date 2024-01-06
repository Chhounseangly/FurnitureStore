package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.CategoryByTabAdapter
import kh.edu.rupp.ite.furniturestore.databinding.ActivityProductByCategoryBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ObjectPayment
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.CategoriesViewModel
import kotlin.properties.Delegates

class ProductsByCategoryActivity :
    BaseActivity<ActivityProductByCategoryBinding>(ActivityProductByCategoryBinding::inflate) {

    private val categoriesViewModel: CategoriesViewModel by viewModels()
    private val lytTab: TabLayout by lazy { binding.lytTab }
    private val loadingLoadProducts: ShimmerFrameLayout by lazy { binding.productsSkeletonLoading }

    private var currentTabId: Int = 0

    private val actionBarView: View by lazy {
        showCustomActionBar(this, R.layout.activity_action_bar)
    }

    companion object {
        private const val EXTRA_TAB_ID = "tab_id"
        fun newIntent(context: Context, tabId: Int): Intent {
            val intent = Intent(context, ProductsByCategoryActivity::class.java)
            intent.putExtra(EXTRA_TAB_ID, tabId)
            return intent
        }
    }

    override fun initActions() {
        //show action bar
        actionBarView.apply {
            findViewById<TextView>(R.id.title_action_bar)?.apply {
                text = getString(R.string.categories_txt)
            }

            findViewById<ImageView>(R.id.backPrev)?.setOnClickListener {
                prevBack(it)
            }
        }



        categoriesViewModel.loadCategoryTypes()

        //assign prev tab id to currentTabId
        currentTabId = intent.getIntExtra(EXTRA_TAB_ID, 0)

    }

    override fun setupListeners() {

    }


    override fun setupObservers() {
        categoriesViewModel.categoryTypesData.observe(this) { it ->
            when (it.status) {
                Status.Processing -> showLoadingAnimation(loadingLoadProducts)
                Status.Success -> {
                    it.data?.let {
                        //setup adapter
                        val adapter = CategoryByTabAdapter(this, it.data)
                        binding.viewPager.adapter = adapter

                        // Find the position of the desired tab with the given id
                        val initialPosition = it.data.indexOfFirst { tab -> tab.id == currentTabId }

                        // Set the initial page to the position of the desired tab
                        binding.viewPager.setCurrentItem(initialPosition, false)
                        //set title of tab bar
                        TabLayoutMediator(lytTab, binding.viewPager) { tab, position ->
                            val data = it.data[position]
                            tab.text = data.name
                        }.attach()
                    }
                    hideLoadingAnimation(loadingLoadProducts)
                    binding.lytTab.visibility = View.VISIBLE
                }

                else -> {
                    hideLoadingAnimation(loadingLoadProducts)
                    binding.lytTab.visibility = View.VISIBLE
                }
            }
        }

    }
}
