package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiResponse
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryModel
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.Data
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesViewModel : ViewModel() {

    private val _categoryTypesData = MutableLiveData<ApIData<List<CategoryTypes>>>()
    val categoryTypesData: LiveData<ApIData<List<CategoryTypes>>>
        get() = _categoryTypesData

    private val _productByCategory = MutableLiveData<ApIData<Data>>()

    val productByCategory: LiveData<ApIData<Data>>
        get() = _productByCategory


    fun loadCategoryTypes() {
        var apiData = ApIData<List<CategoryTypes>>(Status.Processing,null) //status 102 is processing
        _categoryTypesData.postValue(apiData)
        viewModelScope.launch(Dispatchers.IO) {
            apiData =  try {
               val response = RetrofitInstance.get().api.loadCategories()
               ApIData(Status.Success, response.data)
            }catch (ex: Exception){
                ApIData(Status.Failed, null)
            }

            withContext(Dispatchers.Main.immediate){
                _categoryTypesData.postValue(apiData)
            }

        }
//        RetrofitInstance.get().api.loadCategories().enqueue(object : Callback<CategoryModel> {
//            override fun onResponse(call: Call<CategoryModel>, response: Response<CategoryModel>) {
//                val responseData = response.body()
//
//                if (responseData != null) {
//                    val apiData = ApIData(response.code(), responseData.data)
//                    _categoryTypesData.postValue(apiData)
//                } else {
//                    println("Response data is null")
//                }
//            }
//            override fun onFailure(call: Call<CategoryModel>, t: Throwable) {
//                println("Failure: ${t.message}")
//            }
//        })
    }


    fun loadProductByCategory(id: Int){
        RetrofitInstance.get().api.loadProductsByCategory(id).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val responseData = response.body()
                if (responseData != null) {
                    val apiData = ApIData(Status.Success, responseData.data)
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