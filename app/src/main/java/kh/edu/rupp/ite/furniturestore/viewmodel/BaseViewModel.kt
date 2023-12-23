package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

open class BaseViewModel<T> : ViewModel() {

    private val _resData = MutableLiveData<ApIData<T>>()
    val resData: LiveData<ApIData<T>> get() = _resData

    /**
     * A generic function to perform API calls asynchronously and handle the response.
     *
     * @param request A suspend function representing the API call.
     * @param successBlock A block to execute when the response is successful.
     * @param failureBlock A block to execute when the response is an error.
     */
    fun performApiCall(
        request: suspend () -> Response<T>,
        successBlock: (T) -> ApIData<T>,
        failureBlock: (Response<T>) -> ApIData<T>
    ) {
        // Create an initial AuthApiData with Processing status
        var responseData: ApIData<T> = ApIData(Status.Processing, null)

        // Set the initial value in the ViewModel
        _resData.value = responseData

        // Launch a coroutine in the IO dispatcher
        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                // Make the API call
                val response = request.invoke()

                // Check the HTTP status code of the response
                when (response.code()) {
                    200 -> successBlock(response.body()!!)
                    else -> failureBlock(response)
                }
            } catch (e: Exception) {
                // Handle exceptions, e.g., network issues
                Log.e("BaseViewModel", "${e.message}")
                // Set failure status in case of an exception
                ApIData(Status.Failed, null)
            }

            withContext(Dispatchers.Main.immediate) {
                _resData.value = responseData
            }
        }
    }
}
