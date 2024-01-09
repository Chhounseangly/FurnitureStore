package kh.edu.rupp.ite.furniturestore.view.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCategoryBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductItemBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductByCate
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.CategoriesViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.FavoriteViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel
import java.util.concurrent.TimeUnit

class CategoriesFragment() :
    BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {
    private val categoriesViewModel: CategoriesViewModel by viewModels()

    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()


    companion object {
        private var TAB_ID: Int = 0
        fun newInstance(tabId: Int): CategoriesFragment {
            val fragment = CategoriesFragment()
            val args = Bundle()
            TAB_ID = tabId
            fragment.arguments = args
            return fragment
        }
    }

    override fun bindUi() {
    }

    override fun initFields() {

    }

    override fun initActions() {
        // Load product data by category and category types
        categoriesViewModel.loadProductByCategoryApi(TAB_ID)
        categoriesViewModel.loadCategoryTypes()
    }

    override fun setupListeners() {

    }

    override fun setupObservers() {
        // Set up observer for load product by category LiveData
        val productsSkeletonLoading = binding.productsSkeletonLoading
        categoriesViewModel.productByCategory.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> showLoadingAnimation(productsSkeletonLoading)
                Status.Success -> {
                    it.data?.let { data -> displayProductByCate(data.data) }
                    hideLoadingAnimation(productsSkeletonLoading)
                }

                else -> {
                    hideLoadingAnimation(productsSkeletonLoading)
                }
            }
        }
    }

    // Display products by category in the RecyclerView
    private fun displayProductByCate(items: ProductByCate) {
        // Set up GridLayoutManager for the RecyclerView
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        val recyclerProductsByCate = binding.recyclerProductsByCate
        recyclerProductsByCate.layoutManager = gridLayoutManager

        // Create DynamicAdapter for products with ViewHolderProductItemBinding
        val productByCategoryAdapter =
            DynamicAdapter<Product, ViewHolderProductItemBinding>(ViewHolderProductItemBinding::inflate)
            { view, item, binding ->
                // Set click listener to navigate to ProductDetailActivity
                view.setOnClickListener {
                    val intent = Intent(it.context, ProductDetailActivity::class.java)
                    intent.putExtra("id", item.id)
                    it.context.startActivity(intent)
                }

                val handler = Handler(Looper.getMainLooper())
                val delayMillis = TimeUnit.SECONDS.toMillis(2)
                // Load product data into the ViewHolderProductItemBinding
                with(binding) {
                    Picasso.get()
                        .load(item.imageUrl)
                        .placeholder(loadingImg(requireContext()))
                        .error(R.drawable.ic_error)
                        .into(img)
                    name.text = item.name
                    price.text = "$ ${item.price}"

                    // Add to cart button click listener
                    addToCartBtn.setOnClickListener {
                        val token = AppPreference.get(requireContext()).getToken()
                        if (token == null) {
                            Snackbar.make(
                                requireView(),
                                "Please login to add product to shopping cart",
                                Snackbar.LENGTH_LONG
                            ).show()
                            return@setOnClickListener
                        }

                        val toastMessage = shoppingCartViewModel.addProductToShoppingCart(item.id)
                        if (toastMessage === "Product existed on shopping cart") {
                            Snackbar.make(
                                requireView(),
                                toastMessage,
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {
                            Snackbar.make(
                                requireView(),
                                toastMessage,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }

                    // Set favorite button based on the isFavorite flag
                    bntFav.setImageResource(if (item.is_favorite?.is_favourited == 1) R.drawable.ic_favorited else R.drawable.ic_fav)
                    // Favorite button click listener
                    bntFav.setOnClickListener {
                        val token = AppPreference.get(requireContext()).getToken()
                        if (token == null) {
                            Snackbar.make(
                                requireView(),
                                "Please login to add favorite",
                                Snackbar.LENGTH_LONG
                            ).show()
                            return@setOnClickListener
                        }
                        // Set the favorite button image based on the isFavorite flag
                        if (item.is_favorite?.is_favourited == 1) {
                            item.is_favorite.is_favourited = 0
                            bntFav.setImageResource(R.drawable.ic_fav)
                        } else {
                            item.is_favorite?.is_favourited = 1
                            bntFav.setImageResource(R.drawable.ic_favorited)
                            Snackbar.make(
                                requireView(),
                                "Marked as favorite",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        handler.removeCallbacksAndMessages(null)
                        handler.postDelayed({
                            favoriteViewModel.toggleFavorite(item) {
                                // Set the favorite button image based on the result
                                bntFav.setImageResource(if (it) R.drawable.ic_favorited else R.drawable.ic_fav)
                            }
                        }, delayMillis)
                    }
                }
            }

        // Set data to the adapter and attach it to the RecyclerView
        productByCategoryAdapter.setData(items.products)
        recyclerProductsByCate.adapter = productByCategoryAdapter
    }

}
