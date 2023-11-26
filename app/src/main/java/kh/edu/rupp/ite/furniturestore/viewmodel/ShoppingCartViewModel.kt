package kh.edu.rupp.ite.furniturestore.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.BodyPutData
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ShoppingCartViewModel : ViewModel() {

    // Create list for Shopping Cart items
    private val _shoppingCartItems = MutableLiveData<ApIData<List<ShoppingCart>>>()
    private val _tempDataList = mutableListOf<ShoppingCart>()
    private val _totalPrice = MutableLiveData(0.00)
    private val _toastMessage = MutableLiveData<String>()

    // LiveData to hold Shopping Cart Items.
    val shoppingCartItems: LiveData<ApIData<List<ShoppingCart>>> get() = _shoppingCartItems
    val tempDataList: LiveData<List<ShoppingCart>> get() = MutableLiveData(_tempDataList)
    val totalPrice: LiveData<Double> get() = _totalPrice
    val toastMessage: LiveData<String> get() = _toastMessage


    //calculate TotalPrice
    fun calculateTotalPrice(shoppingCart: List<ShoppingCart>) {
        var total = 0.00
        for (item in shoppingCart) {
            if (item.product != null) {
                total += item.product.price * item.qty
            }
        }
        _totalPrice.value = total
    }


    //Function iteration tempData summit to api
    fun executingQtyToApi() {
        if (tempDataList.value?.isNotEmpty() == true) {
            runBlocking {
                val iterator = _tempDataList.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    delay(500) //submit to api 500s per value
                    qtyOperationApi(item.id, item.qty)
                    iterator.remove() // Remove the item after the operation is done
                }
            }
        }
    }

    //handle add product to Shopping Cart
    @SuppressLint("SuspiciousIndentation")
    fun addProductToShoppingCart(productId: Int) {
        if (_shoppingCartItems.value?.data?.isNotEmpty() == true) {
            val existed = shoppingCartItems.value?.data?.find { it.product_id == productId }
            if (existed != null) {
                _toastMessage.postValue("Product existed on shopping cart")
            } else {
                addProductToCartApi(productId)
            }
        } else addProductToCartApi(productId)
    }

    private fun addProductToCartApi(productId: Int) {
        //processing as background
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.get().api.addProductToShoppingCart(
                        AddProductToShoppingCart(productId)
                )
                loadProductsCartData()
                Log.e("message", response.message)
            } catch (ex: Exception) {
                Log.e("error", "${ex.message}")
            }

            //process outside background
            withContext(Dispatchers.Main.immediate) {
                loadProductsCartData()
            }
        }
    }

    //Retrieve products of shopping cart
    fun loadProductsCartData() {
        var apiData = ApIData<List<ShoppingCart>>(Status.Processing, null)
        _shoppingCartItems.value = apiData

        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
                val response = RetrofitInstance.get().api.loadShoppingCartUnPaid()
                ApIData(Status.Success, response.data)

            } catch (ex: Exception) {
                Log.e("error", "${ex.message}")
                ApIData(Status.Failed, null)
            }
            //process outside background
            withContext(Dispatchers.Main.immediate) {
                _shoppingCartItems.postValue(apiData)
            }
        }
    }

    //operation of qty
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

    // handle check update qty and store tempDataList
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

    //update total when increase or decrease qty in shopping cart
    private fun updateTotalPrice() {
        var total = 0.00
        for (item in shoppingCartItems.value?.data!!) {
            if (item.product != null) {
                total += item.product.price * item.qty
            }
        }
        _totalPrice.postValue(total)
    }

    //handle Summit Qty to api
    private fun qtyOperationApi(productId: Int, qty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.get().api.qtyOperation(productId, BodyPutData(qty))
                Log.e("message", response.message)

            } catch (ex: Exception) {
                Log.e("error", "${ex.message}")
            }
            //process outside background
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
                // Making the API call to delete the product from the shopping cart using Retrofit
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