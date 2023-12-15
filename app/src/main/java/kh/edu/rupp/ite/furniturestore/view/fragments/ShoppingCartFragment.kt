package kh.edu.rupp.ite.furniturestore.view.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.ShoppingCartAdapter
import kh.edu.rupp.ite.furniturestore.custom_method.LoadingMethod
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCartBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ObjectPayment
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.CheckoutActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.PaymentViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class ShoppingCartFragment : Fragment() {
    private lateinit var fragmentCartBinding: FragmentCartBinding
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var cartContainerLoading: ShimmerFrameLayout
    private lateinit var shoppingCartViewModel: ShoppingCartViewModel
    private lateinit var paymentViewModel: PaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false)
        shoppingCartViewModel = ViewModelProvider(requireActivity())[ShoppingCartViewModel::class.java]
        paymentViewModel = ViewModelProvider(requireActivity())[PaymentViewModel::class.java]
        // Set up SwipeRefreshLayout
        swipeRefreshLayout = fragmentCartBinding.refreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            shoppingCartViewModel.loadProductsCartData()
        }

        return fragmentCartBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Shimmer animation view
        cartContainerLoading = fragmentCartBinding.cartContainerLoading

        shoppingCartViewModel.shoppingCartItems.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> LoadingMethod().showLoadingAnimation(cartContainerLoading)
                Status.Success -> it.data?.let { it1 ->
                    displayProductCart(it1)
                    shoppingCartViewModel.calculateTotalPrice(it.data)
                    swipeRefreshLayout.isRefreshing = false
                    LoadingMethod().hideLoadingAnimation(cartContainerLoading)
                }

                else -> {
                    swipeRefreshLayout.isRefreshing = false
                    LoadingMethod().hideLoadingAnimation(cartContainerLoading)
                }
            }
        }

        shoppingCartViewModel.totalPrice.observe(viewLifecycleOwner) {
            fragmentCartBinding.totalPrice.text = "$ " + it.toString()
        }

        shoppingCartViewModel.itemCount.observe(viewLifecycleOwner) {
            fragmentCartBinding.itemsCount.text = it.toString()
        }

        shoppingCartViewModel.loadProductsCartData()
    }

    private fun displayProductCart(shoppingCart: List<ShoppingCart>) {
        // Set up RecyclerView layout manager
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentCartBinding.shoppingCartRecyclerView.layoutManager = linearLayoutManager

        // Set up shopping cart adapter
        shoppingCartAdapter = ShoppingCartAdapter(shoppingCartViewModel)
        shoppingCartAdapter.submitList(shoppingCart)
        fragmentCartBinding.shoppingCartRecyclerView.adapter = shoppingCartAdapter

        // Attach ItemTouchHelper to RecyclerView for swipe-to-delete
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(fragmentCartBinding.shoppingCartRecyclerView)

        // Set up checkout button click listener
        fragmentCartBinding.checkoutBtn.setOnClickListener {

            val list = ArrayList<ObjectPayment>()
            for (i in shoppingCart){
                list.addAll(listOf(ObjectPayment(i.product_id, i.id, i.qty, i.product.price)))
            }

            val activityCheckoutIntent = Intent(context, CheckoutActivity::class.java)
            activityCheckoutIntent.putParcelableArrayListExtra("shoppingCartObject", ArrayList(list) )

            startActivity(activityCheckoutIntent)
        }
    }

    // ItemTouchHelper callback for swipe-to-delete
    private val simpleItemTouchCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // final int fromPos = viewHolder.getAdapterPosition();
                // final int toPos = target.getAdapterPosition();
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                swipeDir: Int
            ) {
                // Confirm deletion with an AlertDialog
                showDeleteConfirmationDialog(viewHolder.adapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                drawSwipeToDelete(c, viewHolder, dX)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }

    // Function to show an AlertDialog for confirming deletion
    private fun showDeleteConfirmationDialog(position: Int) {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Confirm Delete")
        alertDialog.setMessage("Are you sure you want to delete this item?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            // Remove Product from Shopping Cart if user clicks yes
            val productId = shoppingCartAdapter.currentList[position].id
            shoppingCartViewModel.deleteProductShoppingCart(productId)
        }
        alertDialog.setNegativeButton("No") { _, _ ->
            // Undo the swipe
            shoppingCartAdapter.notifyItemChanged(position)
        }
        alertDialog.show()
    }

    // Function to draw swipe-to-delete UI
    private fun drawSwipeToDelete(c: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float) {
        val itemView = viewHolder.itemView
        // Take bottom of card and top of card remove it
        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
        // 3 / of height display text delete
        val width = height / 3

        val p = Paint()
        p.isAntiAlias = true

        if (dX < 0) {
            // Draw red background for delete action
            p.color = Color.parseColor("#D32F2F")
            val background = RectF(
                itemView.right.toFloat() + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            c.drawRect(background, p)

            // Draw delete icon
            val icon = BitmapFactory.decodeResource(resources, R.drawable.ic_cart)
            if (icon != null) {
                val iconDest = RectF(
                    itemView.right.toFloat() - 2 * width,
                    itemView.top.toFloat() + width,
                    itemView.right.toFloat() - width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(icon, null, iconDest, p)
            }

            // Draw the "Delete" text
            p.color = Color.WHITE
            p.textSize = 44f
            p.textAlign = Paint.Align.CENTER
            val text = "Delete"
            val textX = itemView.right.toFloat() - 2 * width + 40
            val textY = itemView.top.toFloat() + height / 2
            c.drawText(text, textX, textY, p)
        }
    }
}