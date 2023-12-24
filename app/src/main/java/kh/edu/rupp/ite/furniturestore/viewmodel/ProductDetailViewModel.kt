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

class ProductDetailViewModel : BaseViewModel() {

    // LiveData to hold the product details
    private val _productsData = MutableLiveData<ApiData<Res<Product>>>()
    val productsData: LiveData<ApiData<Res<Product>>>
        get() = _productsData

    // Function to load product details by ID
    fun loadProductDetail(id: Int) {
        performApiCall(_productsData, { RetrofitInstance.get().api.loadProductDetail(id) })
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
