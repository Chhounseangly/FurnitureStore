package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductListViewModel: ViewModel() {

    private val _productsData = MutableLiveData<ApIData<List<ProductList>>>()
    private val BASE_URL = "http://10.0.2.2:8000/"
    val productsData: LiveData<ApIData<List<ProductList>>>
        get() = _productsData


    private lateinit var productList: List<ProductList>
    fun loadProductsData(){

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        val call = api.loadProductList()

        call.enqueue(object : Callback<List<ProductList>> {
            override fun onResponse(call: Call<List<ProductList>>, response: Response<List<ProductList>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("onProductResponse", "Success")
                        productList = it
                    }
                    val apiData = ApIData<List<ProductList>>(Status.SUCCESS, productList)
                    _productsData.postValue(apiData)
                } else {
                    Log.d("onProductResponse", "Error: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<List<ProductList>>, t: Throwable) {
                Log.d("onProductResponse", "Failed: $t")
            }
        })
    }
}