package kh.edu.rupp.ite.furniturestore.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductFavoriteBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity

class FavoriteAdapter
    : ListAdapter<Product, FavoriteAdapter.ProductListViewHolder>(
    diffCallback
) {

    // Companion object for the DiffUtil.ItemCallback
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.id == newItem.id
        }
    }

    // Create and bind the view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        // Use View Binding to inflate the layout
        val binding = ViewHolderProductFavoriteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductListViewHolder(binding)
    }

    // Bind the data to the view holder and handle click events
    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ProductDetailActivity::class.java)
            intent.putExtra("id", product.id)
            it.context.startActivity(intent)
        }
    }

    // View Holder class for holding and binding views
    class ProductListViewHolder(
        private val binding: ViewHolderProductFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        // Bind data to the views
        fun bind(product: Product) {
            // Use Picasso to load and display the product image
            with(binding.img) {
                Picasso.get().load(product.imageUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_error)
                    .into(this)
            }
            // Set the product name and price
            binding.textName.text = product.name
            binding.textPrice.text = product.price.toString()
        }
    }
}