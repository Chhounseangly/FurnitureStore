package kh.edu.rupp.ite.furniturestore.view.activity

import android.widget.ImageView
import android.widget.TextView
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
    private val categoriesViewModel = CategoriesViewModel()
    private lateinit var categoriesFragment: CategoriesFragment
    private var id = 0

    override fun bindUi() {
        //call method prev back
        prevBack()

        // Get data from previous activity
        val intent = intent
        id = intent.getIntExtra("id", 0)
        val titleTypeCate = findViewById<TextView>(R.id.titleTypeCate)

        // assign category title to appBar
        titleTypeCate.text = "Categories"

        // displayFragment(CategoriesFragment(id))
        val lytTab = findViewById<TabLayout>(R.id.lytTab)
        val loadingLoadProducts = findViewById<ShimmerFrameLayout>(R.id.loadingLoadProducts)
        categoriesViewModel.loadCategoryTypes()
        categoriesViewModel.categoryTypesData.observe(this) { it ->
            when (it.status) {
                Status.Processing -> showLoadingAnimation(loadingLoadProducts)
                Status.Success -> {
                    it.data?.let {
                        for (data in it) {
                            val tab = lytTab.newTab().setText(data.name).setId(data.id)
                            lytTab.addTab(tab)
                        }
                    }
                    hideLoadingAnimation(loadingLoadProducts)
                }

                else -> {
                    hideLoadingAnimation(loadingLoadProducts)
                }
            }
        }

        // Declare prevId as a property of your class
        var prevId: Int = 0

        // Set up TabLayout.OnTabSelectedListener if needed
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
                // Handle tab unselection
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselection if needed
            }
        })
    }

    override fun setupListeners() {

    }

    override fun setupObservers() {

    }

    override fun initActions() {

    }

    //method prev back
    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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