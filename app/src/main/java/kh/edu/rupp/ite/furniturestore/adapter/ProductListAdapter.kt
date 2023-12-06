package kh.edu.rupp.ite.furniturestore.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductItemBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.FavoriteViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class ProductListAdapter(
    private val shoppingCartViewModel: ShoppingCartViewModel,
    private val favoriteViewModel: FavoriteViewModel
) : ListAdapter<Product, ProductListAdapter.ProductListViewHolder>(
    object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id
    }
) {

    // onCreateViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderProductItemBinding.inflate(layoutInflater, parent, false)
        return ProductListViewHolder(binding, shoppingCartViewModel, favoriteViewModel)
    }

    // onBindViewHolder
    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        // Bind data to the ViewHolder
        val products = getItem(position)
        holder.bind(products)

        // Item click listener to open ProductDetailActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ProductDetailActivity::class.java)
            intent.putExtra("id", products.id)
            it.context.startActivity(intent)
        }
    }

    // ViewHolder class
    class ProductListViewHolder(
        private val binding: ViewHolderProductItemBinding,
        private val shoppingCartViewModel: ShoppingCartViewModel,
        private val favoriteViewModel: FavoriteViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        // Bind data to views
        fun bind(product: Product) {
            with(binding) {
                // Load image using Picasso
                Picasso.get().load(product.imageUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_error)
                    .into(img)

                // Set product name and price
                name.text = product.name
                val priceText = StringBuilder().append("$").append(product.price).toString()
                price.text = priceText


                // Set favorite button based on the isFavorite flag
                bntFav.setImageResource(if (product.isFavorite == 1) R.drawable.ic_favorited else R.drawable.ic_fav)

                // Add to cart button click listener
                addToCartBtn.setOnClickListener {
                    shoppingCartViewModel.addProductToShoppingCart(product.id)
                }

                // Favorite button click listener
                bntFav.setOnClickListener {
                    favoriteViewModel.toggleFavorite(product) {
                        // Set the favorite button image based on the result
                        bntFav.setImageResource(if (it) R.drawable.ic_favorited else R.drawable.ic_fav)
                    }
                }
            }
        }
    }
}