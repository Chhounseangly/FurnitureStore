package kh.edu.rupp.ite.furniturestore.api.service

import kh.edu.rupp.ite.furniturestore.api.model.Product
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/query_product")
    fun loadProductList(): Call<List<Product?>?>?
}