package kh.edu.rupp.ite.furniturestore.model.api.model


//this class open from another class can inherited
open class ProductList(
    var id: Int,
    var name: String,
    var imageUrl: String,
    var price: Int,
)