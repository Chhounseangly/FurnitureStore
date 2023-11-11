package kh.edu.rupp.ite.furniturestore.model.api.model


data class ResponseMessage(
    val message: String,
    val data: String?
)

data class BodyPutData(
    val qty: Int
)