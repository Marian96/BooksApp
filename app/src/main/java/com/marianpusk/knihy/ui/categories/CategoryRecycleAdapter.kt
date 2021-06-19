package com.marianpusk.knihy.ui.categories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.database.entities.Category
import com.marianpusk.knihy.databinding.CategoriesBinding


class CategoryRecycleAdapter(val clickListener: CategoryListener) : ListAdapter<Category, CategoryRecycleAdapter.CategoryViewHolder>(CategorydDiffCallback()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        when(holder) {
            is CategoryViewHolder -> {

                holder.bind(getItem(position),clickListener)
            }
        }

    }


    class CategoryViewHolder(
        val binding: CategoriesBinding
    ): RecyclerView.ViewHolder(binding.root) {


        fun bind(
            item: Category,
            clickListener: CategoryListener

        ) {
            binding.cat = item
            binding.clickListener = clickListener
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): CategoryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CategoriesBinding.inflate(layoutInflater,parent,false)
                return CategoryViewHolder(binding)
            }
        }
    }



}

class CategorydDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}
class CategoryListener(val clickListener: ( id: Int) -> Unit){

    fun onClick(category: Category) = clickListener( category.id)
}






