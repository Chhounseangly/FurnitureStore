package kh.edu.rupp.ite.furniturestore.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kh.edu.rupp.ite.furniturestore.model.api.model.ApiData
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.model.api.model.ProductByCate
import kh.edu.rupp.ite.furniturestore.model.api.model.Res
import kh.edu.rupp.ite.furniturestore.model.api.service.RetrofitInstance

class CategoriesViewModel : BaseViewModel() {

    // LiveData to observe changes in the list of category types
    private val _categoryTypesData = MutableLiveData<ApiData<Res<List<CategoryTypes>>>>()
    val categoryTypesData: LiveData<ApiData<Res<List<CategoryTypes>>>>
        get() = _categoryTypesData

    // LiveData to observe changes in the product list based on selected category
    private val _productByCategory = MutableLiveData<ApiData<Res<ProductByCate>>>()
    val productByCategory: LiveData<ApiData<Res<ProductByCate>>>
        get() = _productByCategory

    // Function to load the list of available category types
    fun loadCategoryTypes() {
        performApiCall(_categoryTypesData, { RetrofitInstance.get().api.loadCategories() })
    }

    // Function to load products based on the selected category
    fun loadProductByCategoryApi(id: Int) {
        performApiCall(_productByCategory, { RetrofitInstance.get().api.loadProductsByCategory(id) })
    }
}
