package kh.edu.rupp.ite.furniturestore.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductSlider
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance

class ProductSliderViewModel: BaseViewModel() {

    private val _productSliderData = MutableLiveData<ApiData<Res<List<ProductSlider>>>>()
    val productSliderData: LiveData<ApiData<Res<List<ProductSlider>>>> get() = _productSliderData


    fun loadProductSliderData(){
        performApiCall(_productSliderData, { RetrofitInstance.get().api.imageSlider() })
    }
}