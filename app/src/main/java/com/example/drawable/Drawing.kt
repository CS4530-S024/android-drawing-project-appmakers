package com.example.drawable

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

// TODO: Fix the code below to work with the database
//@Entity(tableName="funFact")
//data class FunFact(var text: String, var source_url: String){
//    @PrimaryKey(autoGenerate = true)
//    var id: Int = 0 //
//}