package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.view.View
import android.widget.TextView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.ActivityProductDetailBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderCarouselBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ImageUrls
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.viewmodel.ProductDetailViewModel

class ProductDetailActivity :
    BaseActivity<ActivityProductDetailBinding>(ActivityProductDetailBinding::inflate) {

    private var id = 0
    private val productDetailViewModel = ProductDetailViewModel()

    override fun initActions() {
        // Get id from previous activity
        val intent = intent
        id = intent.getIntExtra("id", 0)

        // Pass id to loadProductDetail
        productDetailViewModel.loadProductDetail(id)

        // If the TextView's height is more than 10 lines, set the visibility of the seeMoreButton to VISIBLE.
        if (binding.description.lineCount > 10) {
            binding.seeMoreBtn.visibility = View.VISIBLE
        }
        toggleTextViewMaxLines(binding.seeMoreBtn)
        prevBack(binding.backBtn)
    }

    override fun setupListeners() {

    }

    override fun setupObservers() {
        // Observe changes in product data
        productDetailViewModel.productsData.observe(this) {
            when (it.status) {
                Status.Processing -> {
                    // Handle processing state if needed
                }

                Status.Success -> {
                    it.data?.let { data ->
                        displayUi(data)
                    }
                }

                else -> {
                    // Handle other states if needed
                }
            }
        }
    }

    private fun displayUi(data: Product) {
        // Display carousel of product images
        data.imageUrls.let { img ->
            if (img != null) {
                displayCarousel(img)
            }
        }

        with(binding) {
            // Display basic product information
            name.text = data.name
            price.text = "$ " + data.price.toString()
            description.text = data.description

            // Set favorite button based on the isFavorite flag
            bntFav.setImageResource(
                when (data.is_favorite?.is_favourited) {
                    1 -> R.drawable.ic_favorited
                    0 -> R.drawable.ic_fav
                    else -> R.drawable.ic_fav
                }
            )

            // Favorite button click listener
            bntFav.setOnClickListener {
                productDetailViewModel.toggleFavorite(data) { result ->
                    // Set the favorite button image based on the result
                    bntFav.setImageResource(
                        if (result) R.drawable.ic_favorited
                        else R.drawable.ic_fav
                    )
                }
            }
        }
    }

    private fun toggleTextViewMaxLines(seeMoreBtn: TextView) {
        // Toggle the visibility of the remaining lines of the TextView when seeMoreButton is clicked.
        seeMoreBtn.setOnClickListener {
            if (binding.description.maxLines == 3) {
                binding.description.maxLines = Int.MAX_VALUE
                seeMoreBtn.text = "See less"
            } else {
                binding.description.maxLines = 3
                seeMoreBtn.text = "See more"
            }
        }
    }

    private fun displayCarousel(carouselSlider: List<ImageUrls>) {
        val carouselRecyclerView = binding.carouselRecyclerView

        // Set up CarouselLayoutManager and attach to RecyclerView
        carouselRecyclerView.layoutManager = CarouselLayoutManager()
        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(carouselRecyclerView)

        val carouselAdapter =
            DynamicAdapter<ImageUrls, ViewHolderCarouselBinding>(ViewHolderCarouselBinding::inflate) { view, item, binding ->
                // Add a listener to preview
                view.setOnClickListener {
                    val intent = Intent(it.context, PreviewImageActivity::class.java)
                    intent.putExtra("imageUrl", item.imageUrl)
                    it.context.startActivity(intent)
                }
                Picasso.get().load(item.imageUrl).into(binding.carouselImageView)
            }

        // Set up CarouselAdapter and display carousel images
        carouselAdapter.setData(carouselSlider)
        carouselRecyclerView.adapter = carouselAdapter
    }
}
