package kh.edu.rupp.ite.furniturestore.model.api.model

data class ApIData<T>(
    val status: Status,
    val data: T?
)
enum class Status{
    Processing, Success, Failed, LoadingMore
}

data class AuthApiData<T>(
    val status: StatusAuth,
    val data: T?
)

enum class StatusAuth{
    Processing, Success, Failed, NeedVerify
}
