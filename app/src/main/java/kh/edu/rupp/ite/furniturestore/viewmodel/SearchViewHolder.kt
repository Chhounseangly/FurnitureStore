package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewHolder : ViewModel() {

    // LiveData to observe changes in the search results
    private val _data = MutableLiveData<ApiData<List<Product>>>()

    val data: LiveData<ApiData<List<Product>>>
        get() = _data

    // Function to perform a search based on the product name
    fun search(name: String) {
        // Initial status while processing
        var apiData = ApiData<List<Product>>(Status.Processing, null)
        _data.postValue(apiData)

        // Processing in the background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                // Perform a product search by name through the API
                val response = RetrofitInstance.get().api.searchProductByName(name)
                ApiData(Status.Success, response.data)
            } catch (ex: Exception) {
                // Handle exceptions and set status to failed
                ApiData(Status.Failed, null)
            }

            // Process outside the background (update LiveData)
            withContext(Dispatchers.Main.immediate) {
                _data.postValue(apiData)
            }
        }
    }
}
