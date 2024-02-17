package com.example.drawable

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Drawing(val name: String, val bitmap: Bitmap, val date: String)
class DrawableViewModel: ViewModel(){
    private val drawings = MutableLiveData<MutableList<Drawing>>()
    val drawingsList = drawings as LiveData<out List<Drawing>>
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
     * Removes drawing from list
     * @param index The index of the drawing we are removing from the List
     */
    fun removeDrawing(index: Int){
        drawings.value!!.removeAt(index)
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
     * Sets the current bitmap
     * @param index the position of the drawing with the bitmap
     */
    fun setCurrBitmap(index: Int){
        currIndex = index
        bitmapLiveData.value = drawings.value!![index].bitmap
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

    /**
     * Gets the title of the drawing
     * @param index the position of the drawing
     */
    fun getDrawingTitle(index: Int): String {
        return drawings.value!![index].name
    }
}