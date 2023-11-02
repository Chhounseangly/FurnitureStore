package kh.edu.rupp.ite.furniturestore.controller.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.controller.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.controller.fragments.CartFragment
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductItemBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList


class ProductListAdapter :
    ListAdapter<ProductList, ProductListAdapter.ProductListViewHolder>(ProductListAdapter()) {

    //constructor
    private class ProductListAdapter : DiffUtil.ItemCallback<ProductList>() {
        override fun areItemsTheSame(oldItem: ProductList, newItem: ProductList): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ProductList, newItem: ProductList): Boolean =
            oldItem.id == newItem.id
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderProductItemBinding.inflate(layoutInflater, parent, false)
        return ProductListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val products = getItem(position)
        val addToCartBtn = holder.viewHolderProductItemBinding.addToCartBtn
        val bundle = Bundle()
        addToCartBtn.setOnClickListener {
            bundle.putInt("id", products.id)
            bundle.putString("name", products.name)
            bundle.putInt("price", products.price)
            bundle.putString("image", products.imageUrl)
            val cartFragment = CartFragment()
            cartFragment.arguments = bundle
        }

        holder.bind(products)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ProductDetailActivity::class.java)
            intent.putExtra("id", products.id)
            intent.putExtra("title", products.name)
            intent.putExtra("price", products.price)
            intent.putExtra("imageUrl", products.imageUrl)
            it.context.startActivity(intent)
        }

    }

    class ProductListViewHolder(
        val viewHolderProductItemBinding: ViewHolderProductItemBinding
    ) : RecyclerView.ViewHolder(viewHolderProductItemBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(productList: ProductList) {
            //add image url to ImageView by Library Picasso
            Picasso.get().load(productList.imageUrl).into(viewHolderProductItemBinding.img)
            viewHolderProductItemBinding.title.text = productList.name
            viewHolderProductItemBinding.price.text = "$ " +productList.price.toString()
        }

    }

}





