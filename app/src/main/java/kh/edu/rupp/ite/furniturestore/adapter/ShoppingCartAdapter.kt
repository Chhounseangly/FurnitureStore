package kh.edu.rupp.ite.furniturestore.adapter

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductCartBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel
import java.util.concurrent.TimeUnit

class ShoppingCartAdapter(private var shoppingCartViewModel: ShoppingCartViewModel)
    : ListAdapter<ShoppingCart, ShoppingCartAdapter.ProductCartViewHolder>(
    object : DiffUtil.ItemCallback<ShoppingCart>() {
        override fun areItemsTheSame(oldItem: ShoppingCart, newItem: ShoppingCart) = oldItem == newItem
        override fun areContentsTheSame(oldItem: ShoppingCart, newItem: ShoppingCart) = oldItem.id == newItem.id
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCartViewHolder {
        val binding = ViewHolderProductCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ProductCartViewHolder(binding, shoppingCartViewModel)
    }

    override fun onBindViewHolder(holder: ProductCartViewHolder, position: Int) {
        val products = getItem(position)
        holder.bind(products)
    }

    class ProductCartViewHolder(
        private val viewHolderProductCartBinding: ViewHolderProductCartBinding,
        private val shoppingCartViewModel: ShoppingCartViewModel
    ) : RecyclerView.ViewHolder(viewHolderProductCartBinding.root) {

        private val handler = Handler(Looper.getMainLooper())
        private val delayMillis = TimeUnit.SECONDS.toMillis(2)

        fun bind(item: ShoppingCart) {
            //passing data from api to view
            with(viewHolderProductCartBinding) {
                Picasso.get().load(item.product.imageUrl).into(productImg)
                nameProduct.text = item.product.name
                "$ ${item.product.price}".also { price.text = it }
                displayQty.text = item.qty.toString()

                productImg.setOnClickListener {
                    val intent = Intent(it.context, ProductDetailActivity::class.java)
                    intent.putExtra("id", item.product_id)
                    it.context.startActivity(intent)
                }

                //handle action of button
                with(shoppingCartViewModel) {
                    // Combine button click listeners
                    val qtyButtonClick = { operation: String ->
                        qtyOperation(item, operation)
                        displayQty.text = item.qty.toString()
                        handler.removeCallbacksAndMessages(null)
                        handler.postDelayed({ executingQtyToApi() }, delayMillis)
                    }

                    addBtn.setOnClickListener { qtyButtonClick("increaseQty") }
                    minusBtn.setOnClickListener { qtyButtonClick("decreaseQty") }
                }
            }
        }
    }
}