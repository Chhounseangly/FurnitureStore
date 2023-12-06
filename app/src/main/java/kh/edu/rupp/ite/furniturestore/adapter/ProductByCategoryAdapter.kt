package kh.edu.rupp.ite.furniturestore.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductItemBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity

class ProductByCategoryAdapter : ListAdapter<Product, ProductByCategoryAdapter.ProductListViewHolder>(
    // Use a direct instantiation of DiffUtil.ItemCallback in the constructor
    object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderProductItemBinding.inflate(layoutInflater, parent, false)
        return ProductListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val products = getItem(position)

        holder.bind(products)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ProductDetailActivity::class.java)
            intent.putExtra("id", products.id)
            it.context.startActivity(intent)
        }
    }

    class ProductListViewHolder(
        private val viewHolderProductItemBinding: ViewHolderProductItemBinding
    ) : RecyclerView.ViewHolder(viewHolderProductItemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            with(viewHolderProductItemBinding) {
                Picasso.get().load(product.imageUrl).into(img)
                name.text = product.name
                price.text = "$ ${product.price}"
            }
        }
    }
}