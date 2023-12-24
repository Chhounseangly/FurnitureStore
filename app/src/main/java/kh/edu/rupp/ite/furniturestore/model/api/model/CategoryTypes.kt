package kh.edu.rupp.ite.furniturestore.model.api.model

class CategoryTypes (
    var id: Int,
    var name: String
)

data class ProductByCate(
    val id: Int,
    val name: String,
    val created_at: String?,
    val updated_at: String?,
    val products: List<Product>
)