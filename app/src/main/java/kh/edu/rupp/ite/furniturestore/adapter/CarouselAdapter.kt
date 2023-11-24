package kh.edu.rupp.ite.furniturestore.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderCarouselBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ImageUrls
import kh.edu.rupp.ite.furniturestore.view.activity.PreviewImageActivity
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity


class CarouselAdapter : ListAdapter<ImageUrls, CarouselAdapter.CarouselViewHolder>(
    CarouselAdapter()
) {
    private class CarouselAdapter : DiffUtil.ItemCallback<ImageUrls>() {
        override fun areItemsTheSame(oldItem: ImageUrls, newItem: ImageUrls): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ImageUrls, newItem: ImageUrls): Boolean =
            oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderCarouselBinding.inflate(layoutInflater, parent, false)

        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind(image)


        //add listener to preview
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, PreviewImageActivity::class.java)
            intent.putExtra("imageUrl", image.imageUrl)
            it.context.startActivity(intent)
        }
    }

    class CarouselViewHolder(
        private val viewHolderCarouselBinding: ViewHolderCarouselBinding
    ) : RecyclerView.ViewHolder(viewHolderCarouselBinding.root) {
        fun bind(image: ImageUrls) {
        Log.d("image", image.imageUrl)
            Picasso.get().load(image.imageUrl).into(viewHolderCarouselBinding.carouselImageView)

        }

    }
}