package com.example.drawable

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName="drawingpaths")
data class DrawingPath(val modDate: Long, var name: String) {
    @Transient
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0 // integer primary key for the DB
}