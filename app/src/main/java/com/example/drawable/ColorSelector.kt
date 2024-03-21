//package com.example.drawable
//
//import android.graphics.Bitmap
//import android.graphics.RectF
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.unit.dp
//import androidx.fragment.app.Fragment
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.Path
//import android.graphics.drawable.ColorDrawable
//import android.icu.text.SimpleDateFormat
//import android.os.Bundle
//import android.view.GestureDetector
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewGroup
//import android.view.Window
//import android.view.inputmethod.EditorInfo
//import android.view.inputmethod.InputMethodManager
//import android.widget.ImageButton
//import androidx.compose.foundation.interaction.InteractionSource
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.interaction.PressInteraction
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.fragment.app.activityViewModels
//import androidx.navigation.fragment.findNavController
//import com.example.drawable.databinding.FragmentDrawingCanvasBinding
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import yuku.ambilwarna.AmbilWarnaDialog
//import java.util.Date
//import java.util.LinkedList
//import kotlin.math.cos
//import kotlin.math.sin
//import kotlin.random.Random
//
//class ColorSelector : Fragment() {
//
//
//    @Composable
//    fun HueSelector(
//        setColor: (Float) -> Unit
//    ){
//        val scope = rememberCoroutineScope()
//        val interactionSource = remember {
//            MutableInteractionSource()
//        }
//        val pressOffset = remember {
//            mutableStateOf(Offset.Zero)
//        }
//        Canvas(
//            modifier = Modifier
//            .height(40.dp)
//            .width(300.dp)
//            .clip(RoundedCornerShape(50)),
//            contentDescription = "Hue Selector")
//            {
//                val drawScopeSize = size
//                val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
//                val hueCanvas = Canvas(bitmap)
//                val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
//                val hueColors = IntArray((huePanel.width()).toInt())
//                var hue = 0f
//                for (i in hueColors.indices) {
//                    hueColors[i] = AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))
//                    hue += 360f / hueColors.size
//                }
//                val linePaint = Paint()
//                linePaint.strokeWidth = 0F
//                for (i in hueColors.indices) {
//                    linePaint.color = hueColors[i]
//                    hueCanvas.drawLine(i.toFloat(), 0F, i.toFloat(), huePanel.bottom, linePaint)
//                }
//
//                drawBitmap(
//                    bitmap = bitmap,
//                    panel = huePanel
//                )
//
//                fun pointToHue(pointX: Float): Float {
//                    val width = huePanel.width()
//                    val x = when {
//                        pointX < huePanel.left -> 0F
//                        pointX > huePanel.right -> width
//                        else -> pointX - huePanel.left
//                    }
//                    return x * 360f / width
//                }
//
//                scope.collectPressGesture(interactionSource) { pressPosition ->
//                    val pressPos = pressPosition.x.coerceIn(0f..drawScopeSize.width)
//                    pressOffset.value = Offset(pressPos, 0f)
//                    val selectedHue = pointToHue(pressPos)
//                    setColor(selectedHue)
//                }
//
//                drawCircle(
//                    Color.White,
//                    radius = size.height/2,
//                    center = Offset(pressOffset.value.x, size.height/2),
//                    style = Stroke(
//                        width = 2.dp.toPx()
//                    )
//                )
//            }
//    }
//
//    fun CoroutineScope.collectForPress(
//        interactionSource: InteractionSource,
//        setOffset: (Offset) -> Unit
//    ) {
//        launch {
//            interactionSource.interactions.collect { interaction ->
//                (interaction as? PressInteraction.Press)
//                    ?.pressPosition
//                    ?.let(setOffset)
//            }
//        }
//    }
//
//    private fun drawBitmap(bitmap: Bitmap, panel: RectF) {
//
//    }
//
//}