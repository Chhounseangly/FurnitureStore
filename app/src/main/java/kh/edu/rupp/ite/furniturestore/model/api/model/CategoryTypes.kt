package kh.edu.rupp.ite.furniturestore.model.api.model


class CategoryTypes (
    var id: Int,
    var name: String
)

class  CategoryModel(
    val success: Boolean,
    val message: String,
    val data: List<CategoryTypes>
)
data class Product(
    val id: Int,
    val category_id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val created_at: String?,
    val updated_at: String?
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
