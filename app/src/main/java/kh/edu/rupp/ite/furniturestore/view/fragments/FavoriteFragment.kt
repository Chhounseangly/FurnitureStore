package kh.edu.rupp.ite.furniturestore.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import kh.edu.rupp.ite.furniturestore.adapter.FavoriteAdapter
import kh.edu.rupp.ite.furniturestore.custom_method.LoadingMethod
import kh.edu.rupp.ite.furniturestore.databinding.FragmentFavoriteBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.FavoriteViewModel

class FavoriteFragment : Fragment() {
    // View binding for the fragment
    private lateinit var fragmentFavoriteBinding: FragmentFavoriteBinding

    // Adapter for the list of favorite products
    private lateinit var favoriteAdapter: FavoriteAdapter

    // SwipeRefreshLayout for refreshing the list
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // ShimmerFrameLayout for loading animation
    private lateinit var mShimmerViewContainer: ShimmerFrameLayout

    // TextView for displaying a message when there is no data
    private lateinit var noDataMsg: TextView

    // ViewModel for handling favorite products
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        fragmentFavoriteBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return fragmentFavoriteBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements
        noDataMsg = fragmentFavoriteBinding.noData

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = fragmentFavoriteBinding.refreshLayout
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
        LoadingMethod().showLoadingAnimation(mShimmerViewContainer)
    }

    // Hide loading animation
    private fun hideLoadingAnimation() {
        LoadingMethod().hideLoadingAnimation(mShimmerViewContainer)
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
        // Create LinearLayoutManager
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentFavoriteBinding.favoriteRecyclerView.layoutManager = linearLayoutManager

        // Create and set up the adapter
        favoriteAdapter = FavoriteAdapter()
        favoriteAdapter.submitList(data)
        fragmentFavoriteBinding.favoriteRecyclerView.adapter = favoriteAdapter
    }
}