package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.ObjectPayment
import kh.edu.rupp.ite.furniturestore.model.api.model.PaymentModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentViewModel: ViewModel() {

    // LiveData to hold the response message from the payment API
    private val _responseMessage = MutableLiveData<ApIData<ResponseMessage>>()

    val responseMessage: LiveData<ApIData<ResponseMessage>>
        get() = _responseMessage

    // Function to initiate the payment process
    fun payment(data: List<ObjectPayment>) {
        // Convert data to a list of PaymentModel
        val list = mutableListOf<PaymentModel>()
        for (i in data) {
            list.add(PaymentModel(i.product_id, i.shopping_card_id))
        }

        // Initial status while processing payment
        var apiData = ApIData<ResponseMessage>(Status.Processing, null)
        _responseMessage.postValue(apiData)

        // Processing payment in the background
        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                // Make a payment request to the API
                RetrofitInstance.get().api.postPayment(list)
                ApIData(Status.Success, null)
            } catch (ex: Exception) {
                // Handle exceptions and set status to failed
                ex.printStackTrace()
                Log.e("failed", "${ex.message}")
                ApIData(Status.Failed, null)
            }

            // Process outside the background (update LiveData)
            withContext(Dispatchers.Main.immediate) {
                _responseMessage.postValue(apiData)
            }
        }
    }
}
