package kh.edu.rupp.ite.furniturestore.model.api.model

data class VerifyEmailRequest (
    val email: String,
    val otp: String
)