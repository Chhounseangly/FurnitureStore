package kh.edu.rupp.ite.furniturestore.model.api.model


data class ResponseMessage(
    val message: String,
    val data: String?
)

data class BodyPutData(
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