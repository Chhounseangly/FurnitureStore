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

class ProductDetailViewModel : ViewModel() {
    private val _productsData = MutableLiveData<ApIData<Product>>()
    val productsData: LiveData<ApIData<Product>>
        get() = _productsData

    fun loadProductDetail(id: Int) {
        var apiData = ApIData<Product>(Status.Processing, null) //status 102 is processing
        _productsData.value = apiData
        //processing as background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                val response = RetrofitInstance.get().api.loadProductDetail(id)
                ApIData(Status.Success, response.data)
            } catch (ex: Exception) {
                Log.e("error", "${ex.message}")
                ApIData(Status.Failed, null)
            }
            //process outside background
            withContext(Dispatchers.Main.immediate) {
                _productsData.value = apiData
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
                ex.message?.let { Log.d("ProductDetailVM", it) }
                callback(false)
            }
        }
    }
}