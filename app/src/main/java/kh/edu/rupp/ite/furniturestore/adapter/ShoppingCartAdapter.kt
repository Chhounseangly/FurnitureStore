package kh.edu.rupp.ite.furniturestore.adapter

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
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel
import java.util.concurrent.TimeUnit

class ShoppingCartAdapter(private var shoppingCartViewModel: ShoppingCartViewModel) :
    ListAdapter<ShoppingCart, ShoppingCartAdapter.ProductCartViewHolder>(
        ProductAddToCartAdapter()
    ) {
    //constructor
    private class ProductAddToCartAdapter : DiffUtil.ItemCallback<ShoppingCart>() {
        override fun areItemsTheSame(oldItem: ShoppingCart, newItem: ShoppingCart): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ShoppingCart, newItem: ShoppingCart): Boolean =
            oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCartViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderProductCartBinding.inflate(layoutInflater, parent, false)

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
        private val delayMillis = TimeUnit.SECONDS.toMillis(50)

        fun bind(item: ShoppingCart) {

            //passing data from api to view
            Picasso.get().load(item.product?.imageUrl).into(viewHolderProductCartBinding.productImg)
            viewHolderProductCartBinding.nameProduct.text = item.product?.name
            viewHolderProductCartBinding.price.text = item.product?.price.toString()
            viewHolderProductCartBinding.displayQty.text = item.qty.toString()

            //handle Increase Qty
            viewHolderProductCartBinding.addBtn.setOnClickListener {
                shoppingCartViewModel.qtyOperation(item, "increaseQty")
                viewHolderProductCartBinding.displayQty.text = item.qty.toString()
                // it will delay 50ms to call executingQtyToApi()
                handler.postDelayed({
                    shoppingCartViewModel.executingQtyToApi()
                }, delayMillis)
            }
            //handle Decrease Qty
            viewHolderProductCartBinding.minusBtn.setOnClickListener {
                shoppingCartViewModel.qtyOperation(item, "decreaseQty")
                viewHolderProductCartBinding.displayQty.text = item.qty.toString()
                // it will delay 50ms to call executingQtyToApi()
                handler.postDelayed({
                    shoppingCartViewModel.executingQtyToApi()
                }, delayMillis)
            }
        }
    }

}