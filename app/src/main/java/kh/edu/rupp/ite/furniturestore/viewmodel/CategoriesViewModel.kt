package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiResponse
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesViewModel : ViewModel() {

    // LiveData to observe changes in the list of category types
    private val _categoryTypesData = MutableLiveData<ApIData<List<CategoryTypes>>>()
    val categoryTypesData: LiveData<ApIData<List<CategoryTypes>>>
        get() = _categoryTypesData

    // LiveData to observe changes in the product list based on selected category
    private val _productByCategory = MutableLiveData<ApIData<ApiResponse>>()
    val productByCategory: LiveData<ApIData<ApiResponse>>
        get() = _productByCategory

    // Function to load the list of available category types
    fun loadCategoryTypes() {
        // Initial status while processing
        var apiData = ApIData<List<CategoryTypes>>(Status.Processing, null)
        _categoryTypesData.postValue(apiData)

        // Processing in the background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                // Fetch the list of category types from the API
                val response = RetrofitInstance.get().api.loadCategories()
                ApIData(Status.Success, response.data)
            } catch (ex: Exception) {
                // Handle exceptions and set status to failed
                ApIData(Status.Failed, null)
            }

            // Process outside the background (update LiveData)
            withContext(Dispatchers.Main.immediate) {
                _categoryTypesData.postValue(apiData)
            }
        }
    }

    // Function to load products based on the selected category
    fun loadProductByCategoryApi(id: Int) {
        // Initial status while processing
        var apiData = ApIData<ApiResponse>(Status.Processing, null)
        _productByCategory.value = apiData

        // Processing in the background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                // Fetch products by the selected category from the API
                val response = RetrofitInstance.get().api.loadProductsByCategory(id)
                ApIData(Status.Success, response)
            } catch (ex: Exception) {
                // Handle exceptions and log the error
                Log.d("Error", "${ex.message}")
                ApIData(Status.Failed, null)
            }

            // Process outside the background (update LiveData)
            withContext(Dispatchers.Main.immediate) {
                _productByCategory.value = apiData
            }
        }
    }
}
