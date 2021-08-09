package com.marianpusk.knihy.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun fromBtMap(bitmap: Bitmap?): ByteArray{
        val outputStream = ByteArrayOutputStream()
        bitmap?.let {
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        }

        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitMap(byteArray: ByteArray?): Bitmap?{

         return BitmapFactory.decodeByteArray(byteArray,0,byteArray!!.size)


    }

}