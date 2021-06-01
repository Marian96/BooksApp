package com.marianpusk.knihy.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Category (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var name: String = ""
)