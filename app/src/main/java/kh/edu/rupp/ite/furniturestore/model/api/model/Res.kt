package kh.edu.rupp.ite.furniturestore.model.api.model


data class Res<T>(
    val success: Boolean,
    val message: String,
    val data: List<T>
)