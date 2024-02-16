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
     *
     */
    fun setColor(color: Int){
        saveColor.value = color
        saveColor.value = saveColor.value
    }

    /**
     *
     */
    fun updateBitmap(bitmap: Bitmap) {
        bitmapLiveData.value = bitmap
        bitmapLiveData.value = bitmapLiveData.value
    }

    /**
     *
     */
    fun setCurrBitmap(index: Int){
        currIndex = index
        bitmapLiveData.value = drawings.value!![index].bitmap
        bitmapLiveData.value = bitmapLiveData.value
    }

    /**
     *
     */
    fun fixOrder(drawing: Drawing){
        drawings.value!!.removeAt(currIndex!!)
        add(drawing)
    }

    /**
     *
     */
    fun getDrawingTitle(index: Int): String {
        return drawings.value!![index].name
    }
}