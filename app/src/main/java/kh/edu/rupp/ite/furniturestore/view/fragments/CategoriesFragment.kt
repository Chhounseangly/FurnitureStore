package kh.edu.rupp.ite.furniturestore.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.squareup.picasso.Picasso
import kh.edu.rupp.ite.furniturestore.adapter.DynamicAdapter
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCategoryBinding
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderProductItemBinding
import kh.edu.rupp.ite.furniturestore.model.api.model.Product
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductByCate
import kh.edu.rupp.ite.furniturestore.model.api.model.Status
import kh.edu.rupp.ite.furniturestore.view.activity.ProductDetailActivity
import kh.edu.rupp.ite.furniturestore.viewmodel.CategoriesViewModel

class CategoriesFragment(private var id: Int) : BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {
    private var categoriesViewModel = CategoriesViewModel()
    override fun bindUi() {
    }

    override fun initFields() {
        categoriesViewModel = ViewModelProvider(this)[CategoriesViewModel::class.java]
    }

    override fun initActions() {
        categoriesViewModel.loadProductByCategoryApi(id)
        categoriesViewModel.loadCategoryTypes()
    }

    override fun setupListeners() {

    }

    override fun setupObservers() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loadingLoadProducts = binding.loadingLoadProducts
        categoriesViewModel.productByCategory.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.Processing -> showLoadingAnimation(loadingLoadProducts)
                Status.Success -> {
                    it.data?.let { it1 -> displayProductByCate(it1.data) }
                    hideLoadingAnimation(loadingLoadProducts)
                }

                else -> {
                    hideLoadingAnimation(loadingLoadProducts)
                }
            }
        }
    }

    private fun displayProductByCate(items: ProductByCate) {
        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        val recyclerProductsByCate = binding.recyclerProductsByCate
        recyclerProductsByCate.layoutManager = gridLayoutManager


        val productByCategoryAdapter = DynamicAdapter<Product, ViewHolderProductItemBinding>(ViewHolderProductItemBinding::inflate){
            view, item, binding ->
            view.setOnClickListener {
                val intent = Intent(it.context, ProductDetailActivity::class.java)
                intent.putExtra("id", item.id)
                it.context.startActivity(intent)
            }
            with(binding) {
                Picasso.get().load(item.imageUrl).into(img)
                name.text = item.name
                price.text = "$ ${item.price}"
            }
        }

        productByCategoryAdapter.setData(items.products)

//        val productByCategoryAdapter = ProductByCategoryAdapter();
//        productByCategoryAdapter.submitList(items.products)
        recyclerProductsByCate.adapter = productByCategoryAdapter
    }
}