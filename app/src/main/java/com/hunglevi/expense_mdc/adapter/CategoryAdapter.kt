package com.hunglevi.expense_mdc.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hunglevi.expense_mdc.R
import com.hunglevi.expense_mdc.data.model.Category
import com.hunglevi.expense_mdc.databinding.ItemCategoryBinding

class CategoryAdapter(
    private var categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.categoryName.text = category.name
            binding.categoryIcon.setImageResource(getIconResource(category.icon)) // Example method to load icon
            binding.root.setOnClickListener {
                onItemClick(category)
            }
        }

        private fun getIconResource(icon: String): Int {
            val iconName = icon.split("/").last().split(".").firstOrNull()?.trim()?.lowercase() ?: ""
            val resId = binding.root.context.resources.getIdentifier(
                iconName,
                "drawable",
                binding.root.context.packageName
            )
            Log.d("CategoryAdapter", "Icon name: $iconName, Resource ID: $resId")
            return if (resId != 0) resId else R.drawable.category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    //    fun updateData(newCategories: List<Category>) {
//        categories = newCategories
//        notifyDataSetChanged()
//    }
    fun updateData(newCategories: List<Category>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = categories.size
            override fun getNewListSize(): Int = newCategories.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                categories[oldItemPosition].id == newCategories[newItemPosition].id

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                categories[oldItemPosition] == newCategories[newItemPosition]
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        categories = newCategories
        diffResult.dispatchUpdatesTo(this)
    }
}