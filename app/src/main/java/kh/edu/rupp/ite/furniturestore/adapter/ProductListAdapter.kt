package kh.edu.rupp.ite.furniturestore.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductItemBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel


class ProductListAdapter(private var shoppingCartViewModel: ShoppingCartViewModel) :
    ListAdapter<Product, ProductListAdapter.ProductListViewHolder>(ProductListAdapter()) {

    //constructor
    private class ProductListAdapter : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
            oldItem.id == newItem.id
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderProductItemBinding.inflate(layoutInflater, parent, false)
        return ProductListViewHolder(binding, shoppingCartViewModel)
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
        private val viewHolderProductItemBinding: ViewHolderProductItemBinding,
        private val shoppingCartViewModel: ShoppingCartViewModel
    ) : RecyclerView.ViewHolder(viewHolderProductItemBinding.root) {
        fun bind(product: Product) {
            //add image url to ImageView by Library Picasso
            Picasso.get().load(product.imageUrl).into(viewHolderProductItemBinding.img)
            viewHolderProductItemBinding.name.text = product.name
            viewHolderProductItemBinding.price.text = "$ " +product.price.toString()

            viewHolderProductItemBinding.addToCartBtn.setOnClickListener {
                shoppingCartViewModel.addItemToCart(product)
            }
        }

    }

}





