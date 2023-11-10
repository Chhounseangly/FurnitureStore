package kh.edu.rupp.ite.furniturestore.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kh.edu.rupp.ite.furniturestore.view.activity.ProductsByCategoryActivity
import kh.edu.rupp.ite.furniturestore.model.api.model.CategoryTypes
import kh.edu.rupp.ite.furniturestore.databinding.ViewHolderCategoryTypeBinding

class CategoryTypesAdapter: ListAdapter<CategoryTypes, CategoryTypesAdapter.CategoryTypesViewHolder>(
    CategoryTypesAdapter()
) {
    private class CategoryTypesAdapter : DiffUtil.ItemCallback<CategoryTypes>() {
        override fun areItemsTheSame(oldItem: CategoryTypes, newItem: CategoryTypes): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: CategoryTypes, newItem: CategoryTypes): Boolean =
            oldItem.id == newItem.id
    }

    class CategoryTypesViewHolder(
        private val viewHolderCategoryTypeBinding: ViewHolderCategoryTypeBinding
    ) : RecyclerView.ViewHolder(viewHolderCategoryTypeBinding.root) {
        fun bind(categoryTypes: CategoryTypes) {
            //add image url to ImageView by Library Picasso
//            Picasso.get().load(categoryTypes.imageUrl).into(viewHolderCategoryTypeBinding.img)
            viewHolderCategoryTypeBinding.name.text = categoryTypes.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryTypesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewHolderCategoryTypeBinding.inflate(layoutInflater, parent, false)

        return CategoryTypesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryTypesViewHolder, position: Int) {
        val categoryTypes = getItem(position)
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ProductsByCategoryActivity::class.java)
            intent.putExtra("id", categoryTypes.id)
            intent.putExtra("name", categoryTypes.name)
            it.context.startActivity(intent)
        }
        holder.bind(categoryTypes)
    }
}






