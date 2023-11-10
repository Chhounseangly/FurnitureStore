package kh.edu.rupp.ite.furniturestore.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductDetail

class ProductDetailActivity : AppCompatActivity() {

    private var id = 0
    private var price = 0
    private lateinit var name: String
    private lateinit var imageUrl: String
    private lateinit var desc: TextView
    private lateinit var totalPrice: TextView
    private lateinit var productCount: TextView
    private lateinit var addBtn: ImageButton
    private lateinit var minusBtn: ImageButton

    private lateinit var seeMoreBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        desc = findViewById(R.id.description)
        seeMoreBtn = findViewById(R.id.seeMoreBtn)

        // Get data from previous activity
        val intent = intent
        id = intent.getIntExtra("id", 0)
        name = intent.getStringExtra("name").toString()
        price = intent.getIntExtra("price", 0)
        imageUrl = intent.getStringExtra("imageUrl").toString()

        Log.d("detail", "$id $name $price $imageUrl")

        // If the TextView's height is more than 3 lines, set the visibility of the seemoreButton to VISIBLE.
        if (desc.lineCount > 3) {
            seeMoreBtn.visibility = View.VISIBLE
        }
        toggleTextViewMaxLines(seeMoreBtn)

        Picasso.get().load(imageUrl).into(findViewById<ImageView>(R.id.imageSlider))

        prevBack()
    }

    private fun toggleTextViewMaxLines(seeMoreBtn: TextView) {
        // When the seeMoreButton is clicked, toggle the visibility of the remaining lines of the TextView.
        seeMoreBtn.setOnClickListener {
            if (desc.maxLines == 3) {
                desc.maxLines = Int.MAX_VALUE
                seeMoreBtn.text = "See less"
            } else {
                desc.maxLines = 3
                seeMoreBtn.text = "See more"
            }
        }
    }

    //function prev back
    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun displayProductDetail(productDetail: ProductDetail) {

    }

}