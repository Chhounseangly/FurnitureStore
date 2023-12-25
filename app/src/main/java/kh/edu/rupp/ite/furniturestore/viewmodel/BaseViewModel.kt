package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

open class BaseViewModel : ViewModel() {

    /**
     * A generic function to perform API calls asynchronously and handle the response.
     *
     * @param response LiveData to update with the API response.
     * @param request A suspend function representing the API call.
     * @param successBlock A block to execute when the response is successful.
     * @param failureBlock A block to execute when the response is an error.
     */
    inline fun <reified T> performApiCall(
        response: MutableLiveData<ApiData<T>>,
        crossinline request: suspend () -> Response<T>,
        crossinline successBlock: (T) -> ApiData<T> = { data -> ApiData(Status.Success, data) },
        crossinline failureBlock: (Response<T>) -> ApiData<T> = { ApiData(Status.Failed, null) }
    ) {
        // Initial response data with Processing status
        var responseData: ApiData<T>

        // Check if the response LiveData already has a value
        response.value?.let {
            // If yes, set LoadingMore status and update the LiveData
            responseData = ApiData(Status.LoadingMore, it.data)
            response.postValue(responseData)
        } ?: response.postValue(ApiData(Status.Processing, null)) // If no, set Processing status

        // Launch a coroutine in the IO dispatcher
        viewModelScope.launch(Dispatchers.IO) {
            responseData = try {
                // Make the API call
                val res = request.invoke()

                // Check the HTTP status code of the response
                when (res.code()) {
                    in 200..204 -> {
                        Log.d("BaseViewModel", "Success: ${res.body()}")
                        successBlock(res.body()!!)
                    }

                    401 -> failureBlock(res)
                    403 -> ApiData(Status.NeedVerify, null)

                    in 400..499 -> {
                        val errorBody = res.errorBody()?.string() ?: "Unknown error"
                        val errorResAuth = Gson().fromJson(errorBody, T::class.java)

                        Log.e("BaseViewModel", "Error: $errorBody")
                        ApiData(Status.Failed, errorResAuth)
                    }

                    else -> {
                        Log.e("BaseViewModel", "Error: ${res.errorBody()?.string()}")
                        failureBlock(res)
                    }
                }
            } catch (e: Exception) {
                Log.e("BaseViewModel", "Error: ${e.message}")
                ApiData(Status.Failed, null)
            }

            withContext(Dispatchers.Main.immediate) {
                response.value = responseData
            }
        }
    }

    // Function to toggle the favorite status of a product
    fun toggleFavorite(
        product: Product,
        callback: (Boolean) -> Unit = {},
        reloadFavorites: () -> Unit = {}
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Toggle favorite status through API
                val response =
                    RetrofitInstance.get().api.toggleFavorite(AddProductToShoppingCart(product.id))
                val apiData = response.data
                callback(apiData)
            } catch (ex: Exception) {
                // Handle exceptions and log the error
                ex.message?.let { Log.e("FavoriteViewModel", it) }
                callback(false)
            }

            // Performing UI-related operations outside the background thread
            withContext(Dispatchers.Main.immediate) {
                // Reloading the list of favorite products after toggling
                reloadFavorites()
            }
        }
    }
}
