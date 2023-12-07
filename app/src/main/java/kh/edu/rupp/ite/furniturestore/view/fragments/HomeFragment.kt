package kh.edu.rupp.ite.furniturestore.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.CategoryTypesAdapter
import kh.edu.rupp.ite.furniturestore.adapter.ProductListAdapter
import kh.edu.rupp.ite.furniturestore.custom_method.LoadingMethod
import kh.edu.rupp.ite.furniturestore.databinding.FragmentHomeBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductSlider
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.CategoriesViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.FavoriteViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ProductListViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ProductSliderViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class HomeFragment : Fragment() {
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var floatingActionButton: FloatingActionButton

    private lateinit var productListViewModel: ProductListViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var productSliderViewModel: ProductSliderViewModel

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private lateinit var noDataMsg: TextView

    private lateinit var shoppingCartViewModel: ShoppingCartViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        productListViewModel = ViewModelProvider(this)[ProductListViewModel::class.java]
        categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        productSliderViewModel = ViewModelProvider(this)[ProductSliderViewModel::class.java]
        shoppingCartViewModel = ViewModelProvider(requireActivity())[ShoppingCartViewModel::class.java]
        favoriteViewModel = ViewModelProvider(requireActivity())[FavoriteViewModel::class.java]

        //refresh layout loading data again
        swipeRefreshLayout = fragmentHomeBinding.refreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            productListViewModel.loadProductsData()
            categoriesViewModel.loadCategoryTypes()
            productSliderViewModel.loadProductSliderData()
        }
        return fragmentHomeBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init load data from api
        productListViewModel.loadProductsData()
        categoriesViewModel.loadCategoryTypes()
        productSliderViewModel.loadProductSliderData()

        noDataMsg = fragmentHomeBinding.noData
        noDataMsg.text = "No Data"
        noDataMsg.visibility = View.GONE

        mShimmerViewContainer = fragmentHomeBinding.shimmerViewContainer

        //get data of products to display on recycler view
        productListViewModel.productsData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> LoadingMethod().showLoadingAnimation(mShimmerViewContainer)
                Status.Success -> {
                    if (it.data != null) {
                        noDataMsg.visibility = View.GONE
                        displayProductList(it.data)
                        swipeRefreshLayout.isRefreshing = false
                        LoadingMethod().hideLoadingAnimation(mShimmerViewContainer)
                    }
                }

                Status.Failed -> {
                    noDataMsg.visibility = View.VISIBLE
                    swipeRefreshLayout.isRefreshing = false
                    LoadingMethod().hideLoadingAnimation(mShimmerViewContainer)
                }
            }
        }
        //get data of product slider images to display on slider
        productSliderViewModel.productSliderData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Success -> it.data?.let { it1 ->
                    displaySliderProduct(it1)
                    swipeRefreshLayout.isRefreshing = false
                }

                else -> {}
            }
        }

        //get data from CategoriesViewModel
        categoriesViewModel.categoryTypesData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> LoadingMethod().showLoadingAnimation(mShimmerViewContainer)
                Status.Success -> it.data?.let { it1 ->
                    displayCategory(it1)
                    swipeRefreshLayout.isRefreshing = false
                    LoadingMethod().hideLoadingAnimation(mShimmerViewContainer)
                }

                else -> {
                    fragmentHomeBinding.cateTitle.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing = false
                    LoadingMethod().hideLoadingAnimation(mShimmerViewContainer)
                }
            }
        }

        shoppingCartViewModel.loadProductsCartData()
        //shopping cart
        shoppingCartViewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }


        // Update the cart RecyclerView with LiveData from ViewModel

        nestedScrollView = view.findViewById(R.id.homeFragment)
        floatingActionButton = view.findViewById(R.id.fabBtn)

        //auto hide floating button go to go
        floatingActionButton.hide()

        // Add a scroll listener to the NestedScrollView.
        nestedScrollView.setOnScrollChangeListener { _, scrollX, scrollY, oldScrollX, oldScrollY ->
            // Check if the user is scrolling up or down.
            if (scrollY > oldScrollY) {
                floatingActionButton.show()
            } else
                floatingActionButton.hide()
        }
        // Scroll to the top of the NestedScrollView when the user clicks on the fabToTop button.
        floatingActionButton.setOnClickListener {
            nestedScrollView.smoothScrollTo(0, 0, 500)
        }
    }

    // Inside YourFragment
//    private val handler = Handler(Looper.getMainLooper())
//    private val refreshInterval = 10000L // 1 minute in milliseconds
//
////    private val refreshRunnable = object : Runnable {
////        override fun run() {
////            // Fetch data at regular intervals
////            productListViewModel.loadProductsData()
////            Log.d("Fetching", "Fetching")
////            // Schedule the next refresh
////            handler.postDelayed(this, refreshInterval)
////        }
////    }
////
////    override fun onResume() {
////        super.onResume()
////        Log.d("onResume", "onResume")
////
////        // Start the refresh loop when the Fragment is visible
//        handler.postDelayed(refreshRunnable, refreshInterval)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Log.d("onPause", "onPause")
//
//        // Stop the refresh loop when the Fragment is not visible
//        handler.removeCallbacks(refreshRunnable)
//    }


    //display product list on home screen
    private fun displayProductList(productsList: List<Product>) {
        // Create GridLayout Manager
        val gridLayoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        fragmentHomeBinding.productListRecyclerView.layoutManager = gridLayoutManager

        // Create adapter
        val productListAdapter = ProductListAdapter(
            shoppingCartViewModel,
            favoriteViewModel,
        )
        productListAdapter.submitList(productsList)
        fragmentHomeBinding.productListRecyclerView.adapter = productListAdapter

    }

    //display slider product on the top
    private fun displaySliderProduct(productSlider: List<ProductSlider?>) {
        val sliderModels = ArrayList<SlideModel>()
        for (data in productSlider) {
            if (data != null) {
                sliderModels.add(SlideModel(data.imageUrl, ScaleTypes.FIT))
            }
        }
        fragmentHomeBinding.carousel.setImageList(sliderModels, ScaleTypes.FIT)
    }

    //display Types of Category
    private fun displayCategory(categoryTypes: List<CategoryTypes>) {
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fragmentHomeBinding.categoryRecyclerView.layoutManager = linearLayoutManager
        val categoryTypesAdapter = CategoryTypesAdapter()
        categoryTypesAdapter.submitList(categoryTypes)
        fragmentHomeBinding.categoryRecyclerView.adapter = categoryTypesAdapter
    }
}