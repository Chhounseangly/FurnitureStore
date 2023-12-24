package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance

class SearchViewModel : BaseViewModel() {

    // LiveData to observe changes in the search results
    private val _data = MutableLiveData<ApiData<Res<List<Product>>>>()

    val data: LiveData<ApiData<Res<List<Product>>>>
        get() = _data

    // Function to perform a search based on the product name
    fun search(name: String) {
        performApiCall(_data, { RetrofitInstance.get().api.searchProductByName(name) })
    }
}
