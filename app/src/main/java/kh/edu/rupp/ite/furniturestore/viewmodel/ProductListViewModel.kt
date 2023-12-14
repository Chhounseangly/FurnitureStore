package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductListViewModel : ViewModel() {

    // LiveData to observe changes in the list of products
    private val _productsData = MutableLiveData<ApIData<List<Product>>>()
    val productsData: LiveData<ApIData<List<Product>>>
        get() = _productsData

    // Function to load the list of products
    fun loadProductsData() {
        // Initial status while processing
        var apiData = ApIData<List<Product>>(Status.Processing, null)
        _productsData.postValue(apiData)

        // Processing in the background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                // Fetch the list of products from the API
                val response = RetrofitInstance.get().api.loadProductList()
                ApIData(Status.Success, response.data)
            } catch (ex: Exception) {
                // Handle exceptions and set status to failed
                ApIData(Status.Failed, null)
            }

            // Process outside the background (update LiveData)
            withContext(Dispatchers.Main.immediate) {
                _productsData.postValue(apiData)
            }
        }
    }
}
