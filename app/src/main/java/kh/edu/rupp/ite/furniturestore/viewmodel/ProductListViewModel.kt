package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance

class ProductListViewModel : BaseViewModel() {

    // LiveData to observe changes in the list of products
    private val _productsData = MutableLiveData<ApiData<Res<List<Product>>>>()
    val productsData: LiveData<ApiData<Res<List<Product>>>> get() = _productsData

    // Function to load the list of products
    fun loadProductsData() {
        performApiCall(_productsData, { RetrofitInstance.get().api.loadProductList() })
    }

    fun loadMoreProductsData(page: Int) {
        performApiCall(
            response = _productsData,
            request = { RetrofitInstance.get().api.loadProductList(page) },
            successBlock = { response ->
                val newData = response.data
                val currentData = _productsData.value?.data?.data
                val updatedData = currentData?.toMutableList()?.apply {
                    addAll(newData)
                } ?: newData

                ApiData(
                    Status.Success,
                    Res(response.message, updatedData, response.meta),
                    response.meta
                )
            }
        )
    }
}
