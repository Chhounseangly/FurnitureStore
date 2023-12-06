package kh.edu.rupp.ite.furniturestore.model.api.model

class CategoryTypes (
    var id: Int,
    var name: String
)

class  CategoryModel(
    val message: String,
    val data: List<CategoryTypes>
)

data class ApiResponse(
    val message: String,
    val data: ProductByCate
)
data class ProductByCate(
    val id: Int,
    val name: String,
    val created_at: String?,
    val updated_at: String?,
    val products: List<Product>
)