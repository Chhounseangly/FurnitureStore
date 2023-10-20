package kh.edu.rupp.ite.furniturestore.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kh.edu.rupp.ite.furniturestore.api.model.Product
import kh.edu.rupp.ite.furniturestore.api.service.ApiService
import kh.edu.rupp.ite.furniturestore.databinding.FragmentHomeBinding
import kh.edu.rupp.ite.furniturestore.ui.adapter.ProductAdapter
import retrofit2.Call

import retrofit2.Callback

import retrofit2.Response

import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment() {
    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return fragmentHomeBinding.root
    }


    var data = ArrayList<Product>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = arrayListOf(
            Product(
                1,
                "test",
                "test",
                "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png"
            ),
            Product(
                1,
                "test",
                "test",
                "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png"
            ),
            Product(
                1,
                "test",
                "test",
                "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png"
            )

        )
        showProductList(data)
    }

    private fun loadProductsListFromApi() {

        // Create Client Request
        val productsReq = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create service Object
        val apiService = productsReq.create(ApiService::class.java)

        // Load Products list from api/server
        apiService.loadProductList()?.enqueue(object : Callback<List<Product?>?> {
            override fun onResponse(
                call: Call<List<Product?>?>,
                response: Response<List<Product?>?>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        showProductList(it)
                    }
                } else

                    Toast.makeText(context, "Load Products data failed!", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<List<Product?>?>, t: Throwable) {
                Toast.makeText(context, "Load Products data failed!", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showProductList(productsList: List<Product?>) {

        // Create GridLayout Manager
        val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fragmentHomeBinding.recyclerView.layoutManager = gridLayoutManager

        // Create adapter
        val productAdapter = ProductAdapter()
        productAdapter.submitList(productsList)
        fragmentHomeBinding.recyclerView.adapter = productAdapter
    }
}