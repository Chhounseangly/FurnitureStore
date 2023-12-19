package kh.edu.rupp.ite.furniturestore.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentFavoriteBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductItemBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.utility.SnackbarUtil
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.FavoriteViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class FavoriteFragment : BaseFragment() {
    // View binding for the fragment
    private lateinit var fragmentFavoriteBinding: FragmentFavoriteBinding

    // SwipeRefreshLayout for refreshing the list
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // ShimmerFrameLayout for loading animation
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout

    // TextView for displaying a message when there is no data
    private lateinit var noDataMsg: TextView

    // ViewModel for handling favorite products
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var shoppingCartViewModel: ShoppingCartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        fragmentFavoriteBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = fragmentFavoriteBinding.refreshLayout
        return fragmentFavoriteBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteViewModel = ViewModelProvider(requireActivity())[FavoriteViewModel::class.java]
        shoppingCartViewModel =
            ViewModelProvider(requireActivity())[ShoppingCartViewModel::class.java]

        // Initialize UI elements
        noDataMsg = fragmentFavoriteBinding.noData


        swipeRefreshLayout.setOnRefreshListener {
            // Refresh the list when SwipeRefreshLayout is triggered
            favoriteViewModel.loadFavoriteProducts()
        }

        // Initialize ShimmerFrameLayout
        mShimmerViewContainer = fragmentFavoriteBinding.loading

        // Observe changes in the list of favorite products
        favoriteViewModel.productsData.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> showLoadingAnimation()
                Status.Success -> it.data?.let { data -> handleSuccess(data) }
                Status.Failed -> handleFailure()
            }
        }

        // Load initial data
        favoriteViewModel.loadFavoriteProducts()
    }

    // Show loading animation
    private fun showLoadingAnimation() {
        showLoadingAnimation(mShimmerViewContainer)
    }

    // Hide loading animation
    private fun hideLoadingAnimation() {
        hideLoadingAnimation(mShimmerViewContainer)
    }

    // Handle successful data retrieval
    private fun handleSuccess(data: List<Product>) {
        hideLoadingAnimation()
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
        hideLoadingAnimation()

        // Show the "No Data" message on failure
        noDataMsg.visibility = View.VISIBLE

        // Stop the SwipeRefreshLayout refreshing animation
        swipeRefreshLayout.isRefreshing = false
    }

    // Display the list of favorite products using a RecyclerView
    private fun displayFavorite(data: List<Product>) {
        // Create GridLayout Manager
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        fragmentFavoriteBinding.favoriteRecyclerView.layoutManager = gridLayoutManager


        //create adapter, passing <Model, ViewHolderBinding>  and display ui
        val favoriteAdapter =
            DynamicAdapter<Product, ViewHolderProductItemBinding>(ViewHolderProductItemBinding::inflate) { view, item, binding ->
                view.setOnClickListener {
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
                        SnackbarUtil.showSnackBar(requireContext(), requireView(), shoppingCartViewModel.toastMessage)
                    }



                    // Favorite button click listener
                    bntFav.setOnClickListener {
                        favoriteViewModel.toggleFavorite(item) {

                        }
                    }
                }
            }

        //set up data to Adapter
        favoriteAdapter.setData(data)
        // Create and set up the adapter
//        favoriteAdapter = FavoriteAdapter()
//        favoriteAdapter.submitList(data)
        fragmentFavoriteBinding.favoriteRecyclerView.adapter = favoriteAdapter
    }
}