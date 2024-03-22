package com.example.drawable

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.graphics.Color
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

data class Drawing(val bitmap: Bitmap, val dPath: DrawingPath)
class DrawableViewModel(private val repository: DrawingRepository) : ViewModel(){

    //new implementation
    val drawings: Flow<List<Drawing>> = repository.drawings
    val count: Flow<Int> = repository.count
    class Factory(private val repository: DrawingRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DrawableViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DrawableViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val bitmapLiveData = MutableLiveData<Bitmap>()
    var currBitmap = bitmapLiveData as LiveData<out Bitmap>

//    private val _bitmapFlow = MutableStateFlow<Bitmap?>(null) // Assuming bitmap can be initially null
//    val bitmapFlow: StateFlow<Bitmap?> = _bitmapFlow.asStateFlow()

    private val saveColor =  MutableLiveData<Int>(Color.BLACK)
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

    /**
     * Sets the current bitmap
     * @param dpath The drawing path of file to set as current bitmap
     */
  fun setCurrBitmap(dpath: DrawingPath){
        val drawing = repository.loadDrawing(dpath)
        bitmapLiveData.value = drawing.bitmap
        bitmapLiveData.value = bitmapLiveData.value
    }

    /**
     * Removes drawing from list
     * @param dpath The drawing path of the file to delete
     */
    fun removeDrawing(dpath: DrawingPath): Boolean {
        return repository.deleteDrawing(dpath)
    }
}