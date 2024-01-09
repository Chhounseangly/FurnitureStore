package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
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

    private val _qtySumUpProducts = MutableLiveData<Int>()
    val qtySumUpProducts: LiveData<Int>
        get() = _qtySumUpProducts

    fun loadHistoryData(){
        performApiCall(_historyData, {
            RetrofitInstance.get().api.loadHistoryPurchase()
        })
    }

    fun qtySumUp(){
        var sumUpQty = 0
        historyData.observeForever { apiData ->
            // Check if data is not null before accessing it
            if (apiData?.data != null) {
                for (i in apiData.data.data){
                    sumUpQty += i.qty
                }
            }
            _qtySumUpProducts.postValue(sumUpQty)
        }
    }


}