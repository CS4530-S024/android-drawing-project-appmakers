package com.example.drawable

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
import androidx.constraintlayout.widget.ConstraintSet.Motion
import com.example.drawable.MainActivity.Companion.paintbrush
import com.example.drawable.MainActivity.Companion.path

/**
 *
 */
class CanvasView : View {

    var lParams: ViewGroup.LayoutParams? = null

    var paintScreen: Paint = Paint()

    companion object {
        var pathList = ArrayList<Path>()
        var currentBrush = Paint(Color.BLACK)
        var bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
    }

    constructor(context: Context) : this(context, null) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttribute: Int) : super(
        context,
        attrs,
        defStyleAttribute
    ) {
        init()
    }

    // val - constant
    // var - can change

    private var pathMap: HashMap<Int, Path> = HashMap<Int, Path>()
    private var previousMap: HashMap<Int, Path> = HashMap<Int, Path>()


    /**
     *  Initializes the variables and set
     */
    private fun init() {
        //lParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
//        bitmap = Bitmap.createBitmap(lParams!!.width, lParams!!.height, Bitmap.Config.ARGB_8888)
//        canvas = Canvas(bitmap)

        // Initializing the initial brush size
        paintbrush.isAntiAlias = true
        paintbrush.color = currentBrush.color
        paintbrush.strokeWidth = 8F
        paintbrush.strokeCap = Paint.Cap.ROUND
        paintbrush.style = Paint.Style.STROKE
        paintbrush.strokeJoin = Paint.Join.ROUND


    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0f, 0f, paintScreen)
        for (i in pathList.indices) {
            canvas.drawPath(pathList[i], paintbrush);
            invalidate()
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
        bitmap.eraseColor(Color.WHITE)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                pathList.add(path);

                return true
            }

        }
        postInvalidate()
        return false
    }
}