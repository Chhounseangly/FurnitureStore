package kh.edu.rupp.ite.furniturestore.model.api.service

import kh.edu.rupp.ite.furniturestore.model.api.model.ApiResponse
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList
import kh.edu.rupp.ite.furniturestore.model.api.model.Res

import kh.edu.rupp.ite.furniturestore.model.api.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/products")
    fun loadProductList(): Call<Res<ProductList>>
    @GET("api/categories")
    fun loadCategories(): Call<CategoryModel>
    @GET("api/categories_by_id/{id}")
    fun loadProductsByCategory(@Path("id") id: Int): Call<ApiResponse>
    fun loadProductList(): Call<List<ProductList>>

    @GET("?name=luc")
    fun getUser(): Call<User>
}