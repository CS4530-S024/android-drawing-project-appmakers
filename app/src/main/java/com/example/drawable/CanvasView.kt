package com.example.drawable

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.Canvas
import android.graphics.Bitmap
import android.view.ViewGroup


class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var myBitmap : Bitmap? = null
    private var lParams: ViewGroup.LayoutParams? = null

    init {
        lParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    /**
     * Sets the new bitmap, and triggers a redraw
     */
    fun setBitmap(bitmap: Bitmap) {
        myBitmap = bitmap
        invalidate() // Trigger redraw
    }

    /**
     * Redraws the bitmap when invalidate is called
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        myBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }
}