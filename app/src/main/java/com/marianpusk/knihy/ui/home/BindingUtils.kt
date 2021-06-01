package com.marianpusk.knihy.ui.home

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.carapplicaiton.database.daos.BooksDao
import com.marianpusk.knihy.R
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.database.entities.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("image")
fun ImageView.setImage(item: BookEntity?){
    item?.let {
        it.image?.let {
            setImageBitmap(item.image)
        }
    }
}

@BindingAdapter("bookName")
fun TextView.setFileName(item: BookEntity?) {
    item?.let {
        text = item.title
    }
}

@BindingAdapter("year")
fun TextView.setYear(item: BookEntity?) {
    item?.let {
        text = item.year.toString()
    }
}

@BindingAdapter("bookCategory")
fun TextView.setCategory(item: BookEntity?){
    item?.let {
        CoroutineScope(Dispatchers.IO).launch {
            val database = bookDatabase.getInstance(context)
            val category = database.books.getCategory(item.id)
            text = category
        }

    }

}

@BindingAdapter("rating")
fun RatingBar.setRating(item: BookEntity?){
    item?.let {
        rating = it.rating
    }
}

@BindingAdapter("author")
fun TextView.setAuthor(item: BookEntity?){
    item?.let {

        text = item.author
    }
}

@BindingAdapter("category")
fun TextView.setCategory(cat: Category?){
    cat?.let {
        text = cat.name
    }
}

