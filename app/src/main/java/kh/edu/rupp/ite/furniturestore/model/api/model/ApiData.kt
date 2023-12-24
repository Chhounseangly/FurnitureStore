package kh.edu.rupp.ite.furniturestore.model.api.model

data class ApiData<T>(
    val status: Status,
    val data: T?,
    val meta: Meta? = null
)

data class Meta(
    val current_page: Int,
    val per_page: Int,
    val total: Int
)

enum class Status{
    Processing, Success, Failed, LoadingMore, NeedVerify
}
