package kh.edu.rupp.ite.furniturestore.model.api.model

import com.google.gson.annotations.SerializedName

//key word data mean open for another class can inherited
data class Product(
  var id: Int,
  val category_id: Int,
  val name: String,
  val price: Double,
  val imageUrl: String,
  val description: String,
  val created_at: String?,
  val updated_at: String?,
  var qty: Int = 1,
  var isFavorite: Int,
  @SerializedName("image_urls") //rename image_urls to ImageUrls
  val imageUrls: List<ImageUrls>?
)

//model of ProductDetail
data class ProductDetail(
  val message: String,
  val data: Product
)

//model of ImageUrls
data class ImageUrls(
  val id: Int,
  val product_id: Int,
  val imageUrl: String,
  val created_at: String?,
  val updated_at: String?,
)