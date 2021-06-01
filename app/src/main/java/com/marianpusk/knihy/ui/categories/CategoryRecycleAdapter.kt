package com.marianpusk.knihy.ui.categories

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.database.entities.Category
import com.marianpusk.knihy.databinding.BooksBinding
import com.marianpusk.knihy.databinding.CategoriesBinding


class CategoryRecycleAdapter : ListAdapter<Category, CategoryRecycleAdapter.fileViewHolder>(CategorydDiffCallback()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): fileViewHolder {
        return fileViewHolder.from(parent)
    }

//    override fun getItemCount(): Int {
//        return items.size
//    }


//    fun submitList(planList:List<TrainingPlanEntity>) {
//        items = planList
//    }

    override fun onBindViewHolder(holder: fileViewHolder, position: Int) {
        when(holder) {
            is fileViewHolder -> {

                holder.bind(getItem(position))
            }
        }

    }


    class fileViewHolder(
        val binding: CategoriesBinding
    ): RecyclerView.ViewHolder(binding.root) {


        fun bind(
            item: Category

        ) {
            binding.cat = item
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): fileViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CategoriesBinding.inflate(layoutInflater,parent,false)
                return fileViewHolder(binding)
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






