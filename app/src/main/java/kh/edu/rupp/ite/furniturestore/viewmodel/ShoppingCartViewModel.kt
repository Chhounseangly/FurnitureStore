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
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    //Retrieve products of shopping cart
    fun loadProductsCartData() {
        RetrofitInstance.get().api.loadShoppingCartUnPaid()
            .enqueue(object : Callback<Res<ShoppingCart>> {
                override fun onResponse(
                    call: Call<Res<ShoppingCart>>,
                    response: Response<Res<ShoppingCart>>
                ) {
                    val responseData = response.body()
                    if (responseData != null) {
                        val apiData = ApIData(response.code(), responseData.data)
                        _shoppingCartItems.value = apiData
                    } else {
                        println("Response data is null")
                    }
                }
                override fun onFailure(call: Call<Res<ShoppingCart>>, t: Throwable) {
                    println("Failure: ${t.message}")
                }
            })
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
            }else{
                addProductToCartApi(productId)
                loadProductsCartData()
            }
        }else
            addProductToCartApi(productId)
            loadProductsCartData()
    }

     private fun addProductToCartApi(productId: Int) {
        RetrofitInstance.get().api.addProductToShoppingCart(AddProductToShoppingCart(productId))
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("Result", responseData.message)
                    } else {
                        println("Response data is null")
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    println("Failure: ${t.message}")
                }
            })
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
        RetrofitInstance.get().api.qtyOperation(productId, BodyPutData(qty))
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    val responseData = response.body()
                    if (responseData != null) {
                        Log.d("Result", responseData.message)
                    } else {
                        println("Response data is null")
                    }
                }
                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    println("Failure: ${t.message}")
                }
            })
    }

}