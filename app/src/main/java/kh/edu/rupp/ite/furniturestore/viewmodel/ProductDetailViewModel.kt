package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance

class ProductDetailViewModel : BaseViewModel() {

    // LiveData to hold the product details
    private val _productsData = MutableLiveData<ApiData<Res<Product>>>()
    val productsData: LiveData<ApiData<Res<Product>>>
        get() = _productsData

    // Function to load product details by ID
    fun loadProductDetail(id: Int) {
        performApiCall(_productsData, { RetrofitInstance.get().api.loadProductDetail(id) })
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
            }
        )
    }
}
