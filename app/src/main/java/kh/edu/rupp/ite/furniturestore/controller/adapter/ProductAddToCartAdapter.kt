package kh.edu.rupp.ite.furniturestore.controller.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.controller.fragments.CartFragment
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCartBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductCartBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList

class ProductAddToCartAdapter: ListAdapter<ProductList, ProductAddToCartAdapter.ProductCartViewHolder>(ProductAddToCartAdapter()) {
    //constructor
    private class ProductAddToCartAdapter : DiffUtil.ItemCallback<ProductList>() {
        override fun areItemsTheSame(oldItem: ProductList, newItem: ProductList): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ProductList, newItem: ProductList): Boolean =
            oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCartViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderProductCartBinding.inflate(layoutInflater, parent, false)
        return ProductCartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductCartViewHolder, position: Int) {
        val products = getItem(position)
        holder.bind(products)
    }

    class ProductCartViewHolder(
        private val viewHolderProductCartBinding: ViewHolderProductCartBinding
    ) : RecyclerView.ViewHolder(viewHolderProductCartBinding.root) {
        fun bind(productList: ProductList) {
            //add image url to ImageView by Library Picasso
            Picasso.get().load(productList.imageUrl).into(viewHolderProductCartBinding.productImg)
            viewHolderProductCartBinding.nameProduct.text = productList.name
            viewHolderProductCartBinding.price.text = "$ " +productList.price
        }
    }


}