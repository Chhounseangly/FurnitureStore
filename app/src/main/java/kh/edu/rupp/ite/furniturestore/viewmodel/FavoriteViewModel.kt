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

class FavoriteViewModel: ViewModel() {
    // LiveData to observe products data changes
    private val _productsData = MutableLiveData<ApIData<List<Product>>>()
    val productsData: LiveData<ApIData<List<Product>>>
        get() = _productsData

    fun loadFavoriteProducts(){
        Log.e("FavoriteViewModel", "loadFavoriteProducts")
        var apiData = ApIData<List<Product>>(Status.Processing,null)
        _productsData.postValue(apiData)

        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                val response = RetrofitInstance.get().api.loadFavorite()
                Log.e("FavoriteViewModel", "success")
                ApIData(Status.Success, response.data)
            } catch (ex: Exception) {
                Log.e("FavoriteViewModel", "failed")
                ApIData(Status.Failed, null)
            }
            withContext(Dispatchers.Main.immediate) {
                _productsData.postValue(apiData)
            }
        }
    }

    fun toggleFavorite(product: Product, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.get().api.toggleFavorite(AddProductToShoppingCart(product.id))
                val apiData = response.data
                callback(apiData)
            } catch (ex: Exception) {
                ex.message?.let { Log.d("FavoriteViewModel", it) }
                callback(false)
            }

            // Performing UI-related operations outside the background thread
            withContext(Dispatchers.Main.immediate) {
                // Reloading the list of favorite products
                loadFavoriteProducts()
            }
        }
    }
}