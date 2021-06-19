package com.marianpusk.knihy.ui.book

import android.graphics.Bitmap
import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marianpusk.knihy.database.entities.ImageEntity
import com.marianpusk.knihy.databinding.BookImageBinding

class ImageRecycleAdapter (val clickListener: BookImageListener) : ListAdapter<ImageEntity, ImageRecycleAdapter.CodeViewHolder>(TrainingPlandDiffCallback()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodeViewHolder {
        return CodeViewHolder.from(parent)
    }

//    override fun getItemCount(): Int {
//        return items.size
//    }


//    fun submitList(planList:List<TrainingPlanEntity>) {
//        items = planList
//    }

    override fun onBindViewHolder(holder: CodeViewHolder, position: Int) {
        when(holder) {
            is CodeViewHolder -> {

                holder.bind(getItem(position),clickListener)
            }
        }

    }


    class CodeViewHolder(
        val binding: BookImageBinding
    ): RecyclerView.ViewHolder(binding.root) {


        fun bind(
            item: ImageEntity,
            clickListener: BookImageListener
        ) {
            binding.clickListener = clickListener
            binding.image = item
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): CodeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BookImageBinding.inflate(layoutInflater,parent,false)
                return CodeViewHolder(binding)
            }
        }
    }

}

class TrainingPlandDiffCallback : DiffUtil.ItemCallback<ImageEntity>() {
    override fun areItemsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean {
        return oldItem == newItem
    }
}

class BookImageListener(val clickListener: (value: Bitmap?, id: Int) -> Unit){

    fun onClick(image: ImageEntity) = clickListener(image.image, image.id)
}




