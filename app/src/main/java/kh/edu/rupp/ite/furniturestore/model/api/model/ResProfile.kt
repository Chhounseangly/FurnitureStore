package kh.edu.rupp.ite.furniturestore.model.api.model


data class ResProfile(
    val message: String,
    val data: User
)

class User(
    val id: Int,
    val name: String,
    val email: String,
    val avatar: String,
    val email_verified_at: String,
    val created_at: Any?,
    val updated_at: Any?
)




