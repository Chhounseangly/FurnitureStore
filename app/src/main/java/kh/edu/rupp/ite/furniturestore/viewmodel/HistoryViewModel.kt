package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.HistoryModel
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance

class HistoryViewModel: BaseViewModel() {

    private val _historyData = MutableLiveData<ApiData<Res<List<HistoryModel>>>>()

    val historyData: LiveData<ApiData<Res<List<HistoryModel>>>>
        get() = _historyData


    fun loadHistoryData(){
        performApiCall(_historyData, { RetrofitInstance.get().api.loadHistoryPurchase() })
    }
}