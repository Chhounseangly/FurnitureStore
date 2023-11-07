package kh.edu.rupp.ite.furniturestore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductSlider
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductSliderBinding

class ProductSliderAdapter: ListAdapter<ProductSlider, ProductSliderAdapter.ProductSliderViewHolder>(
    ProductSliderAdapter()
) {

    private class ProductSliderAdapter : DiffUtil.ItemCallback<ProductSlider>() {
        override fun areItemsTheSame(oldItem: ProductSlider, newItem: ProductSlider): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ProductSlider, newItem: ProductSlider): Boolean =
            oldItem.id == newItem.id
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSliderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderProductSliderBinding.inflate(layoutInflater, parent, false)

        return ProductSliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductSliderViewHolder, position: Int) {
        val products = getItem(position)
        holder.bind(products)
    }

    class ProductSliderViewHolder(
        private val viewHolderProductSliderBinding: ViewHolderProductSliderBinding,

    ) : RecyclerView.ViewHolder(viewHolderProductSliderBinding.root) {
        fun bind(productSlider: ProductSlider) {
            val sliderModels = ArrayList<SlideModel>()
            sliderModels.add(SlideModel(productSlider.imageUrl, productSlider.name, ScaleTypes.FIT))

//            viewHolderProductSliderBinding.carousel.setImageList()
            //add image url to ImageView by Library Picasso
//            Picasso.get().load(productSlider.imageUrl).into(viewHolderProductSliderBinding.imageView)
//            viewHolderProductSliderBinding.nameTxt.text= productSlider.name
//            viewHolderProductSliderBinding.descriptionTxt.text= productSlider.description

        }
    }
}







