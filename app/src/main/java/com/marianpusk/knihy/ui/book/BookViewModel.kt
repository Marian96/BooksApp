package com.marianpusk.knihy.ui.book

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.marianpusk.carapplicaiton.database.daos.BooksDao
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.database.entities.Category
import kotlinx.coroutines.*

class BookViewModel(
    val database: BooksDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _cameraImage = MutableLiveData<Bitmap>()
    private var _categoryId = MutableLiveData<Int>()

    val cameraImage: LiveData<Bitmap>
    get() = _cameraImage

    val categoryId: LiveData<Int>
    get() = _categoryId

    val categories = database.getAllCategories()



    private suspend fun insert(bookEntity: BookEntity) {
        withContext(Dispatchers.IO) {
            database.insertBook(bookEntity)
        }
    }

    private suspend fun update(bookEntity: BookEntity){
        withContext(Dispatchers.IO){
            database.updateBook(bookEntity)
        }
    }

    private suspend fun getCategoryId(name: String){
        withContext(Dispatchers.IO){
            _categoryId.postValue(database.getCategoryIdByName(name))
        }
    }

    private suspend fun clear(){
        withContext(Dispatchers.IO){
            database.clearBook()
        }
    }

    fun getImage(img: Bitmap){
        _cameraImage.postValue(img)
    }

    fun insertBook(book: BookEntity){
        uiScope.launch {
            insert(book)
            update(book)
        }
    }

    fun getCategoryIdByName(name: String){
        uiScope.launch {
            getCategoryId(name)
        }
    }

    fun deleteBooks(){
        uiScope.launch {
            clear()
        }
    }

    override fun onCleared() {

        super.onCleared()
        viewModelJob.cancel()
    }

}