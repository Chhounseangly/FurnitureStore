package kh.edu.rupp.ite.furniturestore.model.api.model

data class ShoppingCart(
    var id: Int,
    val product_id: Int,
    val paid: Int,
    var qty: Int,
    val created_at: String?,
    val updated_at: String?,
    val product: ProductModel
)

data class ProductModel(
    val id: Int,
    val category_id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val description: String,
    val created_at: String?,
    val updated_at: String?,
)


data class AddProductToShoppingCart(
    val product_id: Int,
)
