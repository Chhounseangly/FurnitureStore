package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailViewModel : ViewModel() {

    // LiveData to hold the product details
    private val _productsData = MutableLiveData<ApiData<Product>>()
    val productsData: LiveData<ApiData<Product>>
        get() = _productsData

    // Function to load product details by ID
    fun loadProductDetail(id: Int) {
        // Initial status while processing
        var apiData = ApiData<Product>(Status.Processing, null)
        _productsData.value = apiData

        // Processing in the background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                // Fetch product details from the API
                val response = RetrofitInstance.get().api.loadProductDetail(id)
                ApiData(Status.Success, response.data)
            } catch (ex: Exception) {
                // Handle exceptions and set status to failed
                Log.e("error", "${ex.message}")
                ApiData(Status.Failed, null)
            }

            // Process outside the background (update LiveData)
            withContext(Dispatchers.Main.immediate) {
                _productsData.value = apiData
            }
        }
    }

    // Function to toggle favorite status of a product
    fun toggleFavorite(product: Product, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Toggle favorite status through API
                val response = RetrofitInstance.get().api.toggleFavorite(AddProductToShoppingCart(product.id))
                val apiData = response.data
                callback(apiData)
            } catch (ex: Exception) {
                // Handle exceptions and log the error
                ex.message?.let { Log.d("ProductDetailVM", it) }
                callback(false)
            }
        }
    }
}
