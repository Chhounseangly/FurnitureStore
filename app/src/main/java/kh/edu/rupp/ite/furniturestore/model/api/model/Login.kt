package kh.edu.rupp.ite.furniturestore.model.api.model

data class Login (
    val email: String,
    val password: String
)


data class SignUp(
    val email: String,
    val password: String,
    val username: String
)

