package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.BodyPutData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShoppingCartViewModel : ViewModel() {

    // Create list for Shopping Cart items
    private val _shoppingCartItems = MutableLiveData<ApIData<List<ShoppingCart>>>()
    private val _totalPrice = MutableLiveData(0.00)

    val totalPrice: LiveData<Double> get() = _totalPrice

    // LiveData to hold Shopping Cart Items. Exposing LiveData to the outside world
    val shoppingCartItems: LiveData<ApIData<List<ShoppingCart>>> get() = _shoppingCartItems

    // Add Item to Shopping Cart
    fun addItemToCart(product: Product) {
        _shoppingCartItems.let {
            if (!it.value?.data?.isEmpty()!!){
                for (items in it.value?.data!!) {
                    if (items.product_id == product.id) {
                        items.qty++
                        _totalPrice.value = _totalPrice.value?.plus(product.price)
                    } else {
                        addProductToCart(AddProductToShoppingCart(product.id))
                        loadProductsCartData()
                    }
                }
            } else {
                addProductToCart(AddProductToShoppingCart(product.id))
                loadProductsCartData()
            }
        }
    }

    fun updateTotalPrice(shoppingCart: List<ShoppingCart>) {
        var total = 0.00
        for (item in shoppingCart) {
            if (item.product != null) {
                total += item.product.price * item.qty
            }
        }
        _totalPrice.value = total
    }


//    // Remove Item from Shopping Cart
//    fun removeItemFromCart(item: ProductList) {
//        _shoppingCartItems.remove(item)
//    }

    //minus item
    fun minusQtyItem(product: Product) {
        _shoppingCartItems.let {
            for (items in it.value?.data!!) {
                if (items.product_id == product.id) {
                    if (items.qty > 1) {
                        items.qty--
                        _totalPrice.value = _totalPrice.value?.minus(product.price)
                    }
                }
            }
        }
    }


    fun loadProductsCartData() {
        RetrofitInstance.get().api.loadShoppingCartUnPaid().enqueue(object : Callback<Res<ShoppingCart>> {
            override fun onResponse(
                call: Call<Res<ShoppingCart>>,
                response: Response<Res<ShoppingCart>>
            ) {
                val responseData = response.body()
                if (responseData != null) {
                    val apiData = ApIData(response.code(), responseData.data)
                    _shoppingCartItems.postValue(apiData)
                } else {
                    println("Response data is null")
                }
            }

            override fun onFailure(call: Call<Res<ShoppingCart>>, t: Throwable) {
                println("Failure: ${t.message}")
            }
        })
    }

    fun qtyOperation(id: Int, qty: Int) {
        RetrofitInstance.get().api.qtyOperation(id, BodyPutData(qty))
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

    private fun addProductToCart(data: AddProductToShoppingCart) {
        RetrofitInstance.get().api.addProductToShoppingCart(data)
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