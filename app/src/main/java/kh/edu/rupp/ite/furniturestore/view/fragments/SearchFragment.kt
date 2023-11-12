package kh.edu.rupp.ite.furniturestore.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.furniturestore.adapter.SearchFoundAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentSearchBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
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
        val textview = fragmentSearchBinding.notFound
        textview.text = "No Data"
        textview.visibility = View.INVISIBLE
        searchView.clearFocus()

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchViewHolder.search(query)
                    searchViewHolder.data.observe(viewLifecycleOwner) {
                        when(it.status){
                            204 -> it.let {
                                fragmentSearchBinding.notFound.visibility = View.VISIBLE
                                displayProductSearchFound(null)
                            }
                            200 -> it.data?.let {
                                    it1 -> displayProductSearchFound(it1)
                                    fragmentSearchBinding.notFound.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null){
                    fragmentSearchBinding.notFound.visibility = View.INVISIBLE
                }
              return true
            }
        })

    }

    private fun searchProduct(text: String?) {
        if (text != null) {
            searchViewHolder.search(text)
            searchViewHolder.data.observe(viewLifecycleOwner) {
                Log.d("check", "$it")
                when(it.status){
                    204 -> fragmentSearchBinding.notFound.visibility = View.VISIBLE
                    200 -> it.data?.let { it1 -> displayProductSearchFound(it1) }
                    404 -> fragmentSearchBinding.notFound.visibility = View.VISIBLE
                }
            }
        }
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


