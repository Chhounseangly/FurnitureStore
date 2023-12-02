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
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.SearchViewHolder

class SearchFragment() : Fragment() {

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

        val searchView = fragmentSearchBinding.searchProduct
        val noData = fragmentSearchBinding.notFound
        val loading = fragmentSearchBinding.loading

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                fragmentSearchBinding.notFound.visibility = View.GONE

                // Use a delay before triggering the search
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    // Trigger your search operation with newText
                    if (query != null) {
                        searchViewHolder.search(query)
                        searchViewHolder.data.observe(viewLifecycleOwner) {
                            when (it.status) {
                                Status.Processing -> {
                                    loading.visibility = View.VISIBLE
                                    noData.visibility = View.GONE
                                    displayProductSearchFound(null)
                                }
                                Status.Success -> it.data?.let { it1 ->
                                    loading.visibility = View.GONE
                                    noData.visibility = View.GONE
                                    displayProductSearchFound(it1)
                                }
                                Status.Failed -> {
                                    loading.visibility = View.GONE
                                    noData.visibility = View.VISIBLE
                                    displayProductSearchFound(null)
                                }
                            }
                        }
                    }else {
                        displayProductSearchFound(null)
                        noData.visibility = View.GONE
                    }
                }, 300) // 300 milliseconds (0.3 seconds) delay

                return true
            }
        })

    }

    //function display category
    private fun displayProductSearchFound(product: List<Product>?) {
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentSearchBinding.searchFoundRecyclerView.layoutManager = linearLayoutManager

        val searchFoundAdapter = SearchFoundAdapter()
        searchFoundAdapter.submitList(product)
        fragmentSearchBinding.searchFoundRecyclerView.adapter = searchFoundAdapter
    }
}


