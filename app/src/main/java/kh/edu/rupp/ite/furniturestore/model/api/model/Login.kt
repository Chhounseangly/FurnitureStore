package kh.edu.rupp.ite.furniturestore.model.api.model

data class Login (
    val email: String,
    val password: String
)

data class Password (
    val current_password: String,
    val new_password: String,
    val new_password_confirmation: String
)