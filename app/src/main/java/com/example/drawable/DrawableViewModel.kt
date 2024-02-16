package com.example.drawable

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Drawing(val name: String, val bitmap: Bitmap)
class DrawableViewModel: ViewModel(){
    private val drawings = MutableLiveData<MutableList<Drawing>>()
    val drawingsList = drawings as LiveData<out List<Drawing>>
    val name = "Drawings"
    var currBitmap : Bitmap? = null
    var currColor : Int? = null

    private var currDrawing: Drawing? = null

    private fun createDirectory(){

    }

    /**
     * Adds drawing from list
     * @param drawing The drawing we are inserting into the List
     */
    fun add(drawing: Drawing){
        drawings.value?.add(0, drawing)
        drawings.value = drawings.value
    }

    /**
     * Removes drawing from list
     * @param drawing The drawing we are removing from the List
     */
    fun removeDrawing(index: Int){
        drawings.value?.removeAt(index)
        drawings.value = drawings.value
    }

    //create bitmap here
    fun newBitmap(): Bitmap {
        currBitmap = Bitmap.createBitmap(1024, 1024, Bitmap.Config.ARGB_8888)
        return currBitmap!!
    }

    fun setColor(color: Int){
        currColor = color
    }

    //remove, then puyt at top for chnaged bitmwp
}