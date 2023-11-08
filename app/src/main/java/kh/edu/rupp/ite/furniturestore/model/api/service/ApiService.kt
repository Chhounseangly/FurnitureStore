package kh.edu.rupp.ite.furniturestore.model.api.service

import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/products")
    fun loadProductList(): Call<List<ProductList>>

    @GET("?name=luc")
    fun getUser(): Call<User>
}