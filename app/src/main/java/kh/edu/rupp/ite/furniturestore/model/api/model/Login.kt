package kh.edu.rupp.ite.furniturestore.model.api.model

data class Login (
    val email: String,
    val password: String
)


data class Register(
    val name: String,
    val email: String,
    val password: String
)

