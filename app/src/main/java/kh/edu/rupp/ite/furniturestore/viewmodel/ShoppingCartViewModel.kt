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

    // Declare a MutableLiveData to hold the shopping cart items.
    // ApIData is used to wrap the data along with a status code for better handling.
    // It is a LiveData because it needs to be observed for changes in UI.
    private val _shoppingCartItems = MutableLiveData<ApIData<List<ShoppingCart>>>()

    // Declare a MutableLiveData to hold the total price of items in the shopping cart.
    // It is initialized with 0.00.
    private val _totalPrice = MutableLiveData(0.00)

    // MutableLiveData for displaying messages
    private val _messageLiveData = MutableLiveData<String>()

    // Expose the total price as LiveData to allow observation from the UI.
    val totalPrice: LiveData<Double> get() = _totalPrice

    // Expose the shopping cart items as LiveData to allow observation from the UI.
    val shoppingCartItems: LiveData<ApIData<List<ShoppingCart>>> get() = _shoppingCartItems

    // Expose the message as LiveData to allow observation from the UI.
    val messageLiveData get() = _messageLiveData

    // Function to set a message
    fun setMessage(message: String) {
        _messageLiveData.value = message
    }

    // Add Item to Shopping Cart
    fun addItemToCart(product: Product) {
        // Get current cart items
        val cartItems = _shoppingCartItems.value?.data.orEmpty()

        // Check if the product is already in the cart
        val existingItem = cartItems.find { it.product_id == product.id }

        if (existingItem != null) {
            // Increment quantity and update total price
            existingItem.qty++
            _totalPrice.value = _totalPrice.value?.plus(product.price)
        } else {
            // Add the product to the cart and refresh the cart data
            addProductToCart(AddProductToShoppingCart(product.id))
            loadProductsCartData()
        }
    }

    // Decrease quantity of an item in the cart
    fun minusQtyItem(product: Product) {
        val cartItems = _shoppingCartItems.value?.data.orEmpty()

        for (items in cartItems) {
            if (items.product_id == product.id && items.qty > 1) {
                items.qty--
                _totalPrice.value = _totalPrice.value?.minus(product.price)
                break  // Break once the item is found and quantity is decreased
            }
        }
    }

    // Update total price based on shopping cart items
    fun updateTotalPrice(shoppingCart: List<ShoppingCart>) {
        var total = 0.00
        for (item in shoppingCart) {
            if (item.product != null) {
                total += item.product.price * item.qty
            }
        }
        _totalPrice.value = total
    }

    // Remove Item from Shopping Cart
//    fun removeItemFromCart(item: ProductList) {
//        _shoppingCartItems.remove(item)
//    }

    // Load shopping cart data from the API
    fun loadProductsCartData() {
        RetrofitInstance.get().api.loadShoppingCartUnPaid().enqueue(object : Callback<Res<ShoppingCart>> {
            override fun onResponse(
                call: Call<Res<ShoppingCart>>,
                response: Response<Res<ShoppingCart>>
            ) {
                val responseData = response.body()
                if (responseData != null) {
                    // Wrap the data in ApIData and update LiveData
                    val apiData = ApIData(response.code(), responseData.data)
                    _shoppingCartItems.postValue(apiData)
                } else {
                    // Log a warning if the response data is null
                    Log.w("ShoppingCartViewModel", "Response data is null")
                }
            }

            override fun onFailure(call: Call<Res<ShoppingCart>>, t: Throwable) {
                // Log an error if the API call fails
                Log.e("ShoppingCartViewModel", "Failure: ${t.message}")
            }
        })
    }

    // Perform quantity operation (e.g., update quantity) on an item
    fun qtyOperation(id: Int, qty: Int) {
        RetrofitInstance.get().api.qtyOperation(id, BodyPutData(qty))
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    val responseData = response.body()
                    if (responseData != null) {
                        // Log the result message
                        Log.d("ShoppingCartViewModel", responseData.message)
                        setMessage("Added product to shopping cart")
                    } else {
                        // Log a warning if the response data is null
                        Log.w("ShoppingCartViewModel", "Response data is null")
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    // Log an error if the API call fails
                    Log.e("ShoppingCartViewModel", "Failure: ${t.message}")
                }
            })
    }

    // Add a product to the shopping cart
    private fun addProductToCart(data: AddProductToShoppingCart) {
        RetrofitInstance.get().api.addProductToShoppingCart(data)
            .enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    val responseData = response.body()
                    if (responseData != null) {
                        // Log the result message
                        Log.d("ShoppingCartViewModel", responseData.message)
                    } else {
                        // Log a warning if the response data is null
                        Log.w("ShoppingCartViewModel", "Response data is null")
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    // Log an error if the API call fails
                    Log.e("ShoppingCartViewModel", "Failure: ${t.message}")
                }
            })
    }
}
