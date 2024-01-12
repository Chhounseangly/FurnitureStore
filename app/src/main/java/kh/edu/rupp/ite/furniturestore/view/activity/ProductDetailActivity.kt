package kh.edu.rupp.ite.furniturestore.view.activity

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.ActivityMainBinding
import kh.edu.rupp.ite.furniturestore.databinding.ActivityProductDetailBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderCarouselBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.ImageUrls
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.utility.AppPreference
import kh.edu.rupp.ite.furniturestore.viewmodel.BadgesQuantityStoring
import kh.edu.rupp.ite.furniturestore.viewmodel.ProductDetailViewModel
import kh.edu.rupp.ite.furniturestore.viewmodel.ShoppingCartViewModel

class ProductDetailActivity :
    BaseActivity<ActivityProductDetailBinding>(ActivityProductDetailBinding::inflate) {
    private val badgesQuantityStoring: BadgesQuantityStoring by viewModels()

    private lateinit var shoppingCartViewModel: ShoppingCartViewModel

    private var id = 0
    private val productDetailViewModel = ProductDetailViewModel()

    private val lytLoading: View by lazy { binding.lytLoading }
    private val loading: ProgressBar by lazy { binding.loadingCircle }

    override fun initActions() {
        supportActionBar?.hide()

        shoppingCartViewModel = ViewModelProvider(this)[ShoppingCartViewModel::class.java]
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
                    showCircleLoading(lytLoading, loading)
                }
                Status.Success -> {
                    hideCircleLoading(lytLoading, loading)
                    it.data?.let { data ->
                        displayUi(data.data)
                    }
                }

                else -> {
                    // Handle other states if needed
                }
            }
        }
    }

    private fun displayUi(data: Product) {
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

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
            addToCartBtn.setOnClickListener {
                val token = AppPreference.get(this@ProductDetailActivity).getToken()
                if (token == null) {
                    Snackbar.make(
                        binding.root,
                        "Please login to add product to shopping cart",
                        Snackbar.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                val toastMessage = shoppingCartViewModel.addProductToShoppingCart(data.id)
                if (toastMessage === "Product existed on shopping cart") {
                    Snackbar.make(
                        binding.root,
                        toastMessage,
                        Snackbar.LENGTH_LONG
                    ).show()
                }else {
                    Snackbar.make(
                        binding.root,
                        toastMessage,
                        Snackbar.LENGTH_LONG
                    ).show()
                    badgesQuantityStoring.setQtyShoppingCart(1)
                    badgesQuantityStoring.qtyShoppingCart.observe(this@ProductDetailActivity){
                        setupBadge(R.id.mnuCart, it, activityMainBinding)
                    }
                }
            }
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

                Picasso.get().load(item.imageUrl)
                    .placeholder(loadingImg(this))
                    .error(R.drawable.ic_error)
                    .into(binding.carouselImageView)
            }

        // Set up CarouselAdapter and display carousel images
        carouselAdapter.setData(carouselSlider)
        carouselRecyclerView.adapter = carouselAdapter
    }
}
