package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchViewHolder : ViewModel() {

    private val _data = MutableLiveData<ApIData<List<Product>>>()

    val data: LiveData<ApIData<List<Product>>>
        get() = _data


    fun search(name: String) {
        var apiData = ApIData<List<Product>>(102,null) //status 102 is processing
        _data.postValue(apiData)

        viewModelScope.launch(Dispatchers.IO) {
            //processing as background
             apiData = try {
                 val response = RetrofitInstance.get().api.searchProductByName(name)
                 ApIData(200, response.data)
             } catch (ex: Exception) {
                ApIData(204, null)
            }
            //process outside background
            withContext(Dispatchers.Main.immediate) {
                _data.postValue(apiData)
            }
        }
    }
}