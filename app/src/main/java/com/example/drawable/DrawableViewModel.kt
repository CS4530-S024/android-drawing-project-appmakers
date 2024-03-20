package com.example.drawable

import android.content.Context
import android.app.Application

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

import java.io.File
import java.util.Date

data class Drawing(val bitmap: Bitmap, val dPath: DrawingPath)
class DrawableViewModel(private val repository: DrawingRepository) : ViewModel(){

    //new implementation
    val dPaths : Flow<List<DrawingPath>> = repository.paths
    val drawings: Flow<List<Drawing>> = repository.drawings

    private val bitmapLiveData = MutableLiveData<Bitmap>()
    var currBitmap = bitmapLiveData as LiveData<out Bitmap>

//    private val _bitmapFlow = MutableStateFlow<Bitmap?>(null) // Assuming bitmap can be initially null
//    val bitmapFlow: StateFlow<Bitmap?> = _bitmapFlow.asStateFlow()

    private val saveColor =  MutableLiveData<Int>()
    var currColor = saveColor as LiveData<out Int>


    /**
     * Adds drawing to list
     * @param drawing The drawing we are inserting into the List
     */
    fun add(drawing: Drawing){
        viewModelScope.launch {
            // This is now within a coroutine scope, and you can call suspend functions
            repository.saveDrawing(drawing)
        }
    }

    /**
     * Updates the color when its changed
     * @param color the new color
     */
    fun updateColor(color: Int){
        saveColor.value = color
        saveColor.value = saveColor.value
    }

    /**
     * Updates the current bitmap when changes happen
     * @param bitmap the changed bitmap
     */
    fun updateBitmap(bitmap: Bitmap) {
        bitmapLiveData.value = bitmap
        bitmapLiveData.value = bitmapLiveData.value
    }

    // index dependent things
    /**
     * Sets the current bitmap
     * @param filename The name of the file to set as current bitmap
     */
  fun setCurrBitmap(filename: String){
        val drawing = repository.loadDrawing(filename)
        bitmapLiveData.value = drawing.bitmap
        bitmapLiveData.value = bitmapLiveData.value
    }

    /**
     * Removes drawing from list
     * @param filename The name of the file to delete
     */
    fun removeDrawing(filename: String){
        repository.deleteDrawing(filename)
    }

    /**
     * Gets the title of the drawing
     * @param filename The name of the file to get title of
     */
    fun getDrawingTitle(filename: String): String {
        val drawing = repository.loadDrawing(filename)
        return drawing.dPath.filePath
    }

    fun onDrawingClicked(fileName: String) {

    }
}

class Factory(private val repository: DrawingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrawableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DrawableViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}