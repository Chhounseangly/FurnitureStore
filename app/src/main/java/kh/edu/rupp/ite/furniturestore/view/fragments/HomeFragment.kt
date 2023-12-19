package kh.edu.rupp.ite.furniturestore.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import kh.edu.rupp.ite.furniturestore.utility.SnackbarUtil
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
    private lateinit var noDataMsg: TextView

    private lateinit var shoppingCartViewModel: ShoppingCartViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel

    private lateinit var coordinatorLayout: CoordinatorLayout
    override fun bindUi() {
        coordinatorLayout = binding.myCoordinatorLayout

        productListViewModel = ViewModelProvider(this)[ProductListViewModel::class.java]
        categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
        productSliderViewModel = ViewModelProvider(this)[ProductSliderViewModel::class.java]
        shoppingCartViewModel =
            ViewModelProvider(requireActivity())[ShoppingCartViewModel::class.java]
        favoriteViewModel = ViewModelProvider(requireActivity())[FavoriteViewModel::class.java]

        //refresh layout loading data again
        swipeRefreshLayout = binding.refreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            productListViewModel.loadProductsData()
            categoriesViewModel.loadCategoryTypes()
            productSliderViewModel.loadProductSliderData()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //init load data from api
        productListViewModel.loadProductsData()
        categoriesViewModel.loadCategoryTypes()
        productSliderViewModel.loadProductSliderData()

        noDataMsg = binding.noData
        noDataMsg.text = "No Data"
        noDataMsg.visibility = View.GONE

        mShimmerViewContainer = binding.shimmerViewContainer

        //get data of products to display on recycler view
        productListViewModel.productsData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> showLoadingAnimation(mShimmerViewContainer)
                Status.Success -> {
                    if (it.data != null) {
                        noDataMsg.visibility = View.GONE
                        displayProductList(it.data)
                        swipeRefreshLayout.isRefreshing = false
                        hideLoadingAnimation(mShimmerViewContainer)
                    }
                }

                Status.Failed -> {
                    noDataMsg.visibility = View.VISIBLE
                    swipeRefreshLayout.isRefreshing = false
                    hideLoadingAnimation(mShimmerViewContainer)
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
                Status.Processing -> showLoadingAnimation(mShimmerViewContainer)
                Status.Success -> it.data?.let { it1 ->
                    displayCategory(it1)
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

        shoppingCartViewModel.loadProductsCartData()
        //shopping cart




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

    // Display product list on home screen
    private fun displayProductList(productsList: List<Product>) {

        val title = binding.cateTitle

        // Create GridLayout Manager
        val gridLayoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        binding.productListRecyclerView.layoutManager = gridLayoutManager

        val productListAdapter =
            DynamicAdapter<Product, ViewHolderProductItemBinding>(ViewHolderProductItemBinding::inflate)
            { view, item, binding ->
                view.setOnClickListener {
                    val intent = Intent(it.context, ProductDetailActivity::class.java)
                    intent.putExtra("id", item.id)
                    it.context.startActivity(intent)
                }

                with(binding) {
                    // Load image using Picasso
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
                        SnackbarUtil.showSnackBar(requireContext(), requireView(), shoppingCartViewModel.toastMessage)
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

        productListAdapter.setData(productsList)
        binding.productListRecyclerView.adapter = productListAdapter
    }

    //display slider product on the top
    private fun displaySliderProduct(productSlider: List<ProductSlider?>) {
        val sliderModels = ArrayList<SlideModel>()
        for (data in productSlider) {
            if (data != null) {
                sliderModels.add(SlideModel(data.imageUrl, ScaleTypes.FIT))
            }
        }
        binding.carousel.setImageList(sliderModels, ScaleTypes.FIT)
    }

    //display Types of Category
    private fun displayCategory(categoryTypes: List<CategoryTypes>) {
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRecyclerView.layoutManager = linearLayoutManager

        val categoryTypesAdapter = DynamicAdapter<CategoryTypes, ViewHolderCategoryTypeBinding>(
            ViewHolderCategoryTypeBinding::inflate
        )
        { view, item, binding ->
            view.setOnClickListener {
                val intent = Intent(it.context, ProductsByCategoryActivity::class.java)
                intent.putExtra("id", item.id)
                intent.putExtra("name", item.name)
                it.context.startActivity(intent)
            }

            binding.name.text = item.name
        }

        categoryTypesAdapter.setData(categoryTypes)
        binding.categoryRecyclerView.adapter = categoryTypesAdapter

    }
}