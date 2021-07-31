package com.marianpusk.carapplicaiton.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.marianpusk.knihy.database.entities.BookEntity
import com.marianpusk.knihy.database.entities.Category
import com.marianpusk.knihy.database.entities.ImageEntity

@Dao
interface BooksDao {
    @Insert
    fun insertBook(book: BookEntity)

    @Insert
    fun insertImage(image: ImageEntity)

    @Update
    fun updateImage(image: ImageEntity)

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

    @Query("SELECT * FROM Category ORDER BY name ASC")
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

    @Query("UPDATE BookEntity SET note = :text, author = :aut,id_category = :cat,rating = :rat,year = :year  WHERE id = :key")
    fun updateBookEntity(key:Int,text: String,aut:String,cat:Int?,rat:Float,year:Int )

    @Query("SELECT * FROM ImageEntity")
    fun getAllImages(): LiveData<List<ImageEntity>>

    @Query("SELECT * FROM ImageEntity WHERE id = :key")
    fun getImageById(key: Int): ImageEntity

    @Query("DELETE FROM ImageEntity WHERE id = :key")
    fun deleteImageById(key:Int)

    @Query("DELETE FROM Category WHERE id = :key")
    fun deleteCategoryById(key:Int)

    @Query("SELECT * FROM ImageEntity WHERE id_book = :key")
    fun getImagesByBookId(key: Int): List<ImageEntity>
//    @Query("DELETE FROM GeneratedCodeEntity WHERE id = :key")
//    fun deleteCode(key: Int)
}