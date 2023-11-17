package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductDetail
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailViewModel : ViewModel() {
    private val _productsData = MutableLiveData<ApIData<Product>>()
    val productsData: LiveData<ApIData<Product>>
        get() = _productsData

    fun loadProductDetail(id: Int) {
        var apiData = ApIData<Product>(102, null) //status 102 is processing
        _productsData.value = apiData
        //processing as background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                val response = RetrofitInstance.get().api.loadProductDetail(id)
                ApIData(200, response.data)
            } catch (ex: Exception) {
                Log.e("error", "${ex.message}")
                ApIData(204, null)
            }
            //process outside background
            withContext(Dispatchers.Main.immediate) {
                _productsData.value = apiData
            }
        }

    }
}