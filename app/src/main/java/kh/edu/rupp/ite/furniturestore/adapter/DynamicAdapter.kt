package kh.edu.rupp.ite.furniturestore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class DynamicAdapter<T, VB : ViewBinding>(
    private val inflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    private val onBind: (view: View, item: T, binding: VB) -> Unit
) : RecyclerView.Adapter<DynamicAdapter<T, VB>.ViewHolder>() {

    private var data: List<T> = listOf()

    fun setData(data: List<T>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T {
        return data[position]
    }

    inner class ViewHolder(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = inflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBind(holder.itemView, data[position], holder.binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
