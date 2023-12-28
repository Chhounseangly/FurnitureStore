package kh.edu.rupp.ite.furniturestore.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentSearchBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderSearchFoundBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.SearchViewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val searchView: SearchView by lazy { binding.searchProduct }

    private val handler = Handler()
    private var searchViewHolder = SearchViewModel()

    override fun bindUi() {

    }

    override fun initFields() {

    }

    override fun initActions() {

    }

    @SuppressLint("SetTextI18n")
    override fun setupListeners() {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handling submit if needed
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                // Hide noData view initially
                binding.notFound.visibility = View.GONE

                // Use a delay before triggering the search
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    // Trigger the search operation with newText
                    performSearch(query)
                }, 300) // 300 milliseconds (0.3 seconds) delay

                return true
            }
        })
    }

    override fun setupObservers() {

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
    private fun handleSearchResult(searchData: ApiData<Res<List<Product>>>) {
        with(binding) {
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
                    displayProductSearchFound(searchData.data?.data)
                }

                Status.Failed -> {
                    // Hide loading view and show noData view
                    loading.visibility = View.GONE
                    notFound.visibility = View.VISIBLE
                    // Display an empty product list on failure
                    displayProductSearchFound(null)
                }

                else -> {}
            }
        }
    }

    // Function to display the search results in the RecyclerView
    private fun displayProductSearchFound(product: List<Product>?) {
        // Set up a LinearLayoutManager for the RecyclerView
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.searchFoundRecyclerView.layoutManager = linearLayoutManager

        // Initialize the searchFoundAdapter, passing <Model, ViewHolderBinding> and display UI
        val searchFoundAdapter =
            DynamicAdapter<Product, ViewHolderSearchFoundBinding>(ViewHolderSearchFoundBinding::inflate) { view, item, binding, _ ->
                // Handle click on view to navigate to ProductDetail
                view.setOnClickListener {
                    val intent = Intent(it.context, ProductDetailActivity::class.java)
                    intent.putExtra("id", item.id)
                    it.context.startActivity(intent)
                }

                // Passing data to display UI
                with(binding) {
                    Picasso.get().load(item.imageUrl)
                        .placeholder(loadingImg(requireContext()))
                        .error(R.drawable.ic_error)
                        .into(img)
                    name.text = item.name
                    price.text = item.price.toString()
                }
            }

        // Set data for the searchFoundAdapter
        if (product != null) {
            searchFoundAdapter.setData(product)
        }

        // Set the adapter for the searchFoundRecyclerView
        binding.searchFoundRecyclerView.adapter = searchFoundAdapter
    }
}
