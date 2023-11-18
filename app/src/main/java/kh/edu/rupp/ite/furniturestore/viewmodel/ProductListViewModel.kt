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

class ProductListViewModel: ViewModel() {
    private val _productsData = MutableLiveData<ApIData<List<Product>>>()
    val productsData: LiveData<ApIData<List<Product>>>
        get() = _productsData

    fun loadProductsData(){
        var apiData = ApIData<List<Product>>(Status.Processing,null)
        _productsData.postValue(apiData)

        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                val response = RetrofitInstance.get().api.loadProductList()
                 ApIData(Status.Success, response.data)
            } catch (ex: Exception) {
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
                ex.message?.let { Log.d("ProductListVM", it) }
                callback(false)
            }
        }
    }
}