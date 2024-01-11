package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.HistoryModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductIdModel
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel: BaseViewModel() {

    private val _historyData = MutableLiveData<ApiData<Res<List<HistoryModel>>>>()

    val historyData: LiveData<ApiData<Res<List<HistoryModel>>>>
        get() = _historyData

    private val _qtySumUpProducts = MutableLiveData<Int>()
    val qtySumUpProducts: LiveData<Int>
        get() = _qtySumUpProducts

    // LiveData to hold the response message from the payment API
    private val _resMessage = MutableLiveData<ApiData<Res<String>>>()

    val resMessage: LiveData<ApiData<Res<String>>>
        get() = _resMessage

    fun loadHistoryData(){
        performApiCall(_historyData, {
            RetrofitInstance.get().api.loadHistoryPurchase()
        })
    }

    fun deleteProductFromHis(data : List<ProductIdModel>){
        performApiCall(_resMessage,  {
            RetrofitInstance.get().api.deleteProductFromHistory(data)
        })
//        performApiCall(
//            request = {
//
//            },
//            successBlock = { result ->
//                ApiData(Status.Success, null)
//            },
//            failureBlock = {
//                ApiData(Status.Failed, null)
//            },
//            reloadData = {
//                loadHistoryData()
//            }
//        )
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