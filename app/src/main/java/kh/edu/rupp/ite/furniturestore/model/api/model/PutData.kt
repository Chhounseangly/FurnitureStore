package kh.edu.rupp.ite.furniturestore.model.api.model


data class ResponseMessage(
    val message: String,
    val data: String?
)

data class BodyPutData(
    val id: Int,
    val qty: Int
)


data class PaymentModel(
    var product_id: Int,
    var shopping_cart_id : Int
)

class Data(
    val id: Int,
    val qty: Int
)


data class ResAuth(
    val message: String,
    val data: Token?
)

class Token(
    val token: String,
    val token_type: String,
    val expires_at: Any
)

data class UpdateProfile(
    val name: String?,
    val avatar: String?
)