package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kh.edu.rupp.ite.furniturestore.BuildConfig
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.BodyPutData
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.service.ApiService
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            for (items in it.value?.data!!) {
                if (items.product_id == product.id) {
                    Log.d("product", "true")
                } else addProductToCart(AddProductToShoppingCart(product.id))

            }
        }
//            for (item in _shoppingCartItems) {
//                if (item.id == product.id) {
//                    item.qty++
//                    itemAdded = true
//                    break
//                }
//            }
//        if (!itemAdded) {
//            _shoppingCartItems.add(product)
//        }
//            updateTotalPrice()
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
    fun minusQtyItem(item: Product) {
//        for (product in _shoppingCartItems) {
//            if (item.id == product.id) {
//                if (product.qty > 1) {
//                    product.qty--
//                }
//                break
//            }
//        }
//        updateTotalPrice()
    }


    fun loadProductsCartData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)

        api.loadShoppingCartUnPaid().enqueue(object : Callback<Res<ShoppingCart>> {
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
        RetrofitInstance.api.qtyOperation(id, BodyPutData(qty))
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
        RetrofitInstance.api.addProductToShoppingCart(
            AddProductToShoppingCart(data.product_id))
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