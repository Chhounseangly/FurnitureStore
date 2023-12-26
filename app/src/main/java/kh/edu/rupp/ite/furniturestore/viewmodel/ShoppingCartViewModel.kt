package kh.edu.rupp.ite.furniturestore.viewmodel

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.BodyPutData
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance

class ShoppingCartViewModel : BaseViewModel() {

    // Create list for Shopping Cart items
    private val _shoppingCartItems = MutableLiveData<ApiData<Res<List<ShoppingCart>>>>()
    private val _tempDataList = mutableListOf<ShoppingCart>()
    private val _itemCount = MutableLiveData<Int>()
    private val _totalPrice = MutableLiveData(0.00)

    // LiveData to hold Shopping Cart Items.
    val shoppingCartItems: LiveData<ApiData<Res<List<ShoppingCart>>>> get() = _shoppingCartItems
    val tempDataList: LiveData<List<ShoppingCart>> get() = MutableLiveData(_tempDataList)

    val itemCount: LiveData<Int> get() = _itemCount
    val totalPrice: LiveData<Double> get() = _totalPrice

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
    fun addProductToShoppingCart(productId: Int): String {
        return if (_shoppingCartItems.value?.data?.data?.isNotEmpty() == true) {
            val existed = shoppingCartItems.value?.data?.data?.find { it.product_id == productId }
            if (existed != null) {
                "Product existed on shopping cart"
            } else {
                addProductToCartApi(productId)
                "Added to shopping cart"
            }
        } else {
            addProductToCartApi(productId)
            "Added to shopping cart"
        }
    }

    // Add product to the shopping cart via API
    private fun addProductToCartApi(productId: Int) {
        performApiCall(
            request = {
                RetrofitInstance.get().api.addProductToShoppingCart(
                    AddProductToShoppingCart(
                        productId
                    )
                )
            },
            reloadData = { loadProductsCartData() }
        )
    }

    // Retrieve products in the shopping cart
    fun loadProductsCartData() {
        performApiCall(
            response = _shoppingCartItems,
            request = { RetrofitInstance.get().api.loadShoppingCartUnPaid() },
        )
    }

    // Perform quantity operation (increase/decrease) on a shopping cart item
    fun qtyOperation(item: ShoppingCart, operation: String) {
        val existingItem = shoppingCartItems.value?.data?.data?.find { it.id == item.id }
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
        for (item in shoppingCartItems.value?.data?.data!!) {
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
        performApiCall(
            request = { RetrofitInstance.get().api.qtyOperation(data) },
            reloadData = { loadProductsCartData() }
        )
    }

    // Function to handle API call for deleting a product from the shopping cart
    fun deleteProductShoppingCart(productId: Int) {
        performApiCall(
            request = { RetrofitInstance.get().api.deleteProductShoppingCart(productId) },
            reloadData = { loadProductsCartData() }
        )
    }
}
