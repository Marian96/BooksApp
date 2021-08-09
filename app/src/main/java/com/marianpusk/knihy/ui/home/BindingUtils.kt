package com.marianpusk.knihy.ui.home

import android.annotation.SuppressLint
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.BindingAdapter
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.carapplicaiton.database.daos.BooksDao
import com.marianpusk.knihy.App
import com.marianpusk.knihy.R
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.database.entities.Category
import com.marianpusk.knihy.database.entities.ImageEntity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
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

@BindingAdapter("bookImage")
fun ImageView.setImage(item: ImageEntity?){
    item?.let {
        it.imageURI?.let {
            val imageUri = Uri.parse(it)

            Picasso.get()
                .load(imageUri)
                .resize(150,150)
                .into(this)

        }
    }
}

@BindingAdapter("bookName")
fun TextView.setFileName(item: BookEntity?) {
    item?.let {
        var bookName = item.title
        if(bookName.length > 25) {
            var stringLIst = bookName.chunked(25)
            bookName = ""
            for (item in stringLIst){
                if (item != stringLIst.last()){
                    bookName += item + "\n"
                }
                else{
                    bookName += item
                }

            }
        }
        text = bookName
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
            withContext(Dispatchers.Main){
                text = category

            }
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
        text = it.name
    }
}


