package kh.edu.rupp.ite.furniturestore.controller.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.controller.adapter.CategoryTypesAdapter
import kh.edu.rupp.ite.furniturestore.controller.adapter.ProductListAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentHomeBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductSlider
import kh.edu.rupp.ite.furniturestore.model.api.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment() : Fragment() {
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private var productList = ArrayList<ProductList>()
    private var productSliderList = ArrayList<ProductSlider>()
    private var listCategoryTypes = ArrayList<CategoryTypes>()
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var floatingActionButton: FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return fragmentHomeBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (i in 1..10) {
            productSliderList.add(
                ProductSlider(
                    1,
                    "test $i ",
                    "test",
                    "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png"
                ),
            )
            listCategoryTypes.add(
                CategoryTypes(
                    i,
                    "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png",
                    "chair"
                ),
            )
            productList.add(
                ProductList(
                    i,
                    "Table",
                    "https://i4.komnit.com/store/upload/images/express_2207/112290-ARJDYN/1657316942-ARJDYN.jpg",
                    50
                ),
            )
        }

        displayCategory(listCategoryTypes)
        displaySliderProduct(productSliderList)
        displayProductList(productList)

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

    private fun loadProductsListFromApi() {

        // Create Client Request
        val productsReq = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create service Object
        val apiService = productsReq.create(ApiService::class.java)

        // Load Products list from api/server
        apiService.loadProductList()?.enqueue(object : Callback<List<ProductSlider?>?> {
            override fun onResponse(
                call: Call<List<ProductSlider?>?>,
                response: Response<List<ProductSlider?>?>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        displaySliderProduct(it)
                    }
                } else

                    Toast.makeText(context, "Load Products data failed!", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<List<ProductSlider?>?>, t: Throwable) {
                Toast.makeText(context, "Load Products data failed!", Toast.LENGTH_LONG).show()
            }
        })
    }

    //display product list on home screen
    private fun displayProductList(productsList: ArrayList<ProductList>) {
        // Create GridLayout Manager
        val gridLayoutManager =
            GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        fragmentHomeBinding.productListRecyclerView.layoutManager = gridLayoutManager

        // Create adapter
        val productListAdapter = ProductListAdapter()
        productListAdapter.submitList(productsList)
        fragmentHomeBinding.productListRecyclerView.adapter = productListAdapter

    }

    //display slider product on the top
    private fun displaySliderProduct(productSlider: List<ProductSlider?>) {
        var name = ArrayList<ProductSlider>()
        val sliderModels = ArrayList<SlideModel>()
        for (data in productSlider) {
            if (data != null) {
                sliderModels.add(SlideModel(data.imageUrl, ScaleTypes.FIT))
                fragmentHomeBinding.nameTxt.text = data.name
            }
        }

        fragmentHomeBinding.carousel.setImageList(sliderModels, ScaleTypes.FIT)

    }

    //display Types of Category
    private fun displayCategory(categoryTypes: List<CategoryTypes>) {
        val linearLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        fragmentHomeBinding.categoryRecyclerView.layoutManager = linearLayoutManager
        val categoryTypesAdapter = CategoryTypesAdapter()
        categoryTypesAdapter.submitList(categoryTypes)
        fragmentHomeBinding.categoryRecyclerView.adapter = categoryTypesAdapter

    }

}



