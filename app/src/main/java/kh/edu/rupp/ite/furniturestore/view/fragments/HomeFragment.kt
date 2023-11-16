package kh.edu.rupp.ite.furniturestore.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import kh.edu.rupp.ite.furniturestore.adapter.CategoryTypesAdapter
import kh.edu.rupp.ite.furniturestore.adapter.ProductListAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentHomeBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductSlider
import kh.edu.rupp.ite.furniturestore.viewmodel.CategoriesViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ProductListViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ProductSliderViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class HomeFragment(private val shoppingCartViewModel: ShoppingCartViewModel) : Fragment() {
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var floatingActionButton: FloatingActionButton

    private lateinit var productListViewModel: ProductListViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var productSliderViewModel: ProductSliderViewModel

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mShimmerViewContainer: ShimmerFrameLayout


    private lateinit var loading: ProgressBar
    private lateinit var loadingCategory: ProgressBar
    private lateinit var noDataMsg: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize ViewModel instances
        productListViewModel = ViewModelProvider(this)[ProductListViewModel::class.java]
        categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        productSliderViewModel = ViewModelProvider(this)[ProductSliderViewModel::class.java]

        // Initialize and load data from API
        initData()

        // Set up SwipeRefreshLayout
        setupRefreshLayout()

        return fragmentHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        noDataMsg = fragmentHomeBinding.noData
        noDataMsg.text = "No Data"
        noDataMsg.visibility = View.GONE

        mShimmerViewContainer = fragmentHomeBinding.shimmerViewContainer!!

        // Observe data changes using ViewModel
        observeDataChanges()

        // Set up the FloatingActionButton and NestedScrollView
        setupFabAndNestedScrollView()
    }

    // Set up FloatingActionButton and NestedScrollView
    private fun setupFabAndNestedScrollView() {
        // Use View Binding instead of findViewById
        nestedScrollView = fragmentHomeBinding.homeFragment
        floatingActionButton = fragmentHomeBinding.fabBtn

        // Initially hide FloatingActionButton
        floatingActionButton.hide()

        // Add a scroll listener to the NestedScrollView
        nestedScrollView.setOnScrollChangeListener { _, scrollX, scrollY, oldScrollX, oldScrollY ->
            // Check if the user is scrolling up or down
            if (scrollY > oldScrollY) {
                floatingActionButton.show()
            } else {
                floatingActionButton.hide()
            }
        }

        // Scroll to the top of the NestedScrollView when the FloatingActionButton is clicked
        floatingActionButton.setOnClickListener {
            nestedScrollView.smoothScrollTo(0, 0, 500)
        }
    }

    // Hide loading animation
    private fun hideLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout) {
        viewContainerLoadingId.stopShimmerAnimation()
        viewContainerLoadingId.visibility = View.GONE
    }

    // Show loading animation
    private fun showLoadingAnimation(viewContainerLoadingId: ShimmerFrameLayout) {
        viewContainerLoadingId.startShimmerAnimation()
    }

    //display product list on home screen
    private fun displayProductList(productsList: List<Product>) {
        // Create GridLayout Manager
        val gridLayoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        fragmentHomeBinding.productListRecyclerView.layoutManager = gridLayoutManager

        // Create adapter
        val productListAdapter = ProductListAdapter(shoppingCartViewModel, productListViewModel)
        productListAdapter.submitList(productsList)
        fragmentHomeBinding.productListRecyclerView.adapter = productListAdapter
    }

    // Display product list on the home screen
    private fun displaySliderProduct(productSlider: List<ProductSlider?>) {
        val sliderModels = ArrayList<SlideModel>()
        for (data in productSlider) {
            if (data != null) {
                sliderModels.add(SlideModel(data.imageUrl, ScaleTypes.FIT))
            }
        }
        fragmentHomeBinding.carousel.setImageList(sliderModels, ScaleTypes.FIT)
    }

    // Display types of categories
    private fun displayCategory(categoryTypes: List<CategoryTypes>) {
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fragmentHomeBinding.categoryRecyclerView.layoutManager = linearLayoutManager
        val categoryTypesAdapter = CategoryTypesAdapter()
        categoryTypesAdapter.submitList(categoryTypes)
        fragmentHomeBinding.categoryRecyclerView.adapter = categoryTypesAdapter
    }

    // Initialize data loading and refresh layout
    private fun initData() {
        productListViewModel.loadProductsData()
        categoriesViewModel.loadCategoryTypes()
        productSliderViewModel.loadProductSliderData()
    }

    // Set up SwipeRefreshLayout
    private fun setupRefreshLayout() {
        swipeRefreshLayout = fragmentHomeBinding.refreshLayout!!
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh data when SwipeRefreshLayout is triggered
            initData()
        }
    }

    // Observe changes in ViewModel data
    private fun observeDataChanges() {
        // Observe changes in product list data
        productListViewModel.productsData.observe(viewLifecycleOwner) { productData ->
            handleProductDataStatus(productData)
        }

        // Observe changes in product slider data
        productSliderViewModel.productSliderData.observe(viewLifecycleOwner) { sliderData ->
            handleSliderDataStatus(sliderData)
        }

        // Observe changes in category types data
        categoriesViewModel.categoryTypesData.observe(viewLifecycleOwner) { categoryData ->
            handleCategoryDataStatus(categoryData)
        }

        // Observe the messageLiveData
        shoppingCartViewModel.messageLiveData.observe(viewLifecycleOwner) { message ->
            // Display the message using Toast
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    // Handle status of product list data
    private fun handleProductDataStatus(productData: ApIData<List<Product>>) {
        when (productData.status) {
            102 -> showLoadingAnimation(mShimmerViewContainer)
            200 -> {
                if (!productData.data.isNullOrEmpty()) {
                    displayProductList(productData.data)
                    swipeRefreshLayout.isRefreshing = false
                    hideLoadingAnimation(mShimmerViewContainer)
                }
            }
            else -> {
                noDataMsg.visibility = View.VISIBLE
                swipeRefreshLayout.isRefreshing = false
                hideLoadingAnimation(mShimmerViewContainer)
            }
        }
    }

    // Handle status of slider data
    private fun handleSliderDataStatus(sliderData: ApIData<List<ProductSlider>>) {
        when (sliderData.status) {
            200 -> sliderData.data?.let { displaySliderProduct(it) }
            else -> { /* Handle other status codes if needed */ }
        }
    }

    // Handle status of category data
    private fun handleCategoryDataStatus(categoryData: ApIData<List<CategoryTypes>>) {
        when (categoryData.status) {
            102 -> showLoadingAnimation(mShimmerViewContainer)
            200 -> categoryData.data?.let {
                displayCategory(it)
                swipeRefreshLayout.isRefreshing = false
                hideLoadingAnimation(mShimmerViewContainer)
            }
            else -> {
                fragmentHomeBinding.cateTitle?.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
                hideLoadingAnimation(mShimmerViewContainer)
            }
        }
    }
}