package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList
import kh.edu.rupp.ite.furniturestore.model.api.model.Status

class ProductListViewModel: ViewModel() {

    private val _productsData = MutableLiveData<ApIData<List<ProductList>>>()
    val productsData: LiveData<ApIData<List<ProductList>>>
        get() = _productsData


    fun loadProductsData(){

        val apiData = ApIData<List<ProductList>>(Status.SUCCESS, listOf(
            ProductList(
                1,
                "Table",
                "https://i4.komnit.com/store/upload/images/express_2207/112290-ARJDYN/1657316942-ARJDYN.jpg",
                50,
                1
            ),
            ProductList(
                1,
                "Table",
                "https://i4.komnit.com/store/upload/images/express_2207/112290-ARJDYN/1657316942-ARJDYN.jpg",
                50,
                1
            ),
        ))
        _productsData.postValue(apiData)

    }




}