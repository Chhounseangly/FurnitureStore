package kh.edu.rupp.ite.furniturestore.model.api.model


data class ApIData<T>(
    val status: Status,
    val data: T?
)
enum class Status {
    LOADING, SUCCESS, FAIL
}
