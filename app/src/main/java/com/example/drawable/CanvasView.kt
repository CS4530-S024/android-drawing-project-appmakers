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


/**
 *
 */
data class PaintedPath(val path: Path, val paint: Int)

class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val medWidth: Float = 10F
    private var currentBrush = Paint()
    private var pathMap: HashMap<Int, Path> = HashMap()
    private var previousMap: HashMap<Int, Path> = HashMap()
    private var paintScreen: Paint = Paint()
    private var paintbrush = Paint()

    private var currColor: Int = Color.BLACK
    private var currPenSize: Float = medWidth;
    private var currPenShape: Paint.Cap = Paint.Cap.ROUND

    private var pathList = ArrayList<PaintedPath>()
    private lateinit var bitmap: Bitmap
    private lateinit var canvas: Canvas
    private var width = 8F
    var path = Path()
    var lParams: ViewGroup.LayoutParams? = null

    init {
        lParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        initBrushes()
//        initCanvas()
    }

    private fun initBrushes() {
        paintbrush.isAntiAlias = true
        paintbrush.color = currColor
        paintbrush.strokeWidth = currPenSize
        paintbrush.style = Paint.Style.STROKE
        paintbrush.strokeJoin = Paint.Join.ROUND
        paintbrush.strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0f, 0f, paintScreen)
        invalidate()
        for (paintedPath in pathList) {
            paintbrush.color = paintedPath.paint
            canvas.drawPath(paintedPath.path, paintbrush)
            invalidate()
        }
        paintbrush.strokeWidth = currPenSize
        paintbrush.color = currColor
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
                pathList.add(PaintedPath(path, paintbrush.color));
                return true
            }
        }
        postInvalidate()
        return false
    }

    //todo(set bitmap function)
    fun setColor(color: Int) {
        currentBrush.color = color
        currColor = currentBrush.color
        path = Path()
    }

    fun setPenSize(penSize: Float) {
        currentBrush.strokeWidth = penSize
        currPenSize = currentBrush.strokeWidth
        path = Path()
    }

    fun setPenShape(penShape: Paint.Cap) {
        currentBrush.strokeCap = penShape
        currPenShape = currentBrush.strokeCap
        path = Path()
    }

    //todo(set color function)
    //todo(set current brush function)
}