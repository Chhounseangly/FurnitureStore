package kh.edu.rupp.ite.furniturestore.model.api.model


data class User(
    var count:Any?,
    var name: Any?,
    var gender:Any?,
    var probability: Any?
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String,
    val imageUrl: String,
    val created_at: Any?,
    val updated_at: Any?
)