package kh.edu.rupp.ite.furniturestore.model.api.service

import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiResponse
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryModel
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductDetail
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.BodyPutData
import kh.edu.rupp.ite.furniturestore.model.api.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    //End Point fetching products
    @GET("api/products")
    suspend fun loadProductList(): Res<Product>

    //End Point fetching product Detail By passing id
    @GET("api/products/{id}")
    fun loadProductDetail(@Path("id") id: Int): Call<ProductDetail>

    //End Point fetching categories data
    @GET("api/categories")
    suspend fun loadCategories(): CategoryModel

    //End Point fetching product by category types
    @GET("api/categories_by_id/{id}")
    fun loadProductsByCategory(@Path("id") id: Int): Call<ApiResponse>

    //End Point fetching Product in Shopping Cart not yet Paid
    @GET("api/shoppingCartUnPaid")
    fun loadShoppingCartUnPaid(): Call<Res<ShoppingCart>>


    @POST("api/addProductToShoppingCart")
    fun addProductToShoppingCart(
        @Body product : AddProductToShoppingCart
    ): Call<ResponseMessage>

    //End Point put Quantity Product Operation
    @PUT("api/qtyOperation/{id}")
    fun qtyOperation(
        @Path("id") id: Int,
        @Body qty: BodyPutData
    ): Call<ResponseMessage>

    @GET("api/search_product_by_name")
    suspend fun searchProductByName(
        @Query("name") name: String
    ): Res<Product>


    @GET("?name=luc")
    fun getUser(): Call<User>
}