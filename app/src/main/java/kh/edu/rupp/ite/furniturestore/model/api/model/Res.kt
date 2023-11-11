package kh.edu.rupp.ite.furniturestore.model.api.model


data class Res<T>(
    val message: String,
    val data: List<T>
)