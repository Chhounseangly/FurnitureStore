package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductListViewModel: ViewModel() {
    private val _productsData = MutableLiveData<ApIData<List<Product>>>()
    val productsData: LiveData<ApIData<List<Product>>>
        get() = _productsData

    fun loadProductsData(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.get().api.loadProductList()
                if (response != null) {
                    val apiData = ApIData(200, response.data)
                    _productsData.postValue(apiData)
                }
                else println("Response data is null")
            } catch (ex: Exception) {
                println("Failure: ${ex.message}")
            }

            withContext(Dispatchers.Main.immediate) {

            }
        }
    }
}