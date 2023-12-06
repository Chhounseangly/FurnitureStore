package kh.edu.rupp.ite.furniturestore.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderSearchFoundBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity

class SearchFoundAdapter : ListAdapter<Product, SearchFoundAdapter.ProductSearchFoundViewHolder>(
    object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSearchFoundViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderSearchFoundBinding.inflate(layoutInflater, parent, false)

        return ProductSearchFoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductSearchFoundViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ProductDetailActivity::class.java)
            intent.putExtra("id", product.id)
            it.context.startActivity(intent)
        }
    }

    class ProductSearchFoundViewHolder(
        private val viewHolderSearchFoundBinding: ViewHolderSearchFoundBinding
    ) : RecyclerView.ViewHolder(viewHolderSearchFoundBinding.root) {
        fun bind(item: Product) {
            Picasso.get().load(item.imageUrl).into(viewHolderSearchFoundBinding.img)
            viewHolderSearchFoundBinding.name.text = item.name
            viewHolderSearchFoundBinding.price.text = item.price.toString()
        }
    }
}