package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.PaymentModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentViewModel: ViewModel() {
    private val _responseMessage = MutableLiveData<ApIData<ResponseMessage>>()

    val responseMessage: LiveData<ApIData<ResponseMessage>>
        get() = _responseMessage


    fun payment(body: List<PaymentModel>){
        var apiData = ApIData<ResponseMessage>(Status.Processing, null)
        _responseMessage.postValue(apiData)
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                RetrofitInstance.get().api.postPayment(body)
                ApIData(Status.Success, null)
            }catch (ex: Exception){
                ex.printStackTrace()
                Log.e("failed", "${ex.message}")
                ApIData(Status.Failed, null)
            }

            withContext(Dispatchers.Main.immediate){
                _responseMessage.postValue(apiData)
            }
        }

    }
}