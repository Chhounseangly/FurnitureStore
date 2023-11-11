package kh.edu.rupp.ite.furniturestore.model.api.model


data class ApIData<T>(
    val status: Int,
    val data: T?
)


data class CheckID(
    var id: Int
)
