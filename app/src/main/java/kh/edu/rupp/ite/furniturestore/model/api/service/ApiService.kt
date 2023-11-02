package kh.edu.rupp.ite.furniturestore.model.api.service

import kh.edu.rupp.ite.furniturestore.model.api.model.ProductSlider
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/query_product")
    fun loadProductList(): Call<List<ProductSlider?>?>?

    @GET("?name=luc")
    fun getUser(): Call<User>
}