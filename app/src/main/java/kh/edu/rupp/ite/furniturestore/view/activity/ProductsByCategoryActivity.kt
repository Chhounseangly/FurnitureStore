package kh.edu.rupp.ite.furniturestore.view.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.CategoriesViewModel


class ProductsByCategoryActivity : AppCompatActivity() {
    private val categoriesViewModel = CategoriesViewModel()
    private var id = 0
    private lateinit var title: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_by_category)

        //call method prev back
        prevBack()

        // Get data from previous activity
        val intent = intent
        id = intent.getIntExtra("id", 0)
        title = intent.getStringExtra("name").toString()
        val titleTypeCate = findViewById<TextView>(R.id.titleTypeCate)

        //assign category title to appBar
        titleTypeCate.text = title

        categoriesViewModel.loadProductByCategory(id)
        categoriesViewModel.productByCategory.observe(this) {
            when (it.status) {
                Status.Success -> it.data?.let { it1 ->
                    displayProductByCate(it1.products)
                }

                else -> {

                }
            }
        }
    }

    //method prev back
    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    //display Products By Category
    private fun displayProductByCate(productsList: List<Product>) {
        val gridLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        val recyclerProductsByCate = findViewById<RecyclerView>(R.id.recyclerProductsByCate)
        recyclerProductsByCate.layoutManager = gridLayoutManager
//        val productByCategoryAdapter = ProductByCategoryAdapter();
//        productByCategoryAdapter.submitList(productsList)
//        recyclerProductsByCate.adapter = productByCategoryAdapter
    }

}