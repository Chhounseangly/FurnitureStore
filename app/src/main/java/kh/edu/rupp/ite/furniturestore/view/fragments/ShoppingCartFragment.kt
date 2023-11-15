package kh.edu.rupp.ite.furniturestore.view.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.ShoppingCartAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCartBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ShoppingCart
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class ShoppingCartFragment(private val shoppingCartViewModel: ShoppingCartViewModel) : Fragment() {

    private lateinit var fragmentCartBinding: FragmentCartBinding
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false)
        return fragmentCartBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize and set up the shopping cart view
        setupShoppingCart()
//        fragmentCartBinding.totalPrice.setOnClickListener {
//            shoppingCartViewModel.qtyOperation(1, 5);
//        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("ShoppingCartFragment", "onResume")
        // Refresh the shopping cart view on resume
        setupShoppingCart()
    }

    override fun onPause() {
        super.onPause()
        Log.d("ShoppingCartFragment", "onPause")
    }

    private fun setupShoppingCart() {
        // Observe changes in shopping cart items
        shoppingCartViewModel.shoppingCartItems.observe(viewLifecycleOwner) { cartData ->
            cartData.data?.let { shoppingCart ->
                // Display shopping cart items and update total price
                displayProductCart(shoppingCart)
                shoppingCartViewModel.updateTotalPrice(shoppingCart)
            }
        }

        // Observe changes in total price
        shoppingCartViewModel.totalPrice.observe(viewLifecycleOwner) { totalPrice ->
            // Update the total price text view
            fragmentCartBinding.totalPrice.text = totalPrice.toString()
            Log.d("ShoppingCartFragment", "Total Price: $totalPrice")
        }
    }

    // display products
    private fun displayProductCart(shoppingCart: List<ShoppingCart>) {
        // Create a LinearLayoutManager for the RecyclerView
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentCartBinding.shoppingCartRecyclerView.layoutManager = linearLayoutManager

        // Create and set up the adapter for shopping cart items
        shoppingCartAdapter = ShoppingCartAdapter(shoppingCartViewModel)
        shoppingCartAdapter.submitList(shoppingCart)
        fragmentCartBinding.shoppingCartRecyclerView.adapter = shoppingCartAdapter

        // Attach an ItemTouchHelper for swipe-to-delete functionality
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(fragmentCartBinding.shoppingCartRecyclerView)
    }

    // ItemTouchHelper for swipe-to-delete functionality
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
                    // Remove item from the data list
                    val productList = shoppingCartAdapter.currentList.toMutableList()
                    productList.removeAt(position)
                    shoppingCartAdapter.submitList(productList)
                }
                alertDialog.setNegativeButton("No") { _, _ ->
                    // Undo the swipe
                    val adapter =
                        fragmentCartBinding.shoppingCartRecyclerView.adapter as ShoppingCartAdapter
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