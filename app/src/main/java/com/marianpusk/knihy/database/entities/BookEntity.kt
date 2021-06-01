package com.marianpusk.knihy.database.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_category"),
        onDelete = ForeignKey.CASCADE)
))
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo
    var id_category: Int? = null,

    @ColumnInfo
    var title: String = "",

    @ColumnInfo
    var titleNormalized: String = "",

    @ColumnInfo
    var author: String = "",

    @ColumnInfo
    var year: Int = 0,

    @ColumnInfo
    var note: String = "",

    @ColumnInfo
    var rating: Float = 0F,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image: Bitmap? = null
)


