package kh.edu.rupp.ite.furniturestore.model.api.model

data class ApiData<T>(
    val status: Status,
    val data: T?
)

data class ApiDataList<T>(
    val status: Status,
    val data: List<T>?,
    val meta: Meta?
)

data class Meta(
    val current_page: Int,
    val per_page: Int,
    val total: Int
)

enum class Status{
    Processing, Success, Failed, LoadingMore, NeedVerify
}
