package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.ObjectPayment
import kh.edu.rupp.ite.furniturestore.model.api.model.PaymentModel
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance

class PaymentViewModel : BaseViewModel() {

    // LiveData to hold the response message from the payment API
    private val _resMessage = MutableLiveData<ApiData<Res<String>>>()

    val resMessage: LiveData<ApiData<Res<String>>>
        get() = _resMessage

    // Function to initiate the payment process
    fun payment(data: List<ObjectPayment>) {
        // Convert data to a list of PaymentModel
        val list = mutableListOf<PaymentModel>()
        for (i in data) {
            list.add(PaymentModel(i.product_id, i.shopping_card_id))
        }
        performApiCall(_resMessage, { RetrofitInstance.get().api.postPayment(list) })
    }
}
