package kh.edu.rupp.ite.furniturestore.controller.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductDetail
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderSliderImageBinding
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.model.api.model.Image


class ProductDetailAdapter : ListAdapter<ProductDetail, ProductDetailAdapter.ProductDetailViewHolder>(
    ProductDetailAdapter()
) {
    private class ProductDetailAdapter : DiffUtil.ItemCallback<ProductDetail>() {
        override fun areItemsTheSame(oldItem: ProductDetail, newItem: ProductDetail): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ProductDetail, newItem: ProductDetail): Boolean =
            oldItem.id == newItem.id
    }

    class ProductDetailViewHolder(
        private val viewHolderSliderImageBinding: ViewHolderSliderImageBinding
    ) : RecyclerView.ViewHolder(viewHolderSliderImageBinding.root) {
        fun bind(productDetail: ProductDetail) {
            //add image url to ImageView by Library Picasso
            Picasso.get().load(productDetail.imageUrl.first().image).into(viewHolderSliderImageBinding.imageView)
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





