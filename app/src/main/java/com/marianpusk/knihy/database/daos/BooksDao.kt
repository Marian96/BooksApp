package com.marianpusk.carapplicaiton.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.database.entities.Category

@Dao
interface BooksDao {
    @Insert
    fun insertBook(book: BookEntity)

    @Update
    fun updateBook(book: BookEntity)

    @Insert
    fun insertCategory(category: Category)

    @Update
    fun updateCategory(category: Category)


    @Query("DELETE FROM BookEntity")
    fun clearBook()

    @Query("SELECT * FROM BookEntity ORDER BY titleNormalized ASC")
    fun getAllBooks(): LiveData<List<BookEntity>>

    @Query("SELECT * FROM Category")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM BookEntity WHERE id = :key")
    fun getBookById(key: Int): BookEntity

    @Query("SELECT ca.name FROM Category as ca LEFT JOIN BookEntity as be ON be.id_category = ca.id WHERE be.id = :key")
    fun getCategory(key: Int): String

    @Query("SELECT id FROM CATEGORY WHERE name = :name")
    fun getCategoryIdByName(name:String): Int

    @Query("DELETE FROM BookEntity WHERE id = :key")
    fun deleteBookById(key: Int)

    @Query("UPDATE BookEntity SET note = :text WHERE id = :key")
    fun updatenote(text: String, key:Int)


//    @Query("DELETE FROM GeneratedCodeEntity WHERE id = :key")
//    fun deleteCode(key: Int)
}