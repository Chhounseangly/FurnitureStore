package kh.edu.rupp.ite.furniturestore.view.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.model.api.model.ImageUrls
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.ProductDetailViewModel

class ProductDetailActivity : AppCompatActivity() {

    private var id = 0
    private lateinit var price: TextView
    private lateinit var desc: TextView
    private lateinit var name: TextView
    private lateinit var seeMoreBtn: TextView

    private val productDetailViewModel = ProductDetailViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        //get id from xml files
        desc = findViewById(R.id.description)
        seeMoreBtn = findViewById(R.id.seeMoreBtn)
        price = findViewById(R.id.price)
        name = findViewById(R.id.name)

        // Get id from previous activity
        val intent = intent
        id = intent.getIntExtra("id", 0)

        //passing id to loadProductDetail
        productDetailViewModel.loadProductDetail(id)
        //passing data to display slider image
        productDetailViewModel.productsData.observe(this){
          when(it.status){
              Status.Processing -> null
              Status.Success ->  {
                  name.text = it.data?.name
                  price.text = "$ " + it.data?.price.toString()
                  desc.text = it.data?.description
                  it.data?.imageUrls?.let {
                      it1 -> displaySliderImages(it1)
                  }
              }
              else ->{}
          }
        }

        // If the TextView's height is more than 3 lines, set the visibility of the seemoreButton to VISIBLE.
        if (desc.lineCount > 10) {
            seeMoreBtn.visibility = View.VISIBLE
        }
        toggleTextViewMaxLines(seeMoreBtn)

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

    //function back to prev activity
    private fun prevBack() {
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    //function display only image slider
    private fun displaySliderImages(product: List<ImageUrls>) {
        val sliderModels = ArrayList<SlideModel>()
        val carousel = findViewById<ImageSlider>(R.id.carousel)
        for (i in product){
            sliderModels.add(SlideModel(i.imageUrl, ScaleTypes.FIT))
        }
        carousel.setImageList(sliderModels, ScaleTypes.FIT)
    }

}