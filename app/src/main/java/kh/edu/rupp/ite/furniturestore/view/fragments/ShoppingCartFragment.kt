package kh.edu.rupp.ite.furniturestore.view.fragments

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.ShoppingCartAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCartBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ObjectPayment
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.CheckoutActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.PaymentViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class ShoppingCartFragment : BaseFragment<FragmentCartBinding>(FragmentCartBinding::inflate) {
    // Adapter for the shopping cart
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter

    // Swipe-to-refresh layout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // Shimmer effect for loading
    private lateinit var cartContainerLoading: ShimmerFrameLayout

    // ViewModels for shopping cart and payment
    private lateinit var shoppingCartViewModel: ShoppingCartViewModel
    private lateinit var paymentViewModel: PaymentViewModel

    override fun bindUi() {
        swipeRefreshLayout = binding.refreshLayout
        cartContainerLoading = binding.cartContainerLoading
    }

    override fun initFields() {
        // Initialize ViewModels
        shoppingCartViewModel = ViewModelProvider(requireActivity())[ShoppingCartViewModel::class.java]
        paymentViewModel = ViewModelProvider(requireActivity())[PaymentViewModel::class.java]
    }

    override fun initActions() {

    }

    override fun setupListeners() {
        // Set up refresh listener to reload the shopping cart data
        swipeRefreshLayout.setOnRefreshListener {
            shoppingCartViewModel.loadProductsCartData()
        }
    }

    override fun setupObservers() {
        // Observe changes in the shopping cart items
        shoppingCartViewModel.shoppingCartItems.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> showLoadingAnimation(cartContainerLoading)
                Status.Success -> it.data?.let { it1 ->
                    // Display the shopping cart items
                    displayProductCart(it1)
                    // Calculate and display the total price
                    shoppingCartViewModel.calculateTotalPrice(it.data)
                    swipeRefreshLayout.isRefreshing = false
                    hideLoadingAnimation(cartContainerLoading)
                }

                else -> {
                    swipeRefreshLayout.isRefreshing = false
                    hideLoadingAnimation(cartContainerLoading)
                }
            }
        }

        // Observe changes in the total price
        shoppingCartViewModel.totalPrice.observe(viewLifecycleOwner) {
            binding.totalPrice.text = "$ " + it.toString()
        }

        // Observe changes in the item count
        shoppingCartViewModel.itemCount.observe(viewLifecycleOwner) {
            binding.itemsCount.text = it.toString()
        }
    }

    // Function to display the shopping cart items in the RecyclerView
    private fun displayProductCart(shoppingCart: List<ShoppingCart>) {
        // Set up RecyclerView layout manager
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.shoppingCartRecyclerView.layoutManager = linearLayoutManager

        // Set up shopping cart adapter
        shoppingCartAdapter = ShoppingCartAdapter(shoppingCartViewModel)
        shoppingCartAdapter.submitList(shoppingCart)
        binding.shoppingCartRecyclerView.adapter = shoppingCartAdapter

        // Attach ItemTouchHelper to RecyclerView for swipe-to-delete
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.shoppingCartRecyclerView)

        // Set up checkout button click listener
        binding.checkoutBtn.setOnClickListener {
            // Prepare data for payment and navigate to CheckoutActivity
            val list = ArrayList<ObjectPayment>()
            for (i in shoppingCart) {
                list.addAll(listOf(ObjectPayment(i.product_id, i.id, i.qty, i.product.price)))
            }

            // Passing data and navigation to CheckoutActivity
            val activityCheckoutIntent = CheckoutActivity.newIntent(requireContext(), list)
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
                // Draw UI for swipe-to-delete
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

    // Function to draw the UI for swipe-to-delete action in the RecyclerView
    private fun drawSwipeToDelete(c: Canvas, viewHolder: RecyclerView.ViewHolder, dX: Float) {
        // Get the current item view
        val itemView = viewHolder.itemView

        // Calculate the height of the item view
        val height = itemView.bottom.toFloat() - itemView.top.toFloat()

        // Calculate the width of the delete area as one-third of the item height
        val width = height / 3

        // Create a Paint object for drawing
        val paint = Paint()
        paint.isAntiAlias = true

        // Check if the swipe is to the left (negative dX)
        if (dX < 0) {
            // Draw a red background for the delete action
            paint.color = Color.parseColor("#D32F2F")
            val background = RectF(
                itemView.right.toFloat() + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            c.drawRect(background, paint)

            // Draw the delete icon on the background
            val deleteIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_cart)
            if (deleteIcon != null) {
                val iconDestination = RectF(
                    itemView.right.toFloat() - 2 * width,
                    itemView.top.toFloat() + width,
                    itemView.right.toFloat() - width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(deleteIcon, null, iconDestination, paint)
            }

            // Draw the "Delete" text
            paint.color = Color.WHITE
            paint.textSize = 44f
            paint.textAlign = Paint.Align.CENTER
            val deleteText = "Delete"
            val textX = itemView.right.toFloat() - 2 * width + 40
            val textY = itemView.top.toFloat() + height / 2
            c.drawText(deleteText, textX, textY, paint)
        }
    }
}
