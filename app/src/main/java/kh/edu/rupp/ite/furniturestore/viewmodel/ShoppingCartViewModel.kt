package kh.edu.rupp.ite.furniturestore.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.BodyPutData
import kh.edu.rupp.ite.furniturestore.model.api.model.ObjectPayment
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoppingCartViewModel : ViewModel() {

    // Create list for Shopping Cart items
    private val _shoppingCartItems = MutableLiveData<ApIData<List<ShoppingCart>>>()
    private val _tempDataList = mutableListOf<ShoppingCart>()
    private val _itemCount = MutableLiveData<Int>()
    private val _totalPrice = MutableLiveData(0.00)
    private var _toastMessage: String? = null
    val toastMessage get() = _toastMessage!!


    // LiveData to hold Shopping Cart Items.
    val shoppingCartItems: LiveData<ApIData<List<ShoppingCart>>> get() = _shoppingCartItems
    val tempDataList: LiveData<List<ShoppingCart>> get() = MutableLiveData(_tempDataList)

    val itemCount: LiveData<Int> get() = _itemCount
    val totalPrice: LiveData<Double> get() = _totalPrice
//    val toastMessage: LiveData<String> get() = _toastMessage
    private val _responseMessage = MutableLiveData<ApIData<ResponseMessage>>()

    val responseMessage: LiveData<ApIData<ResponseMessage>>
        get() = _responseMessage


    // Calculate Total Price based on the items in the shopping cart
    fun calculateTotalPrice(shoppingCart: List<ShoppingCart>) {
        var total = 0.00
        var items = 0
        for (item in shoppingCart) {
            total += item.product.price * item.qty
            items += item.qty
        }
        _totalPrice.value = total
        _itemCount.value = items
    }

    // Function to execute quantity updates to the API
    fun executingQtyToApi() {
        if (tempDataList.value?.isNotEmpty() == true) {
            val list = mutableListOf<BodyPutData>()
            for (i in tempDataList.value!!) {
                list.add(BodyPutData(i.product_id, i.qty))
            }
            Handler(Looper.getMainLooper()).postDelayed({
                qtyOperationApi(list)
            }, 4000)
        }
    }

    // Function to add a product to the shopping cart
    @SuppressLint("SuspiciousIndentation")
    fun addProductToShoppingCart(productId: Int) {
        if (_shoppingCartItems.value?.data?.isNotEmpty() == true) {
            val existed = shoppingCartItems.value?.data?.find { it.product_id == productId }
            if (existed != null) {
                _toastMessage = "Product existed on shopping cart"
            }
            else addProductToCartApi(productId)

        } else addProductToCartApi(productId)
    }

    // Add product to the shopping cart via API
    private fun addProductToCartApi(productId: Int) {
        // Processing as background
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Call API to add a product to the shopping cart
                RetrofitInstance.get().api.addProductToShoppingCart(AddProductToShoppingCart(productId))
                _toastMessage = ("Added to shopping cart")
            } catch (ex: Exception) {
                Log.e("error", "${ex.message}")
            }

            // Process outside background
            withContext(Dispatchers.Main.immediate) {
                loadProductsCartData()
            }
        }
    }

    // Retrieve products in the shopping cart
    fun loadProductsCartData() {
        var apiData = ApIData<List<ShoppingCart>>(Status.Processing, null)
        _shoppingCartItems.postValue(apiData)

        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                // Call API to load products in the shopping cart
                val response = RetrofitInstance.get().api.loadShoppingCartUnPaid()
                ApIData(Status.Success, response.data)
            } catch (ex: Exception) {
                Log.e("error", "${ex.message}")
                ApIData(Status.Failed, null)
            }

            // Process outside background
            withContext(Dispatchers.Main.immediate) {
                _shoppingCartItems.postValue(apiData)
            }
        }
    }

    // Perform quantity operation (increase/decrease) on a shopping cart item
    fun qtyOperation(item: ShoppingCart, operation: String) {
        val existingItem = shoppingCartItems.value?.data?.find { it.id == item.id }
        if (existingItem != null) {
            when (operation) {
                "increaseQty" -> {
                    existingItem.qty++
                }

                "decreaseQty" -> {
                    if (existingItem.qty > 1) {
                        existingItem.qty--
                    }
                }
            }
            updateTotalPrice()
            updateTempDataList(existingItem)
        }
    }

    // Update tempDataList with the latest quantity changes
    private fun updateTempDataList(existingItem: ShoppingCart) {
        if (_tempDataList.isNotEmpty()) {
            val found = _tempDataList.find { it.id == existingItem.id }
            if (found != null) {
                found.qty = existingItem.qty
            } else {
                _tempDataList.addAll(listOf(existingItem))
            }
        } else {
            _tempDataList.addAll(listOf(existingItem))
        }
    }

    // Update total price after quantity changes
    private fun updateTotalPrice() {
        var total = 0.00
        var items = 0
        for (item in shoppingCartItems.value?.data!!) {
            if (item.product != null) {
                total += item.product.price * item.qty
                items += item.qty
            }
        }
        _totalPrice.postValue(total)
        _itemCount.postValue(items)
    }

    // Perform quantity operation API call
    private fun qtyOperationApi(data: List<BodyPutData>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Call API to update quantities
                val response = RetrofitInstance.get().api.qtyOperation(data)
                Log.e("message", response.message)
                loadProductsCartData()
            } catch (ex: Exception) {
                Log.e("error", "${ex.message}")
            }
            // Process outside background
            withContext(Dispatchers.Main.immediate) {
                loadProductsCartData()
            }
        }
    }

    // Function to handle API call for deleting a product from the shopping cart
    fun deleteProductShoppingCart(productId: Int) {
        // Launching a coroutine in the IO dispatcher to perform background network operations
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Call API to delete a product from the shopping cart
                val response = RetrofitInstance.get().api.deleteProductShoppingCart(productId)
                // Logging the message from the response (you may want to handle this more gracefully)
                Log.e("Msg", response.message)
                // Reloading the shopping cart data after the product is successfully deleted
                loadProductsCartData()
            } catch (ex: Exception) {
                // Handling exceptions, logging the error message
                Log.e("error", "${ex.message}")
            }

            // Performing UI-related operations on the main thread after the background work is done
            withContext(Dispatchers.Main.immediate) {
                // Reloading the shopping cart data (this seems redundant, as it's already loaded in the try block)
                loadProductsCartData()
            }
        }
    }
}
