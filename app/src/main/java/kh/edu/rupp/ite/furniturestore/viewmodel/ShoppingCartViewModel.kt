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
    private val _toastMessage = MutableLiveData<String>()

    // LiveData to hold Shopping Cart Items.
    val shoppingCartItems: LiveData<ApIData<List<ShoppingCart>>> get() = _shoppingCartItems
    val tempDataList: LiveData<List<ShoppingCart>> get() = MutableLiveData(_tempDataList)
    val itemCount: LiveData<Int> get() = _itemCount
    val totalPrice: LiveData<Double> get() = _totalPrice
    val toastMessage: LiveData<String> get() = _toastMessage


    //calculate TotalPrice
    fun calculateTotalPrice(shoppingCart: List<ShoppingCart>) {
        var total = 0.00
        var items = 0
        for (item in shoppingCart) {
            if (item.product != null) {
                total += item.product.price * item.qty
                items += item.qty
            }
        }
        _totalPrice.value = total
        _itemCount.value = items
    }


    //Function iteration tempData summit to api
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

    //handle add product to Shopping Cart
    @SuppressLint("SuspiciousIndentation")
    fun addProductToShoppingCart(productId: Int) {
        if (_shoppingCartItems.value?.data?.isNotEmpty() == true) {
            val existed = shoppingCartItems.value?.data?.find { it.product_id == productId }
            if (existed != null) _toastMessage.postValue("Product existed on shopping cart")
            else addProductToCartApi(productId)

        } else addProductToCartApi(productId)
    }

    private fun addProductToCartApi(productId: Int) {
        //processing as background
        viewModelScope.launch(Dispatchers.IO) {
            try {
                RetrofitInstance.get().api.addProductToShoppingCart(AddProductToShoppingCart(productId))
                _toastMessage.postValue("Added to shopping cart")
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
        _shoppingCartItems.postValue(apiData)

        viewModelScope.launch(Dispatchers.IO) {
            apiData = try {
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

    //handle Summit Qty to api
    private fun qtyOperationApi(data: List<BodyPutData>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.get().api.qtyOperation(data)
                Log.e("message", response.message)
                loadProductsCartData()
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