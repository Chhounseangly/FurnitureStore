package kh.edu.rupp.ite.furniturestore.view.fragments


import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
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
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel


class ShoppingCartFragment() : Fragment() {

    private lateinit var fragmentCartBinding: FragmentCartBinding
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter
//    private val shoppingCartViewModel = ShoppingCartViewModel()

    private lateinit var shoppingCartViewModel: ShoppingCartViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var cartContainerLoading: ShimmerFrameLayout


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        fragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false)

        shoppingCartViewModel = ViewModelProvider(this)[ShoppingCartViewModel::class.java]
        shoppingCartViewModel.loadProductsCartData()
        swipeRefreshLayout = fragmentCartBinding.refreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            shoppingCartViewModel.loadProductsCartData()
        }
        return fragmentCartBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartContainerLoading = fragmentCartBinding.cartContainerLoading

        shoppingCartViewModel.shoppingCartItems.observe(viewLifecycleOwner) {
            when (it.status) {
                102 -> LoadingMethod().showLoadingAnimation(cartContainerLoading)
                200 -> it.data?.let { it1 ->
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
            fragmentCartBinding.totalPrice.text = it.toString()
        }
    }

    private fun displayProductCart(shoppingCart: List<ShoppingCart>) {
        // Create GridLayout Manager
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentCartBinding.shoppingCartRecyclerView.layoutManager = linearLayoutManager
        // Create adapter
        shoppingCartAdapter = ShoppingCartAdapter(shoppingCartViewModel)
        shoppingCartAdapter.submitList(shoppingCart)
        fragmentCartBinding.shoppingCartRecyclerView.adapter = shoppingCartAdapter

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(fragmentCartBinding.shoppingCartRecyclerView)
    }

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

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val position = viewHolder.adapterPosition
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setTitle("Confirm Delete")
                    alertDialog.setMessage("Are you sure you want to delete this item?")
                    alertDialog.setPositiveButton("Yes") { _, _ ->
                        // Remove Product from Shopping Cart if user click yes
                        val productId = shoppingCartAdapter.currentList[position].id
                        shoppingCartViewModel.deleteProductShoppingCart(productId)

                    }
                    alertDialog.setNegativeButton("No") { _, _ ->
                        // Undo the swipe
                        val adapter = fragmentCartBinding.shoppingCartRecyclerView.adapter as ShoppingCartAdapter
                        adapter.notifyItemChanged(position)
                    }
                    alertDialog.show()
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
                    var icon: Bitmap?
                    val p = Paint()
                    p.isAntiAlias = true
                    if (actionState === ItemTouchHelper.ACTION_STATE_SWIPE) {
                        val itemView = viewHolder.itemView
                        //take bottom of card and top of card remove it
                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        // 3 / of height display text delete
                        val width = height / 3
                        if (dX < 0) {
                            p.color = Color.parseColor("#D32F2F")
                            val background = RectF(
                                    itemView.right.toFloat() + dX,
                                    itemView.top.toFloat(),
                                    itemView.right.toFloat(),
                                    itemView.bottom.toFloat()
                            )
                            c.drawRect(background, p)
                            icon = BitmapFactory.decodeResource(resources, R.drawable.ic_cart)
                            if (icon != null) {
                                val icon_dest = RectF(
                                        itemView.right.toFloat() - 2 * width,
                                        itemView.top.toFloat() + width,
                                        itemView.right.toFloat() - width,
                                        itemView.bottom.toFloat() - width
                                )
                                c.drawBitmap(icon, null, icon_dest, p)
                            }
                            // Draw the "Delete" text
                            p.color = Color.WHITE
                            p.textSize = 36f
                            p.textAlign = Paint.Align.CENTER
                            val text = "Delete"
                            val textX = itemView.right.toFloat() - 2 * width + 40
                            val textY = itemView.top.toFloat() + 40
                            c.drawText(text, textX, textY, p)
                        }
                    }
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
}