package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance

class FavoriteViewModel : BaseViewModel() {

    // LiveData to observe changes in the list of favorite products
    private val _productsData = MutableLiveData<ApiData<Res<List<Product>>>>()
    val productsData: LiveData<ApiData<Res<List<Product>>>>
        get() = _productsData

    // Function to load the list of favorite products
    fun loadFavoriteProducts() {
        Log.d("FavoriteViewModel", "loadFavoriteProducts")
        performApiCall(_productsData, { RetrofitInstance.get().api.loadFavorite() })
    }

    // Function to toggle the favorite status of a product
    fun toggleFavorite(
        product: Product,
        callback: (Boolean) -> Unit = {}
    ) {
        performApiCall(
            request = { RetrofitInstance.get().api.toggleFavorite(AddProductToShoppingCart(product.id)) },
            successBlock = { result ->
                callback(result.data)
                ApiData(Status.Success, null)
            },
            failureBlock = {
                callback(false)
                ApiData(Status.Failed, null)
            },
            reloadData = {
                loadFavoriteProducts()
            }
        )
    }
}
