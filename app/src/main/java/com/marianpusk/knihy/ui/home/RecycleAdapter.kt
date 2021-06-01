package com.marianpusk.knihy.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.databinding.BooksBinding


class RecycleAdapter(val clickListener: BookListener
                     ) : ListAdapter<BookEntity, RecycleAdapter.fileViewHolder>(BookdDiffCallback()),Filterable{


   private var allBooks = mutableListOf<BookEntity>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): fileViewHolder {
        return fileViewHolder.from(parent)
    }



    override fun onBindViewHolder(holder: fileViewHolder, position: Int) {
        when(holder) {
            is fileViewHolder -> {

                holder.bind(getItem(position),clickListener)
            }
        }

    }

fun getBooks(list: List<BookEntity>){

allBooks.addAll(list)
}


    class fileViewHolder(
        val binding: BooksBinding
    ): RecyclerView.ViewHolder(binding.root) {


        fun bind(
            item: BookEntity,
            clickListener: BookListener

        ) {
            binding.clickListener = clickListener
            binding.book = item
            binding.executePendingBindings()


        }

        companion object {
            fun from(parent: ViewGroup): fileViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BooksBinding.inflate(layoutInflater,parent,false)
                return fileViewHolder(binding)
            }
        }
    }

    override fun getFilter(): Filter {
        return myFilter
    }

    var myFilter = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {



            val filteredList= ArrayList<BookEntity>()

            if ( p0.isNullOrEmpty()) {
                filteredList.addAll(allBooks!!)
            } else {
                for (book in allBooks!!) {
                    if (book.title.toLowerCase().contains(p0.toString().toLowerCase()) || book.author.toLowerCase().contains(p0.toString().toLowerCase())) {
                        filteredList.add(book)
                    }
                }
            }

            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults

        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            submitList(p1!!.values as List<BookEntity>)
        }

    }


}

class BookdDiffCallback : DiffUtil.ItemCallback<BookEntity>() {
    override fun areItemsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean {
        return oldItem == newItem
    }
}

class FilesDiffUtilCallback(private val oldList: List<BookEntity>, private val newList: List<BookEntity>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
}

class BookListener(val clickListener: ( id: Int) -> Unit){

    fun onClick(book: BookEntity) = clickListener( book.id)
}


