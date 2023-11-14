package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductListViewModel: ViewModel() {
    private val _productsData = MutableLiveData<ApIData<List<Product>>>()
    val productsData: LiveData<ApIData<List<Product>>>
        get() = _productsData

    fun loadProductsData(){
        var apiData = ApIData<List<Product>>(102,null) //status 102 is processing
        _productsData.postValue(apiData)

        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                val response = RetrofitInstance.get().api.loadProductList()
                 ApIData(200, response.data)
            } catch (ex: Exception) {
                ApIData(204, null)
            }
            withContext(Dispatchers.Main.immediate) {
                _productsData.postValue(apiData)
            }
        }
    }
}