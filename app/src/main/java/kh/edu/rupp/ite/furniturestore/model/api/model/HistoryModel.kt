package kh.edu.rupp.ite.furniturestore.model.api.model

import com.google.gson.annotations.SerializedName

data class HistoryModel(
    val id: Int,
    val product_id: Int,
    val user_id: Int,
    val qty: Int,
    @SerializedName("formatted_created_at")
    val created_at: String?,
    @SerializedName("formatted_updated_at")
    val updated_at: String?,
    val product: ProductModel
)