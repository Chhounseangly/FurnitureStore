package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel : ViewModel() {

    // LiveData to observe changes in the list of favorite products
    private val _productsData = MutableLiveData<ApIData<List<Product>>>()
    val productsData: LiveData<ApIData<List<Product>>>
        get() = _productsData

    // Function to load the list of favorite products
    fun loadFavoriteProducts() {
        Log.e("FavoriteViewModel", "loadFavoriteProducts")
        // Initial status while processing
        var apiData = ApIData<List<Product>>(Status.Processing, null)
        _productsData.postValue(apiData)

        // Processing in the background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                // Fetch the list of favorite products from the API
                val response = RetrofitInstance.get().api.loadFavorite()
                Log.e("FavoriteViewModel", "success")
                ApIData(Status.Success, response.data)
            } catch (ex: Exception) {
                // Handle exceptions and set status to failed
                Log.e("FavoriteViewModel", "failed")
                ApIData(Status.Failed, null)
            }

            // Process outside the background (update LiveData)
            withContext(Dispatchers.Main.immediate) {
                _productsData.postValue(apiData)
            }
        }
    }

    // Function to toggle the favorite status of a product
    fun toggleFavorite(product: Product, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Toggle favorite status through API
                val response = RetrofitInstance.get().api.toggleFavorite(AddProductToShoppingCart(product.id))
                val apiData = response.data
                callback(apiData)
            } catch (ex: Exception) {
                // Handle exceptions and log the error
                ex.message?.let { Log.d("FavoriteViewModel", it) }
                callback(false)
            }

            // Performing UI-related operations outside the background thread
            withContext(Dispatchers.Main.immediate) {
                // Reloading the list of favorite products after toggling
                loadFavoriteProducts()
            }
        }
    }
}
