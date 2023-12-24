package kh.edu.rupp.ite.furniturestore.model.api.model

data class Res<T>(
    val message: String,
    val data: T,
    val meta: Meta?
)

data class BodyPutData(
    val id: Int,
    val qty: Int
)

data class PaymentModel(
    var product_id: Int,
    var shopping_cart_id: Int,
)

class Token(
    val token: String,
    val token_type: String,
    val expires_at: Any
)