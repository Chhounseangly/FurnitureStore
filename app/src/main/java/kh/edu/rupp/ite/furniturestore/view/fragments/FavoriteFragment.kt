package kh.edu.rupp.ite.furniturestore.view.fragments

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentFavoriteBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductItemBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.FavoriteViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>(FragmentFavoriteBinding::inflate) {

    private val favoriteViewModel: FavoriteViewModel by viewModels({ requireActivity() })
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels({ requireActivity() })
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private lateinit var noDataMsg: TextView

    override fun bindUi() {
        swipeRefreshLayout = binding.refreshLayout
        noDataMsg = binding.noData
        mShimmerViewContainer = binding.loading
    }

    override fun initFields() {

    }

    override fun initActions() {
        favoriteViewModel.loadFavoriteProducts()
    }

    override fun setupListeners() {
        swipeRefreshLayout.setOnRefreshListener {
            favoriteViewModel.loadFavoriteProducts()
        }
    }

    override fun setupObservers() {
        favoriteViewModel.productsData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> showLoadingAnimation(mShimmerViewContainer)
                Status.Success -> it.data?.let { data -> handleSuccess(data) }
                Status.Failed -> handleFailure()
                else -> {}
            }
        }
    }

    // Handle successful data retrieval
    private fun handleSuccess(data: List<Product>) {
        hideLoadingAnimation(mShimmerViewContainer)
        displayFavorite(data)

        // Check if the data is null or empty
        if (data.isNotEmpty()) {
            // Hide the "No Data" message if there is data
            noDataMsg.visibility = View.GONE
        } else {
            // Show the "No Data" message if there is no data
            noDataMsg.visibility = View.VISIBLE
        }

        // Stop the SwipeRefreshLayout refreshing animation
        swipeRefreshLayout.isRefreshing = false
    }

    // Handle data retrieval failure
    private fun handleFailure() {
        hideLoadingAnimation(mShimmerViewContainer)

        // Show the "No Data" message on failure
        noDataMsg.visibility = View.VISIBLE

        // Stop the SwipeRefreshLayout refreshing animation
        swipeRefreshLayout.isRefreshing = false
    }

    // Display the list of favorite products using a RecyclerView
    private fun displayFavorite(data: List<Product>) {
        // Create GridLayout Manager
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        binding.favoriteRecyclerView.layoutManager = gridLayoutManager

        // Create DynamicAdapter for products with ViewHolderProductItemBinding
        val favoriteAdapter =
            DynamicAdapter<Product, ViewHolderProductItemBinding>(ViewHolderProductItemBinding::inflate) { view, item, binding ->
                view.setOnClickListener {
                    // Click listener to navigate to ProductDetailActivity
                    val intent = Intent(it.context, ProductDetailActivity::class.java)
                    intent.putExtra("id", item.id)
                    it.context.startActivity(intent)
                }
                with(binding) {
                    // Use Picasso to load and display the product image
                    Picasso.get().load(item.imageUrl)
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.ic_error)
                        .into(img)

                    // Set the product name and price
                    name.text = item.name
                    price.text = item.price.toString()

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
                            // Callback function if needed
                        }
                    }
                }
            }

        // Set data to the adapter and attach it to the RecyclerView
        favoriteAdapter.setData(data)
        binding.favoriteRecyclerView.adapter = favoriteAdapter
    }
}
