package kh.edu.rupp.ite.furniturestore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductCartBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class ShoppingCartAdapter(private var shoppingCartViewModel: ShoppingCartViewModel) :
    ListAdapter<ShoppingCart, ShoppingCartAdapter.ProductCartViewHolder>(ProductAddToCartAdapter()) {

    // DiffUtil callback for efficient RecyclerView updates
    private class ProductAddToCartAdapter : DiffUtil.ItemCallback<ShoppingCart>() {
        override fun areItemsTheSame(oldItem: ShoppingCart, newItem: ShoppingCart): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ShoppingCart, newItem: ShoppingCart): Boolean =
            oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCartViewHolder {
        // Inflate the layout for each item
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderProductCartBinding.inflate(layoutInflater, parent, false)
        return ProductCartViewHolder(binding, shoppingCartViewModel)
    }

    override fun onBindViewHolder(holder: ProductCartViewHolder, position: Int) {
        val products = getItem(position)
        holder.bind(products)
    }

    // ViewHolder class for holding references to views
    class ProductCartViewHolder(
        private val viewHolderProductCartBinding: ViewHolderProductCartBinding,
        private val shoppingCartViewModel: ShoppingCartViewModel
    ) : RecyclerView.ViewHolder(viewHolderProductCartBinding.root) {

        // Bind data to the views
        fun bind(item: ShoppingCart) {
            with(viewHolderProductCartBinding) {
                // Use safe call to handle possible null values
                item.product?.let { product ->
                    // Load image using Picasso library
                    Picasso.get().load(product.imageUrl).into(productImg)

                    // Bind data to the view
                    nameProduct.text = product.name
                    price.text = product.price.toString()
                    displayQty.text = item.qty.toString()

                    // Add click listeners for add and minus buttons
                    addBtn.setOnClickListener {
                        if (item.product != null){
                            shoppingCartViewModel.addItemToCart(Product(
                                item.product_id,
                                item.product.category_id,
                                item.product.name,
                                item.product.price,
                                item.product.imageUrl,
                                item.product.description,
                                item.created_at,
                                item.updated_at,
                                imageUrls = null,
                                isFavorite = 0
                            ))
                            viewHolderProductCartBinding.displayQty.text = item.qty.toString()
                        }
                    }
                    minusBtn.setOnClickListener {
                        if (item.product != null) {
                            shoppingCartViewModel.minusQtyItem(
                                Product(
                                    item.product_id,
                                    item.product.category_id,
                                    item.product.name,
                                    item.product.price,
                                    item.product.imageUrl,
                                    item.product.description,
                                    item.created_at,
                                    item.updated_at,
                                    imageUrls = null,
                                    isFavorite = 0
                                )
                            )
                            viewHolderProductCartBinding.displayQty.text = item.qty.toString()
                        }
                    }
                }
            }
        }
    }
}