package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductSlider
import kh.edu.rupp.ite.furniturestore.model.api.model.Status

class ProductSliderViewModel: ViewModel() {

    private val _productSliderData = MutableLiveData<ApiData<List<ProductSlider>>>()
    val productSliderData: LiveData<ApiData<List<ProductSlider>>>
        get() = _productSliderData


    fun loadProductSliderData(){

        val apiData = ApiData(Status.Success, listOf(
            ProductSlider(
                1,
                "test  ",
                "test",
                "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png"
            ),
            ProductSlider(
                1,
                "test  ",
                "test",
                "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png"
            ),
            ProductSlider(
                1,
                "test  ",
                "test",
                "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png"
            ),
        ))
        _productSliderData.postValue(apiData)
    }
}