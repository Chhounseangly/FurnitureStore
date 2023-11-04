package kh.edu.rupp.ite.furniturestore.controller.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.databinding.FragmentCategoryBinding
import kh.edu.rupp.ite.furniturestore.controller.adapter.CategoryTypesAdapter

class CategoryFragment() : Fragment() {

    private lateinit var fragmentCategoryBinding: FragmentCategoryBinding

    private var listCategoryTypes = ArrayList<CategoryTypes>()
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
        //hide button fab to Top

        listCategoryTypes = arrayListOf(
            CategoryTypes(1, "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png", "chair"),
            CategoryTypes(1, "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png", "chair"),
            CategoryTypes(1, "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png", "chair"),
            CategoryTypes(1, "https://res.cloudinary.com/drbdm4ucw/image/upload/v1693291855/chat_app/Images_Post/ldgvbyvxmzjwsqvlugd4.png", "chair")

        )
        displayCategory(listCategoryTypes)

    }

    private fun displayCategory(categoryTypes: List<CategoryTypes>){

        val gridLayoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        fragmentCategoryBinding.categoryRecyclerView.layoutManager = gridLayoutManager

        val categoryTypesAdapter = CategoryTypesAdapter()
        categoryTypesAdapter.submitList(categoryTypes)
        fragmentCategoryBinding.categoryRecyclerView.adapter = categoryTypesAdapter
    }
}