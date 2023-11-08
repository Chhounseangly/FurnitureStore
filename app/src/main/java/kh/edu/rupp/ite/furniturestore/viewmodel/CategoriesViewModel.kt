package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiResponse
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryModel
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.Data
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CategoriesViewModel : ViewModel() {
    private val BASE_URL = "http://10.0.2.2:8000/"

    private val _categoryTypesData = MutableLiveData<ApIData<List<CategoryTypes>>>()
    val categoryTypesData: LiveData<ApIData<List<CategoryTypes>>>
        get() = _categoryTypesData

    private val _productByCategory = MutableLiveData<ApIData<Data>>()

    val productByCategory: LiveData<ApIData<Data>>
        get() = _productByCategory


    fun loadCategoryTypes() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)
        api.loadCategories().enqueue(object : Callback<CategoryModel> {
            override fun onResponse(call: Call<CategoryModel>, response: Response<CategoryModel>) {
                val responseData = response.body()

                if (responseData != null) {
                    println("Response: ${responseData.success}")
                    println("Message: ${responseData.message}")
                    val apiData = ApIData(Status.SUCCESS, responseData.data)
                    _categoryTypesData.postValue(apiData)
                } else {
                    println("Response data is null")
                }
            }
            override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
                println("Failure: ${t.message}")
            }
        })
    }


    fun loadProductByCategory(id: Int){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiService::class.java)
        api.loadProductsByCategory(id).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val responseData = response.body()
                if (responseData != null) {
                    println("Response: ${responseData.success}")
                    println("Message: ${responseData.message}")
                    val apiData = ApIData(Status.SUCCESS, responseData.data)
                    _productByCategory.postValue(apiData)
                } else {
                    println("Response data is null")
                }
            }
            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                println("Failure: ${t.message}")
            }
        })
    }
}