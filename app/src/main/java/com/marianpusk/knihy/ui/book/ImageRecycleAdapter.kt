package com.marianpusk.knihy.ui.book

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.marianpusk.knihy.database.entities.ImageEntity
import com.marianpusk.knihy.databinding.AddPhotoItemBinding
import com.marianpusk.knihy.databinding.BookImageBinding


class ImageRecycleAdapter (val imageClickListener: BookImageListener, val plusClickListener: PlusImageListener) : ListAdapter<ImageEntity, ImageRecycleAdapter.CodeViewHolder>(TrainingPlandDiffCallback()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodeViewHolder {
        return CodeViewHolder.from(parent,viewType)
    }

    override fun onBindViewHolder(holder: CodeViewHolder, position: Int) {
        when(holder) {
            is CodeViewHolder -> {

                if (position == 0){
                    holder.bind(null,plusClickListener)
                }else{
                    holder.bind(getItem(position - 1),imageClickListener)

                }
            }
        }

    }


    override fun getItemCount(): Int {
        return currentList.count() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0)
            TYPE_ADD
        else
            TYPE_PHOTO
    }

    companion object {
        const val TYPE_ADD = 0
        const val TYPE_PHOTO = 1
    }


    class CodeViewHolder(
        var binding: ViewBinding
    ): RecyclerView.ViewHolder(binding.root) {


        fun bind(
            item: ImageEntity?,
            clickListener: Any
        ) {


            if(binding is BookImageBinding && clickListener is BookImageListener) {
                (binding as BookImageBinding).clickListener = clickListener
                (binding as BookImageBinding).image = item
                (binding as BookImageBinding).executePendingBindings()
            }
            else if (binding is AddPhotoItemBinding && clickListener is PlusImageListener){
                (binding as AddPhotoItemBinding).clickListener = clickListener
            }


        }

        companion object {
            fun from(parent: ViewGroup,viewType: Int): CodeViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                var binding:ViewBinding = if(viewType == TYPE_ADD){
                    AddPhotoItemBinding.inflate(layoutInflater,parent,false)
                } else{
                    BookImageBinding.inflate(layoutInflater,parent,false)

                }

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

class BookImageListener(val clickListener: (value: String?, id: Int) -> Unit){

    fun onClick(image: ImageEntity) = clickListener(image.imageURI, image.id)
}

class PlusImageListener(val clickListener: () -> Unit){
    fun onClick() = clickListener()
}




