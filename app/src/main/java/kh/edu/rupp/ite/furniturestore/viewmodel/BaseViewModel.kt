package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

open class BaseViewModel : ViewModel() {

    private val _resData = MutableLiveData<ApIData<ResponseMessage>>()
    val resData: LiveData<ApIData<ResponseMessage>> get() = _resData

    /**
     * A generic function to perform API calls asynchronously and handle the response.
     *
     * @param T The type of the response body.
     * @param request A suspend function representing the API call.
     * @param successBlock A block to execute when the response is successful.
     */
    fun <T> performApiCall(
        request: suspend () -> Response<T>,
        successBlock: (T) -> ApIData<ResponseMessage>,
    ) {
        // Create an initial AuthApiData with Processing status
        var responseData = ApIData<ResponseMessage>(Status.Processing, null)

        // Set the initial value in the ViewModel
        _resData.value = responseData

        // Launch a coroutine in the IO dispatcher
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Make the API call
                val response = request.invoke()

                // Check the HTTP status code of the response
                responseData = when (response.code()) {
                    200 -> successBlock(response.body()!!)
                    400 -> {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        val errorResAuth = Gson().fromJson(errorBody, ResponseMessage::class.java)

                        Log.e("BaseViewModel", "Update Error: $errorBody")
                        ApIData(Status.Failed, errorResAuth)
                    }
                    else -> {
                        Log.e("BaseViewModel", "Update Error")
                        ApIData(Status.Failed, null)
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions, e.g., network issues
                Log.e("BaseViewModel", "${e.message}")
                // Set failure status in case of an exception
                responseData = ApIData(Status.Failed, null)
            }

            withContext(Dispatchers.Main.immediate) {
                _resData.value = responseData
            }
        }
    }
}