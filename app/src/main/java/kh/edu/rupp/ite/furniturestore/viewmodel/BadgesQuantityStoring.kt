package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BadgesQuantityStoring: ViewModel() {


    private val _qtyShoppingCart = MutableLiveData<Int>()
    private val _qtyFav = MutableLiveData<Int>()

    val qtyShoppingCart: LiveData<Int> get() = _qtyShoppingCart

    fun setQtyShoppingCart(qty: Int) {
        _qtyShoppingCart.value = _qtyShoppingCart.value?.plus(qty) ?: qty // Handle null cases
    }

    fun clearQtyShoppingCart(){
        _qtyShoppingCart.value = 0
    }

    fun clearQtyFav(){
        _qtyFav.value = 0
    }
}