package kh.edu.rupp.ite.furniturestore.model.api.model


class CategoryTypes (
    var id: Int,
    var name: String
)

class  CategoryModel(
    val message: String,
    val data: List<CategoryTypes>
)

data class Data(
    val id: Int,
    val name: String,
    val created_at: String?,
    val updated_at: String?,
    val products: List<Product>
)

data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: Data
)
