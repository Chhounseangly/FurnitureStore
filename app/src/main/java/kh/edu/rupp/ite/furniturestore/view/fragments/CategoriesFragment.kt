package kh.edu.rupp.ite.furniturestore.view.fragments

import android.content.Intent
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.R
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCategoryBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductItemBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductByCate
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.CategoriesViewModel

class CategoriesFragment(private var id: Int) :
    BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {
    private val categoriesViewModel: CategoriesViewModel by viewModels()
    override fun bindUi() {
    }

    override fun initFields() {

    }

    override fun initActions() {
        // Load product data by category and category types
        categoriesViewModel.loadProductByCategoryApi(id)
        categoriesViewModel.loadCategoryTypes()
    }

    override fun setupListeners() {

    }

    override fun setupObservers() {
        // Set up observer for load product by category LiveData
        val loadingLoadProducts = binding.loadingLoadProducts
        categoriesViewModel.productByCategory.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> showLoadingAnimation(loadingLoadProducts)
                Status.Success -> {
                    it.data?.let { data -> displayProductByCate(data.data) }
                    hideLoadingAnimation(loadingLoadProducts)
                }

                else -> {
                    hideLoadingAnimation(loadingLoadProducts)
                }
            }
        }
    }

    // Display products by category in the RecyclerView
    private fun displayProductByCate(items: ProductByCate) {
        // Set up GridLayoutManager for the RecyclerView
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        val recyclerProductsByCate = binding.recyclerProductsByCate
        recyclerProductsByCate.layoutManager = gridLayoutManager

        // Create DynamicAdapter for products with ViewHolderProductItemBinding
        val productByCategoryAdapter =
            DynamicAdapter<Product, ViewHolderProductItemBinding>(ViewHolderProductItemBinding::inflate)
            { view, item, binding ->
                // Set click listener to navigate to ProductDetailActivity
                view.setOnClickListener {
                    val intent = Intent(it.context, ProductDetailActivity::class.java)
                    intent.putExtra("id", item.id)
                    it.context.startActivity(intent)
                }
                // Load product data into the ViewHolderProductItemBinding
                with(binding) {
                    Picasso.get()
                        .load(item.imageUrl)
                        .placeholder(loadingImg(requireContext()))
                        .error(R.drawable.ic_error)
                        .into(img)
                    name.text = item.name
                    price.text = "$ ${item.price}"
                }
            }

        // Set data to the adapter and attach it to the RecyclerView
        productByCategoryAdapter.setData(items.products)
        recyclerProductsByCate.adapter = productByCategoryAdapter
    }
}
