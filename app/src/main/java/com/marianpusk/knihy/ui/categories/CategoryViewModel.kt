package com.marianpusk.knihy.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.marianpusk.carapplicaiton.database.bookDatabase
import com.marianpusk.carapplicaiton.database.daos.BooksDao
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.database.entities.Category
import kotlinx.coroutines.*

class CategoryViewModel(
    val database: BooksDao,
    application: Application
) : AndroidViewModel(application){

    private var viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val categories = database.getAllCategories()

    private suspend fun insert(category: Category) {
        withContext(Dispatchers.IO) {
            database.insertCategory(category)
        }
    }

    private suspend fun update(category: Category){
        withContext(Dispatchers.IO){
            database.updateCategory(category)
        }
    }

    fun insertCategory(category: Category){
        uiScope.launch {
            insert(category)
            update(category)
        }
    }

    override fun onCleared() {

        super.onCleared()
        viewModelJob.cancel()
    }
}