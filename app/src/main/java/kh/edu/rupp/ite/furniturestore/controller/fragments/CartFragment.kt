package kh.edu.rupp.ite.furniturestore.controller.fragments


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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.controller.adapter.ProductAddToCartAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCartBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList


class CartFragment() : Fragment() {

    private lateinit var fragmentCartBinding: FragmentCartBinding
    private var id = 0
    private var price = 0
    private lateinit var title: String
    private lateinit var imageUrl: String
    private var products = ArrayList<ProductList>()

    private lateinit var productAddToCartAdapter: ProductAddToCartAdapter

    private var totalPrice = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false)
        val arguments = arguments
        val test = arguments?.getInt("id")

        return fragmentCartBinding.root
        // Get data from previous activity
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (i in 1..10) {
            products.add(
                ProductList(
                    i,
                    "Table",
                    "https://i4.komnit.com/store/upload/images/express_2207/112290-ARJDYN/1657316942-ARJDYN.jpg",
                    50 + i
                ),
            )
        }

        displayProductCart(products)
        for (product in products) {
            totalPrice += product.price
        }

        fragmentCartBinding.totalPrice.text = "$ ${totalPrice}"


    }

    private fun displayProductCart(productsList: ArrayList<ProductList>) {
        // Create GridLayout Manager
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        fragmentCartBinding.productsAddRecyclerView.layoutManager = linearLayoutManager

        // Create adapter
        productAddToCartAdapter = ProductAddToCartAdapter()
        productAddToCartAdapter.submitList(productsList)
        fragmentCartBinding.productsAddRecyclerView.adapter = productAddToCartAdapter

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(fragmentCartBinding.productsAddRecyclerView)


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
                    // Remove item from the data list
                    val productList = productAddToCartAdapter.currentList.toMutableList()
                    productList.removeAt(position)
                    productAddToCartAdapter.submitList(productList)
                }
                alertDialog.setNegativeButton("No") { _, _ ->
                    // Undo the swipe
                    val adapter =
                        fragmentCartBinding.productsAddRecyclerView.adapter as ProductAddToCartAdapter
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