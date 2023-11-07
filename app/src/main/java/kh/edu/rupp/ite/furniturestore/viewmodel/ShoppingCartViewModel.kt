package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ApIData
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList
import kh.edu.rupp.ite.furniturestore.model.api.model.Status

class ShoppingCartViewModel : ViewModel() {

    // Create list for Shopping Cart items
    private val _shoppingCartItems = mutableListOf<ProductList>()

    // LiveData to hold Shopping Cart Items. Exposing LiveData to the outside world
    val shoppingCartItems: LiveData<List<ProductList>>
        get() = MutableLiveData(_shoppingCartItems)


    private val _totalPrice = MutableLiveData(0)
    val totalPrice: LiveData<Int> get() = _totalPrice

    // Add Item to Shopping Cart
    fun addItemToCart(product: ProductList) {
        var itemAdded = false
        for (item in _shoppingCartItems) {
            if (item.id == product.id) {
                item.qty++
                itemAdded = true
                break
            }
        }
        if (!itemAdded) {
            _shoppingCartItems.add(product)
        }
        updateTotalPrice()
    }
    fun updateTotalPrice() {
        var total = 0
        for (item in _shoppingCartItems) {
            total += item.price * item.qty
        }
        _totalPrice.value = total
    }

//    // Remove Item from Shopping Cart
//    fun removeItemFromCart(item: ProductList) {
//        _shoppingCartItems.remove(item)
//    }

    //minus item
    fun minusQtyItem(item: ProductList) {
        for (product in _shoppingCartItems) {
            if (item.id == product.id) {
                if (product.qty > 1) {
                    product.qty--
                }
                break
            }
        }
        updateTotalPrice()
    }


    fun loadProductsCartData() {
        var apiData = listOf(
            ProductList(
                1,
                "Table",
                "https://i4.komnit.com/store/upload/images/express_2207/112290-ARJDYN/1657316942-ARJDYN.jpg",
                50,
                1
            ),
            ProductList(
                2,
                "Table",
                "https://i4.komnit.com/store/upload/images/express_2207/112290-ARJDYN/1657316942-ARJDYN.jpg",
                1000,
                1
            ),
        )
        _shoppingCartItems.addAll(apiData)
    }

}