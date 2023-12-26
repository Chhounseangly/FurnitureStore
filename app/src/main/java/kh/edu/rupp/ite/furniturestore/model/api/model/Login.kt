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

data class Email (
    val email: String
)

data class ResetPassword (
    val email: String,
    val token: String,
    val password: String,
    val password_confirmation: String
)