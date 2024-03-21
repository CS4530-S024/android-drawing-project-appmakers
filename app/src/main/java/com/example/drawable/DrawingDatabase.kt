package com.example.drawable

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Database(entities= [DrawingPath::class], version = 3, exportSchema = false)

abstract class  DrawingDatabase  : RoomDatabase(){
    abstract fun drawingDao(): DrawingDAO
}

@Dao
interface DrawingDAO {
    //Gets a list of drawing paths from the repo
    @Query("SELECT * FROM drawingpaths ORDER BY modDate DESC")
    fun getAllPaths(): Flow<List<DrawingPath>>

    //marked as suspend so the thread can yield in case the DB update is slow
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(path: DrawingPath)

    @Delete
    suspend fun deleteDrawing(path: DrawingPath)

    @Query("SELECT COUNT(*) FROM drawingpaths")
    fun getDrawingCount():  Flow<Int>

}