package com.marianpusk.knihy.ui.home

import android.app.Application
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.marianpusk.carapplicaiton.database.daos.BooksDao
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.database.entities.ImageEntity
import kotlinx.coroutines.*

class HomeViewModel(
    val database: BooksDao,
    application: Application
) : AndroidViewModel(application) {


    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val books = database.getAllBooks()
    val categories = database.getAllCategories()


    private var _book = MutableLiveData<BookEntity>()

    private var _category = MutableLiveData<String>()

    private var _bookImage = MutableLiveData<ImageEntity>()

    private var _categoryId = MutableLiveData<Int>()

    private var _images = MutableLiveData<List<ImageEntity>>()

    private var _recyclerViewVisible = MutableLiveData<Boolean>()


    val book: LiveData<BookEntity>
    get() = _book

    val category: LiveData<String>
    get() = _category

    val bookImage: LiveData<ImageEntity>
    get() = _bookImage

    val categoryId: LiveData<Int>
    get() = _categoryId

    val images: LiveData<List<ImageEntity>>
    get() = _images

    val recyclerViewVisible: LiveData<Boolean>
    get() = _recyclerViewVisible



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

    private suspend fun insert(imageEntity: ImageEntity) {
        withContext(Dispatchers.IO) {
            database.insertImage(imageEntity)
        }
    }

    private suspend fun update(imageEntity: ImageEntity){
        withContext(Dispatchers.IO){
            database.updateImage(imageEntity)
        }
    }

    private suspend fun getImageById(id: Int){
        withContext(Dispatchers.IO){
           _bookImage.postValue(database.getImageById(id))
        }
    }

    private suspend fun getImagesByBook(id: Int){
        withContext(Dispatchers.IO){
            _images.postValue(database.getImagesByBookId(id))
        }
    }



    private suspend fun getCategory(id: Int){
        withContext(Dispatchers.IO){
            _category.postValue(database.getCategory(id))
        }
    }

    private suspend fun updateBookNow(key:Int,text: String,aut:String,cat:Int?,rat:Float,year:Int){
        withContext(Dispatchers.IO){
            database.updateBookEntity(key,text,aut,cat,rat,year)
        }
    }

    private suspend fun deleteImage(id:Int){
        withContext(Dispatchers.IO){
            database.deleteImageById(id)
        }
    }

    private suspend fun getCategoryId(name: String){
        withContext(Dispatchers.IO){
            _categoryId.postValue(database.getCategoryIdByName(name))
        }
    }

    fun insertImage(imageEntity: ImageEntity){
        uiScope.launch {
            insert(imageEntity)
            update(imageEntity)
        }
    }

    fun deleteBookById(book: BookEntity){
        uiScope.launch {
            deleteBook(book)
        }
    }

    fun deleteImageById(id:Int){
        uiScope.launch {
            deleteImage(id)
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

    fun updateBook(key:Int,text: String,aut:String,cat:Int?,rat:Float,year:Int){
        uiScope.launch {
            updateBookNow(key,text,aut,cat,rat,year)
        }
    }

    fun getCategoryIdByName(name: String){
        uiScope.launch {
            getCategoryId(name)
        }
    }

    fun getImage(id: Int){
        uiScope.launch {
            getImageById(id)
        }
    }

    fun getImages(bookId: Int){
        uiScope.launch {
            getImagesByBook(bookId)
        }
    }

    fun setVisibility(bool: Boolean){
        _recyclerViewVisible.postValue(bool)
    }

    override fun onCleared() {

        super.onCleared()
        viewModelJob.cancel()
    }

}