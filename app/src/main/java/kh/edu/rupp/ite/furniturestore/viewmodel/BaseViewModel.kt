package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

open class BaseViewModel : ViewModel() {

    /**
     * A generic function to perform API calls asynchronously and handle the response.
     *
     * @param resData LiveData to update with the API response.
     * @param request A suspend function representing the API call.
     * @param successBlock A block to execute when the response is successful.
     * @param failureBlock A block to execute when the response is an error.
     */
    fun <T> performApiCall(
        resData: MutableLiveData<ApiData<T>>,
        request: suspend () -> Response<T>,
        successBlock: (T) -> ApiData<T>,
        failureBlock: (Response<T>) -> ApiData<T>
    ) {
        // Create an initial AuthApiData with Processing status
        var responseData: ApiData<T> = ApiData(Status.Processing, null)

        // Set the initial value in the LiveData
        resData.value = responseData

        // Launch a coroutine in the IO dispatcher
        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                // Make the API call
                val response = request.invoke()

                // Check the HTTP status code of the response
                when (response.code()) {
                    200 -> successBlock(response.body()!!)
                    201 -> successBlock(response.body()!!)
                    else -> failureBlock(response)
                }
            } catch (e: Exception) {
                // Handle exceptions, e.g., network issues
                Log.e("BaseViewModel", "${e.message}")
                ApiData(Status.Failed, null)
            }

            withContext(Dispatchers.Main.immediate) {
                resData.value = responseData
            }
        }
    }
}
