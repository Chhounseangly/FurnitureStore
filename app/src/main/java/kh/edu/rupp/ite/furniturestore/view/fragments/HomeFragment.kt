package kh.edu.rupp.ite.furniturestore.view.fragments

import android.content.Intent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentHomeBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderCategoryTypeBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductItemBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductSlider
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.view.activity.ProductsByCategoryActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.CategoriesViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.FavoriteViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ProductListViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ProductSliderViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var floatingActionButton: FloatingActionButton

    private lateinit var productListViewModel: ProductListViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var productSliderViewModel: ProductSliderViewModel

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private lateinit var processBar: ProgressBar
    private lateinit var noDataMsg: TextView

    private lateinit var shoppingCartViewModel: ShoppingCartViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel

    private lateinit var coordinatorLayout: CoordinatorLayout

    private var currentPage = 1

    override fun bindUi() {
        coordinatorLayout = binding.myCoordinatorLayout
        swipeRefreshLayout = binding.refreshLayout
        processBar = binding.loading!!

        noDataMsg = binding.noData
        mShimmerViewContainer = binding.shimmerViewContainer

        nestedScrollView = binding.homeFragment
        floatingActionButton = binding.fabBtn
    }

    override fun initFields() {
        // Initialize ViewModels
        productListViewModel = ViewModelProvider(this)[ProductListViewModel::class.java]
        categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        productSliderViewModel = ViewModelProvider(this)[ProductSliderViewModel::class.java]
        shoppingCartViewModel = ViewModelProvider(requireActivity())[ShoppingCartViewModel::class.java]
        favoriteViewModel = ViewModelProvider(requireActivity())[FavoriteViewModel::class.java]

        // Set initial state for the "No Data" message
        noDataMsg.text = "No Data"
        noDataMsg.visibility = View.GONE
    }

    override fun initActions() {
        // Load initial data from ViewModels
        productListViewModel.loadProductsData()
        categoriesViewModel.loadCategoryTypes()
        productSliderViewModel.loadProductSliderData()
        shoppingCartViewModel.loadProductsCartData()

        // Auto hide floating button go to top
        floatingActionButton.hide()
    }

    override fun setupListeners() {
        // Refresh layout loading data again
        swipeRefreshLayout.setOnRefreshListener {
            productListViewModel.loadProductsData()
            categoriesViewModel.loadCategoryTypes()
            productSliderViewModel.loadProductSliderData()
        }

        // Add a scroll listener to the NestedScrollView.
        nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            // Check if the user is scrolling up or down.
            if (scrollY > oldScrollY) {
                floatingActionButton.show()
            } else
                floatingActionButton.hide()

            val childView = nestedScrollView.getChildAt(0)
            // Check if the childView is not null
            if (childView != null && scrollY == childView.measuredHeight - nestedScrollView.measuredHeight) {
                // Load more data (increment the page and fetch new data)
                productListViewModel.loadMoreProductsData(++currentPage)
            }
        }

        // Scroll to the top of the NestedScrollView when the user clicks on the fabToTop button.
        floatingActionButton.setOnClickListener {
            nestedScrollView.smoothScrollTo(0, 0, 500)
        }
    }

    override fun setupObservers() {
        // Observe data of products to display on recycler view
        productListViewModel.productsData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> showLoadingAnimation(mShimmerViewContainer)
                Status.Success -> {
                    if (it.data != null) {
                        noDataMsg.visibility = View.GONE
                        processBar.visibility = View.GONE
                        displayProductList(it.data)
                        swipeRefreshLayout.isRefreshing = false
                        hideLoadingAnimation(mShimmerViewContainer)
                    }
                }
                Status.Failed -> {
                    noDataMsg.visibility = View.VISIBLE
                    processBar.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing = false
                    hideLoadingAnimation(mShimmerViewContainer)
                }
                Status.LoadingMore -> {
                    processBar.visibility = View.VISIBLE
                }
            }
        }

        // Observe data of product slider images to display on slider
        productSliderViewModel.productSliderData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Success -> it.data?.let { data ->
                    displaySliderProduct(data)
                    swipeRefreshLayout.isRefreshing = false
                }
                else -> {}
            }
        }

        // Observe data from CategoriesViewModel
        categoriesViewModel.categoryTypesData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> showLoadingAnimation(mShimmerViewContainer)
                Status.Success -> it.data?.let { data ->
                    displayCategory(data)
                    swipeRefreshLayout.isRefreshing = false
                    hideLoadingAnimation(mShimmerViewContainer)
                }
                else -> {
                    binding.cateTitle.visibility = View.GONE
                    swipeRefreshLayout.isRefreshing = false
                    hideLoadingAnimation(mShimmerViewContainer)
                }
            }
        }
    }

    // Display product list on home screen
    private fun displayProductList(productsList: List<Product>) {
        // Create GridLayout Manager
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        binding.productListRecyclerView.layoutManager = gridLayoutManager

        // Initialize the product list adapter
        val productListAdapter =
            DynamicAdapter<Product, ViewHolderProductItemBinding>(ViewHolderProductItemBinding::inflate)
            { view, item, binding ->
                // Handle item click to open the ProductDetailActivity
                view.setOnClickListener {
                    val intent = Intent(it.context, ProductDetailActivity::class.java)
                    intent.putExtra("id", item.id)
                    it.context.startActivity(intent)
                }

                // Bind data to the view using Picasso for image loading
                with(binding) {
                    Picasso.get().load(item.imageUrl)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.ic_error)
                        .into(img)

                    // Set product name and price
                    name.text = item.name
                    val priceText = StringBuilder().append("$").append(item.price).toString()
                    price.text = priceText

                    // Set favorite button based on the isFavorite flag
                    bntFav.setImageResource(if (item.isFavorite == 1) R.drawable.ic_favorited else R.drawable.ic_fav)

                    // Add to cart button click listener
                    addToCartBtn.setOnClickListener {
                        shoppingCartViewModel.addProductToShoppingCart(item.id)

                        Snackbar.make(requireView(), shoppingCartViewModel.toastMessage, Snackbar.LENGTH_LONG).show()
                    }

                    // Favorite button click listener
                    bntFav.setOnClickListener {
                        favoriteViewModel.toggleFavorite(item) {
                            // Set the favorite button image based on the result
                            bntFav.setImageResource(if (it) R.drawable.ic_favorited else R.drawable.ic_fav)
                        }
                    }
                }
            }

        // Set data and adapter for the product list
        productListAdapter.setData(productsList)
        binding.productListRecyclerView.adapter = productListAdapter
    }

    // Display slider product on the top
    private fun displaySliderProduct(productSlider: List<ProductSlider?>) {
        val sliderModels = ArrayList<SlideModel>()

        // Convert productSlider data to SlideModel for the image slider
        for (data in productSlider) {
            if (data != null) {
                sliderModels.add(SlideModel(data.imageUrl, ScaleTypes.FIT))
            }
        }

        // Set the image slider data
        binding.carousel.setImageList(sliderModels, ScaleTypes.FIT)
    }

    // Display Types of Category
    private fun displayCategory(categoryTypes: List<CategoryTypes>) {
        // Create LinearLayoutManager for the category RecyclerView
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecyclerView.layoutManager = linearLayoutManager

        // Initialize the category types adapter
        val categoryTypesAdapter = DynamicAdapter<CategoryTypes, ViewHolderCategoryTypeBinding>(
            ViewHolderCategoryTypeBinding::inflate
        ) { view, item, binding ->
            // Handle item click to open ProductsByCategoryActivity
            view.setOnClickListener {
                val intent = Intent(it.context, ProductsByCategoryActivity::class.java)
                intent.putExtra("id", item.id)
                intent.putExtra("name", item.name)
                it.context.startActivity(intent)
            }

            // Set category name
            binding.name.text = item.name
        }

        // Set data and adapter for the category RecyclerView
        categoryTypesAdapter.setData(categoryTypes)
        binding.categoryRecyclerView.adapter = categoryTypesAdapter
    }
}
