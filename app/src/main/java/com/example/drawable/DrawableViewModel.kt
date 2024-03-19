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

data class Drawing(val name: String, val bitmap: Bitmap, val date: String)
class DrawableViewModel(private val repository: DrawingRepository) : ViewModel(){

    private val drawings = MutableLiveData<MutableList<Drawing>>()
    val drawingsList = drawings as LiveData<out List<Drawing>>


    //new implementation
    val dPaths : Flow<List<DrawingPath>> = repository.paths
    val drawingss: Flow<List<Drawing>> = repository.drawings



    class Factory(private val repository: DrawingRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DrawableViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DrawableViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    ///


    private val bitmapLiveData = MutableLiveData<Bitmap>()
    var currBitmap = bitmapLiveData as LiveData<out Bitmap>
    private val saveColor =  MutableLiveData<Int>()
    var currColor = saveColor as LiveData<out Int>
    private var currIndex : Int? = null


    /**
     * Adds drawing to list
     * @param drawing The drawing we are inserting into the List
     */
    fun add(drawing: Drawing){
        val currentList = drawings.value?.toMutableList() ?: mutableListOf()
        currentList.add(0, drawing)
        currIndex = 0
        drawings.value = currentList
        drawings.value = drawings.value
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
     * Fixes the ordering of the drawings by putting the new drawing at the top
     * @param drawing the changed drawing to put at the top
     */
    fun fixOrder(drawing: Drawing){
        drawings.value!!.removeAt(currIndex!!)
        add(drawing)
    }

    // index dependent things
    /**
     * Sets the current bitmap
     * @param index the position of the drawing with the bitmap
     */
    fun setCurrBitmap(index: Int){
        currIndex = index
        bitmapLiveData.value = drawings.value!![index].bitmap
        bitmapLiveData.value = bitmapLiveData.value
    }

    /**
     * Removes drawing from list
     * @param index The index of the drawing we are removing from the List
     */
    fun removeDrawing(index: Int){
        drawings.value!!.removeAt(index)
        drawings.value = drawings.value
    }

    /**
     * Gets the title of the drawing
     * @param index the position of the drawing
     */
    fun getDrawingTitle(index: Int): String {
        return drawings.value!![index].name
    }

    fun onDrawingClicked(fileName: String) {

    }


}