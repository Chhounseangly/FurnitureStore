package kh.edu.rupp.ite.furniturestore.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.api.model.Product
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductBinding


class ProductAdapter: ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductAdapter()) {
    private class ProductAdapter : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id
    }
    class ProductViewHolder(
        private val viewHolderProductBinding: ViewHolderProductBinding
    ) : RecyclerView.ViewHolder(viewHolderProductBinding.root) {

        fun bind(product: Product) {

            //add image url to ImageView by Library Picasso
            Picasso.get().load(product.imageUrl).into(viewHolderProductBinding.imageView)
            viewHolderProductBinding.nameTxt.text= product.name
            viewHolderProductBinding.descriptionTxt.text= product.description

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderProductBinding.inflate(layoutInflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val products = getItem(position)
        holder.bind(products)
    }
}







