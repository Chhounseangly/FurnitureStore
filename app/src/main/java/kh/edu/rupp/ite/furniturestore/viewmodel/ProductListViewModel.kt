package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductListViewModel : BaseViewModel() {

    // LiveData to observe changes in the list of products
    private val _productsData = MutableLiveData<ApiData<List<Product>>>()
    val productsData: LiveData<ApiData<List<Product>>>
        get() = _productsData

    // Function to load the list of products
    fun loadProductsData() {
        // Initial status while processing
        var apiData = ApiData<List<Product>>(Status.Processing, null)
        _productsData.postValue(apiData)

        // Processing in the background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                // Fetch the list of products from the API
                val response = RetrofitInstance.get().api.loadProductList()
                ApiData(Status.Success, response.data, response.meta)
            } catch (ex: Exception) {
                // Handle exceptions and set status to failed
                ApiData(Status.Failed, null, null)
            }

            // Process outside the background (update LiveData)
            withContext(Dispatchers.Main.immediate) {
                _productsData.postValue(apiData)
            }
        }
    }

    // Function to load more products
    fun loadMoreProductsData(page: Int) {
        // Initial status while processing
        var apiData = ApiData(Status.LoadingMore, productsData.value?.data)
        _productsData.postValue(apiData)

        // Processing in the background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                // Fetch the list of products from the API
                val response = RetrofitInstance.get().api.loadProductList(page)
                val newData = response.data

                // If it's an initial load or the page is greater than 1, append the new data
                val updatedData = _productsData.value?.data?.toMutableList().apply {
                    this?.addAll(newData)
                }

                ApiData(Status.Success, updatedData, response.meta)
            } catch (ex: Exception) {
                // Handle exceptions and set status to failed
                ApiData(Status.Failed, productsData.value?.data)
            }

            // Process outside the background (update LiveData)
            withContext(Dispatchers.Main.immediate) {
                _productsData.postValue(apiData)
            }
        }
    }
}
