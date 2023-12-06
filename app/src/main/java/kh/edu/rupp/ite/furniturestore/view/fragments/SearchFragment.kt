package kh.edu.rupp.ite.furniturestore.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.furniturestore.adapter.SearchFoundAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentSearchBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.SearchViewHolder

class SearchFragment : Fragment() {

    private lateinit var fragmentSearchBinding: FragmentSearchBinding

    private val handler = Handler()
    private var searchRunnable: Runnable? = null
    private var searchViewHolder = SearchViewHolder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return fragmentSearchBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the search view
        setupSearchView()
    }

    // Function to set up the search view
    private fun setupSearchView() {
        val searchView = fragmentSearchBinding.searchProduct

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handling submit if needed
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                // Hide noData view initially
                fragmentSearchBinding.notFound.visibility = View.GONE

                // Use a delay before triggering the search
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    // Trigger the search operation with newText
                    performSearch(query)
                }, 500) // 500 milliseconds (0.5 seconds) delay

                return true
            }
        })
    }

    // Function to perform the search
    private fun performSearch(query: String?) {
        if (query != null) {
            // Delegate the search operation to SearchViewHolder
            searchViewHolder.search(query)
            // Observe the LiveData for search results
            searchViewHolder.data.observe(viewLifecycleOwner) {
                // Handle the search results
                handleSearchResult(it)
            }
        } else {
            // If the query is null, display no search results
            displayProductSearchFound(null)
        }
    }

    // Function to handle the search results
    private fun handleSearchResult(searchData: ApIData<List<Product>>) {
        with(fragmentSearchBinding) {
            when (searchData.status) {
                Status.Processing -> {
                    // Show loading view and hide other views
                    loading.visibility = View.VISIBLE
                    notFound.visibility = View.GONE
                    // Display an empty product list during processing
                    displayProductSearchFound(null)
                }

                Status.Success -> {
                    // Hide loading and noData views
                    loading.visibility = View.GONE
                    notFound.visibility = View.GONE
                    // Display the search results
                    displayProductSearchFound(searchData.data)
                }

                Status.Failed -> {
                    // Hide loading view and show noData view
                    loading.visibility = View.GONE
                    notFound.visibility = View.VISIBLE
                    // Display an empty product list on failure
                    displayProductSearchFound(null)
                }
            }
        }
    }

    // Function to display the search results in the RecyclerView
    private fun displayProductSearchFound(product: List<Product>?) {
        // Set up a LinearLayoutManager for the RecyclerView
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentSearchBinding.searchFoundRecyclerView.layoutManager = linearLayoutManager

        // Create a SearchFoundAdapter and submit the product list
        val searchFoundAdapter = SearchFoundAdapter()
        searchFoundAdapter.submitList(product)
        fragmentSearchBinding.searchFoundRecyclerView.adapter = searchFoundAdapter
    }
}