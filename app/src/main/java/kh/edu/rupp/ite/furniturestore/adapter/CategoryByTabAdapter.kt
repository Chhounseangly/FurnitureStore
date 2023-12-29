package kh.edu.rupp.ite.furniturestore.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.view.fragments.CategoriesFragment

class CategoryByTabAdapter(activity: FragmentActivity, private val data: List<CategoryTypes>) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return data.size
    }

    override fun createFragment(position: Int): Fragment {
        return CategoriesFragment.newInstance(data[position].id)
    }

}
