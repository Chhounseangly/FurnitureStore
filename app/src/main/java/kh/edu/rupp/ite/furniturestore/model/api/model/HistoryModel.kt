package kh.edu.rupp.ite.furniturestore.model.api.model

data class HistoryModel(
    val id: Int,
    val product_id: Int,
    val user_id: Int,
    val qty: Int,
    val created_at: String?,
    val updated_at: String?,
    val product: ProductModel
)