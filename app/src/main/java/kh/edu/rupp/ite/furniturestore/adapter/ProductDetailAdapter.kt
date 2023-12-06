package kh.edu.rupp.ite.furniturestore.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderSliderImageBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product

class ProductDetailAdapter : ListAdapter<Product, ProductDetailAdapter.ProductDetailViewHolder>(
    // Use a direct instantiation of DiffUtil.ItemCallback in the constructor
    object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id
    }
) {

    class ProductDetailViewHolder(
        private val viewHolderSliderImageBinding: ViewHolderSliderImageBinding
    ) : RecyclerView.ViewHolder(viewHolderSliderImageBinding.root) {
        fun bind(product: Product) {
            //add image url to ImageView by Library Picasso
//            Picasso.get().load(product.imageUrl.first().image).into(viewHolderSliderImageBinding.imageView)
//            viewHolderProductItemBinding.title.text = productList.name
//            viewHolderProductItemBinding.price.text = "$ " +productList.price.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderSliderImageBinding.inflate(layoutInflater, parent, false)
        return ProductDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) {
        val products = getItem(position)
        holder.bind(products)
    }
}