package com.example.drawable

import android.graphics.Bitmap
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.Exception
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.BitmapFactory
import android.graphics.DiscretePathEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DrawingRepository(private val scope: CoroutineScope, private val dao: DrawingDAO, private val context: Context) {

    //updated when the DB is modified
    val paths : Flow<List<DrawingPath>> = dao.getAllPaths()
    val drawings = paths.map { it.map { drawingPath ->
        return@map loadDrawing(drawingPath.filePath)
        }
    }
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    //should be strings for file paths to internal storage
    suspend fun saveDrawing(drawing: Drawing) {
        val (filePath, date) = saveBitmapToFile(drawing.bitmap, drawing.name)
        val imageEntity = DrawingPath(filePath = filePath, modDate = date)
        scope.launch {
            dao.insertImage(imageEntity)
        }
    }

    fun loadDrawing(filename: String): Drawing {
        val file = File(context.filesDir, filename)
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        return Drawing(filename, bitmap, dateFormat.format(file.lastModified()))
    }

    private fun saveBitmapToFile(bmp: Bitmap, filename: String): Pair<String, Long>{
        val file = File(context.filesDir, filename)
        return try {
            context.openFileOutput(file.name, Context.MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
                    throw IOException("Couldn't save bitmap to file.")
                }
            }
            Pair(file.absolutePath, file.lastModified())
        } catch (e: IOException) {
            e.printStackTrace()
            throw e // Re-throw the exception to be handled by the caller or return Result.failure(e)
        }
    }

    fun deleteDrawing(filename: String): Boolean {
        return try {
            context.deleteFile(filename)

        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
}