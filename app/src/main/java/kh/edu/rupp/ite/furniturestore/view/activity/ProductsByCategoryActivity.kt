package kh.edu.rupp.ite.furniturestore.view.activity

import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ActivityProductByCategoryBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.fragments.CategoriesFragment
import kh.edu.rupp.ite.furniturestore.viewmodel.CategoriesViewModel

class ProductsByCategoryActivity :
    BaseActivity<ActivityProductByCategoryBinding>(ActivityProductByCategoryBinding::inflate) {

    private val categoriesViewModel: CategoriesViewModel by viewModels()
    private val lytTab: TabLayout by lazy { binding.lytTab }
    private val loadingLoadProducts: ShimmerFrameLayout by lazy { binding.productsSkeletonLoading }

    override fun initActions() {
        // call method prev back
        prevBack(binding.backBtn)

        categoriesViewModel.loadCategoryTypes()
    }

    override fun setupListeners() {
        lytTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    if (!tab.isSelected) {
                        // Load and display the fragment only if the tab is not reselected
                        displayFragment(CategoriesFragment(tab.id))
                    } else displayFragment(CategoriesFragment(tab.id))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab un selection
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselection if needed
            }
        })
    }

    override fun setupObservers() {
        categoriesViewModel.categoryTypesData.observe(this) { it ->
            when (it.status) {
                Status.Processing -> {
                    showLoadingAnimation(loadingLoadProducts)
                }
                Status.Success -> {
                    it.data?.let {
                        for (data in it.data) {
                            val tab = lytTab.newTab().setText(data.name).setId(data.id)
                            lytTab.addTab(tab)
                        }
                        // set active tab
                        val position = intent.getIntExtra("position", 0)
                        val tab = lytTab.getTabAt(position)
                        if (tab != null) {
                            lytTab.selectTab(tab)
                        }
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

    private fun displayFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Hide all existing fragments
        for (existingFragment in fragmentManager.fragments) {
            // hide all existing fragments
            fragmentTransaction.hide(existingFragment)
            // set the lifecycle state of the fragment to STARTED
            fragmentTransaction.setMaxLifecycle(existingFragment, Lifecycle.State.STARTED)
        }

        // Check if the fragment is already added
        if (!fragment.isAdded) {
            // If not added, add it to the fragment container
            fragmentTransaction.add(R.id.lytFragmentCate, fragment)
        } else {
            // If already added, show it and set the lifecycle state to RESUMED
            fragmentTransaction.setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            fragmentTransaction.show(fragment)
        }

        // Add the transaction to the back stack
        // fragmentTransaction.addToBackStack(null)

        // Commit the transaction
        fragmentTransaction.commit()
    }
}
