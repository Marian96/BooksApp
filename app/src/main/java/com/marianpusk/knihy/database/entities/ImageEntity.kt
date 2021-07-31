package com.marianpusk.knihy.database.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = arrayOf(
    ForeignKey(entity = BookEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_book"),
        onDelete = ForeignKey.CASCADE)
))
data class ImageEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo
    var id_book: Int? = null,

    @ColumnInfo
    var imageURI: String? = null
)
