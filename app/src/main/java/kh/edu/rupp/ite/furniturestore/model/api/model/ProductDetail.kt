package kh.edu.rupp.ite.furniturestore.model.api.model


//this class inherited from ProductList
class ProductDetail(
    var id: Int,
    var name: String,
    var imageUrl: List<Image>,
    var price: Int,
    var description: String,
)

data class Image(
    val image: String
)