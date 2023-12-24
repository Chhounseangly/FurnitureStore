package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel : BaseViewModel() {

    // LiveData to observe changes in the list of favorite products
    private val _productsData = MutableLiveData<ApiData<Res<List<Product>>>>()
    val productsData: LiveData<ApiData<Res<List<Product>>>>
        get() = _productsData

    // Function to load the list of favorite products
    fun loadFavoriteProducts() {
        Log.d("FavoriteViewModel", "loadFavoriteProducts")
        performApiCall(_productsData, { RetrofitInstance.get().api.loadFavorite() })
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
                ex.message?.let { Log.e("FavoriteViewModel", it) }
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
