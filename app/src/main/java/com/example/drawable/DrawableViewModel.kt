package com.example.drawable

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

external fun brightness(bmp: Bitmap?, brightness: Float)
external fun invertColors(bmp: Bitmap?)

data class Drawing(val bitmap: Bitmap, val dPath: DrawingPath)
class DrawableViewModel(private val repository: DrawingRepository) : ViewModel() {
    companion object {
        init {
            System.loadLibrary("drawable")
        }
    }

    //new implementation
    val drawings: Flow<List<Drawing>> = repository.drawings
    val count: Flow<Int> = repository.count
    val usernameFlow = MutableStateFlow<String?>(null)

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
    private val saveColor = MutableLiveData<Int>(Color.BLACK)
    var currColor = saveColor as LiveData<out Int>

    /**
     * Adds drawing to list
     * @param drawing: The drawing we are inserting into the List
     */
    fun add(drawing: Drawing) {
        viewModelScope.launch {
            repository.saveDrawing(drawing)
        }
    }

    /**
     * Removes drawing from list
     * @param dpath: The drawing path of the file to delete
     */
    fun removeDrawing(dpath: DrawingPath) {
        viewModelScope.launch {
            repository.deleteDrawing(dpath)
        }
    }

    /**
     *
     */
    suspend fun checkForDrawing(name: String): Boolean {
        return repository.nameCheck(name)
    }

    /**
     * Updates the color when its changed
     * @param color: the new color
     */
    fun updateColor(color: Int) {
        saveColor.value = color
        saveColor.value = saveColor.value
    }

    /**
     * Updates the current bitmap when changes happen
     * @param bitmap: the changed bitmap
     */
    fun updateBitmap(bitmap: Bitmap) {
        bitmapLiveData.value = bitmap
        bitmapLiveData.value = bitmapLiveData.value
    }

    /**
     * Sets the current bitmap
     * @param dpath: The drawing path of file to set as current bitmap
     */
    fun setCurrBitmap(dpath: DrawingPath) {
        val drawing = repository.loadDrawing(dpath)
        bitmapLiveData.value = drawing.bitmap
        bitmapLiveData.value = bitmapLiveData.value
    }

    /**
     *
     */
    fun brightenImage() {
        val currentBitmap = bitmapLiveData.value
        if (currentBitmap != null) {
            brightness(currentBitmap, .25F)
            updateBitmap(currentBitmap)
        }
    }

    /**
     *
     */
    fun invertColors() {
        val currentBitmap = bitmapLiveData.value
        if (currentBitmap != null) {
            invertColors(currentBitmap)
            updateBitmap(currentBitmap)

        }
    }

    /**
     *
     */
    fun setTheUsername(name: String){
        usernameFlow.value = name
    }

    fun clear(){
        viewModelScope.launch {
            repository.clearDatabase()
        }
    }
}