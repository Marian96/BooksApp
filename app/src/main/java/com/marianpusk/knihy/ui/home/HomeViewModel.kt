package com.marianpusk.knihy.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.marianpusk.carapplicaiton.database.daos.BooksDao
import com.marianpusk.knihy.database.entities.BookEntity
import kotlinx.coroutines.*

class HomeViewModel(
    val database: BooksDao,
    application: Application
) : AndroidViewModel(application) {


    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val books = database.getAllBooks()

    private var _book = MutableLiveData<BookEntity>()

    private var _category = MutableLiveData<String>()

    val book: LiveData<BookEntity>
    get() = _book

    val category: LiveData<String>
    get() = _category

    private suspend fun deleteBook(item: BookEntity){
        withContext(Dispatchers.IO){
            database.deleteBookById(item.id)
        }
    }

    private suspend fun getBook(id: Int){
        withContext(Dispatchers.IO){
            _book.postValue(database.getBookById(id))
        }
    }

    private suspend fun getCategory(id: Int){
        withContext(Dispatchers.IO){
            _category.postValue(database.getCategory(id))
        }
    }

    private suspend fun updateBook(note: String,id: Int){
        withContext(Dispatchers.IO){
            database.updatenote(note,id)
        }
    }

    fun deleteBookById(book: BookEntity){
        uiScope.launch {
            deleteBook(book)
        }
    }

    fun getBookById(id: Int){
        uiScope.launch {
            getBook(id)
        }
    }

    fun getCategoryById(catId: Int){
        uiScope.launch {
            getCategory(catId)
        }
    }

    fun updateBookNotes(note: String,id:Int){
        uiScope.launch {
            updateBook(note,id)
        }
    }

    override fun onCleared() {

        super.onCleared()
        viewModelJob.cancel()
    }

}