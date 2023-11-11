package kh.edu.rupp.ite.furniturestore.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCategoryBinding
import kh.edu.rupp.ite.furniturestore.adapter.CategoryTypesAdapter
import kh.edu.rupp.ite.furniturestore.viewmodel.CategoriesViewModel

class CategoryFragment() : Fragment() {

    private lateinit var fragmentCategoryBinding: FragmentCategoryBinding

    private lateinit var categoriesViewModel: CategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCategoryBinding = FragmentCategoryBinding.inflate(inflater, container, false)
        return fragmentCategoryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //fetching data from view Model to display
        categoriesViewModel.loadCategoryTypes()
        categoriesViewModel.categoryTypesData.observe(viewLifecycleOwner){
            when(it.status){
                200 -> it.data?.let { it1 -> displayCategory(it1) }
                else -> {}
            }
        }
    }


    //function display category
    private fun displayCategory(categoryTypes: List<CategoryTypes>){

        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        fragmentCategoryBinding.categoryRecyclerView.layoutManager = gridLayoutManager

        val categoryTypesAdapter = CategoryTypesAdapter()
        categoryTypesAdapter.submitList(categoryTypes)
        fragmentCategoryBinding.categoryRecyclerView.adapter = categoryTypesAdapter
    }
}