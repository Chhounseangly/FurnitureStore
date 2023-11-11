package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductDetail
import kh.edu.rupp.ite.furniturestore.model.api.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
class ProductDetailViewModel: ViewModel() {
    private val BASE_URL = "http://10.0.2.2:8000/"

    private val _productsData = MutableLiveData<ApIData<Product>>()
    val productsData: LiveData<ApIData<Product>>
        get() = _productsData

    fun loadProductDetail(id: Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        api.loadProductDetail(id).enqueue(object : Callback<ProductDetail> {
            override fun onResponse(call: Call<ProductDetail>, response: Response<ProductDetail>) {
                val responseData = response.body()
                if (responseData != null) {
                    val apiData = ApIData(response.code(), responseData.data)
                    _productsData.postValue(apiData)
                } else {
                    println("Response data is null")
                }
            }
            override fun onFailure(call: Call<ProductDetail>, t: Throwable) {
                println("Failure: ${t.message}")
            }
        })
    }
}