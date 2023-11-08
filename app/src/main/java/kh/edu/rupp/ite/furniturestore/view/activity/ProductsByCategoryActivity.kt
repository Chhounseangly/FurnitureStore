package kh.edu.rupp.ite.furniturestore.view.activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductList


class ProductsByCategoryActivity: AppCompatActivity() {
    private var productList = ArrayList<ProductList>()
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
        title = intent.getStringExtra("title").toString()
        val titleTypeCate = findViewById<TextView>(R.id.titleTypeCate)

        //assign category title to appBar
        titleTypeCate.text = title

        for (i in 1..10){
            productList.add(
                ProductList(
                    i,
                    "Table",
                    "https://i4.komnit.com/store/upload/images/express_2207/112290-ARJDYN/1657316942-ARJDYN.jpg",
                    50,
                    1
                ),
            )
        }
        displayProductByCate(productList)
    }

    //method prev back
    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    //display Products By Category
    private fun displayProductByCate(productsList: ArrayList<ProductList>){
        val gridLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        val recyclerProductsByCate = findViewById<RecyclerView>(R.id.recyclerProductsByCate)
        recyclerProductsByCate.layoutManager = gridLayoutManager
//        val productListAdapter = ProductListAdapter()
//        productListAdapter.submitList(productsList)
//        recyclerProductsByCate.adapter = productListAdapter
    }

}