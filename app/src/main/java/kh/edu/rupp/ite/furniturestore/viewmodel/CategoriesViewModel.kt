package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiResponse
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductByCate
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesViewModel : ViewModel() {

    private val _categoryTypesData = MutableLiveData<ApIData<List<CategoryTypes>>>()
    val categoryTypesData: LiveData<ApIData<List<CategoryTypes>>>
        get() = _categoryTypesData

    private val _productByCategory = MutableLiveData<ApIData<ApiResponse>>()

    val productByCategory: LiveData<ApIData<ApiResponse>>
        get() = _productByCategory


    fun loadCategoryTypes() {
        var apiData = ApIData<List<CategoryTypes>>(Status.Processing, null)
        _categoryTypesData.postValue(apiData)
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                val response = RetrofitInstance.get().api.loadCategories()
                ApIData(Status.Success, response.data)
            } catch (ex: Exception) {
                ApIData(Status.Failed, null)
            }

            withContext(Dispatchers.Main.immediate) {
                _categoryTypesData.postValue(apiData)
            }
        }
    }


    fun loadProductByCategoryApi(id: Int) {
        var apiData = ApIData<ApiResponse>(Status.Processing, null)
        _productByCategory.value = apiData

        viewModelScope.launch(Dispatchers.IO){
                apiData = try {
                    val response = RetrofitInstance.get().api.loadProductsByCategory(id)
                    ApIData(Status.Success, response)
                }catch (ex: Exception){
                    Log.d("Error", "${ex.message}")
                    ApIData(Status.Failed, null)
                }

            withContext(Dispatchers.Main.immediate){
                _productByCategory.value = apiData
            }
        }
    }
}