package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class ProductListViewModel: ViewModel() {
    private val BASE_URL = "http://10.0.2.2:8000/"

    private val _productsData = MutableLiveData<ApIData<List<ProductList>>>()
    val productsData: LiveData<ApIData<List<ProductList>>>
        get() = _productsData
    private lateinit var productList: List<ProductList>

    fun loadProductsData(){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)


        api.loadProductList().enqueue(object : Callback<Res<ProductList>> {
            override fun onResponse(call: Call<Res<ProductList>>, response: Response<Res<ProductList>>) {
                val responseData = response.body()
                if (responseData != null) {
                    println("Response: ${responseData.success}")
                    println("Message: ${responseData.message}")
                    val apiData = ApIData(Status.SUCCESS, responseData.data)
                    _productsData.postValue(apiData)
                } else {
                    println("Response data is null")
                }
            }

            override fun onFailure(call: Call<Res<ProductList>>, t: Throwable) {
                println("Failure: ${t.message}")
            }
        })

    }




}