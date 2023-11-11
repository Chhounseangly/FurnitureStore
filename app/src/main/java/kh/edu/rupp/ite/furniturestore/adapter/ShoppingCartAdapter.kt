package kh.edu.rupp.ite.furniturestore.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductCartBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.CheckID
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class ShoppingCartAdapter(private var shoppingCartViewModel: ShoppingCartViewModel) : ListAdapter<ShoppingCart, ShoppingCartAdapter.ProductCartViewHolder>(
    ProductAddToCartAdapter()
) {
    //constructor
    private class ProductAddToCartAdapter : DiffUtil.ItemCallback<ShoppingCart>() {
        override fun areItemsTheSame(oldItem: ShoppingCart, newItem: ShoppingCart): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ShoppingCart, newItem: ShoppingCart): Boolean =
            oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCartViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderProductCartBinding.inflate(layoutInflater, parent, false)
        return ProductCartViewHolder(binding, shoppingCartViewModel)
    }

    override fun onBindViewHolder(holder: ProductCartViewHolder, position: Int) {
        val products = getItem(position)
        holder.bind(products)
    }

    class ProductCartViewHolder(
        private val viewHolderProductCartBinding: ViewHolderProductCartBinding,
        private val shoppingCartViewModel: ShoppingCartViewModel
    ) : RecyclerView.ViewHolder(viewHolderProductCartBinding.root) {
        fun bind(item: ShoppingCart) {
            //add image url to ImageView by Library Picasso
            Picasso.get().load(item.product?.imageUrl).into(viewHolderProductCartBinding.productImg)
//            //passing data list to view
            viewHolderProductCartBinding.nameProduct.text = item.product?.name
            viewHolderProductCartBinding.price.text = item.product?.price.toString()
            viewHolderProductCartBinding.displayQty.text = item.qty.toString()
//
//            //add more quantity of items
            viewHolderProductCartBinding.addBtn.setOnClickListener {
                if (item.product != null){
                    shoppingCartViewModel.addItemToCart(Product(item.product_id, item.product.category_id, item.product.name, item.product.price, item.product.imageUrl, item.product.description, item.created_at, item.updated_at, imageUrls=null))
                }
//                viewHolderProductCartBinding.displayQty.text = product.qty.toString()
            }
//            //add minus quantity of items
//            viewHolderProductCartBinding.minusBtn.setOnClickListener {
//                shoppingCartViewModel.minusQtyItem(product)
//                viewHolderProductCartBinding.displayQty.text = product.qty.toString()
//            }
        }
    }


}