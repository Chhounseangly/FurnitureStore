package kh.edu.rupp.ite.furniturestore.model.api.service

import kh.edu.rupp.ite.furniturestore.model.api.model.AddProductToShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiResponse
import kh.edu.rupp.ite.furniturestore.model.api.model.BodyPutData
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryModel
import kh.edu.rupp.ite.furniturestore.model.api.model.Favorite
import kh.edu.rupp.ite.furniturestore.model.api.model.HistoryModel
import kh.edu.rupp.ite.furniturestore.model.api.model.Login
import kh.edu.rupp.ite.furniturestore.model.api.model.Password
import kh.edu.rupp.ite.furniturestore.model.api.model.PaymentModel
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductDetail
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.model.ResAuth
import kh.edu.rupp.ite.furniturestore.model.api.model.ResProfile
import kh.edu.rupp.ite.furniturestore.model.api.model.ResponseMessage
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.VerifyEmailRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    //End Point fetching products
    @GET("api/products?size=4")
    suspend fun loadProductList(
        @Query("page") page: Int = 1
    ): Res<Product>

    //End Point fetching product Detail By passing id
    @GET("api/products/{id}")
    suspend fun loadProductDetail(@Path("id") id: Int): ProductDetail

    //End Point fetching categories data
    @GET("api/categories")
    suspend fun loadCategories(): CategoryModel

    //End Point fetching product by category types
    @GET("api/categories_by_id/{id}")
    suspend fun loadProductsByCategory(@Path("id") id: Int): ApiResponse

    //End Point fetching Product in Shopping Cart not yet Paid
    @GET("api/shoppingCartUnPaid")
    suspend fun loadShoppingCartUnPaid(): Res<ShoppingCart>

    @POST("api/addProductToShoppingCart")
    suspend fun addProductToShoppingCart(@Body product_id: AddProductToShoppingCart): ResponseMessage

    //End Point delete Product from shopping cart
    @DELETE("api/deleteProductCart/{id}")
    suspend fun deleteProductShoppingCart(@Path("id") id: Int): ResponseMessage

    //End Point put Quantity Product Operation
    @PUT("api/qtyOperation")
    suspend fun qtyOperation(@Body data: List<BodyPutData>): ResponseMessage

    @GET("api/search_product_by_name")
    suspend fun searchProductByName(@Query("name") name: String): Res<Product>

    @POST("api/favorite")
    suspend fun toggleFavorite(@Body product_id: AddProductToShoppingCart): Favorite

    @POST("api/login")
    suspend fun login(@Body login: Login): Response<ResAuth>

    @Multipart
    @POST("api/register")
    suspend fun register(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("password_confirmation") passwordConfirmation: RequestBody?,
        @Part file: MultipartBody.Part?,
    ): Response<ResAuth>

    @GET("api/loadProfile")
    suspend fun loadProfile(): ResProfile

    @GET("api/logout")
    suspend fun logout(): ResponseMessage

    @Multipart
    @POST("api/updateProfile?_method=PUT")
    suspend fun updateProfile(
        @Part("name") name: RequestBody?,
        @Part file: MultipartBody.Part?,
    ): ResProfile

    @POST("api/password/change")
    suspend fun changePassword(@Body data: Password): ResponseMessage

    //history
    @POST("api/history")
    suspend fun postPayment(
        @Body data: List<PaymentModel>
    ): ResponseMessage

    //load history data
    @GET("api/history")
    suspend fun loadHistoryPurchase(): Res<HistoryModel>

    //End Point fetching products
    @GET("api/favorite")
    suspend fun loadFavorite(): Res<Product>


    @POST("api/email/verify/usingOTP")
    suspend fun verifyEmail(
        @Body data: VerifyEmailRequest
    ): Response<ResAuth>
}