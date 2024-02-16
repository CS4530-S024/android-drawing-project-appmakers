package com.example.drawable

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.Canvas
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Path
import android.graphics.Paint
import android.view.MotionEvent
import android.view.ViewGroup
import kotlin.math.min



class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private var myBitmap : Bitmap? = null
    var lParams: ViewGroup.LayoutParams? = null

    init {
        lParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    fun setBitmap(bitmap: Bitmap) {
        myBitmap = bitmap
        invalidate() // Trigger redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        myBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }

}