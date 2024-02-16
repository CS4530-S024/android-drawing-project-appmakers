package com.example.drawable
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.drawable.databinding.FragmentDrawingCanvasBinding
import yuku.ambilwarna.AmbilWarnaDialog
import kotlin.math.min

class DrawingCanvas : Fragment() {
    private val thinWidth: Float = 2F
    private val medWidth: Float = 10F
    private val thickWidth: Float = 25F

    data class PaintedPath(val path: Path, val color: Int, val width: Float, val shape: Paint.Cap)
    private var _binding: FragmentDrawingCanvasBinding? = null
    private val binding by lazy { _binding!! }
    private var currPenSize: Float = medWidth
    private var currPenShape: Paint.Cap = Paint.Cap.ROUND

    private var currColor: Int = Color.BLACK
    private var title: String? = null
    private var canvasView: CanvasView? = null
    private lateinit var gestureDetector: GestureDetector
    private  val myViewModel : DrawableViewModel by activityViewModels()

    //bitmap drawing vars
    private var myBitmap : Bitmap? = null
    private var offsetX: Float? = null
    private var offsetY: Float? = null
//    private var currentBrush = Paint()
    private var paintbrush = Paint()
    private var pathList = ArrayList<PaintedPath>()
    var bitmapWidth: Int? = null
    var bitmapHeight: Int? = null
    var scaleFactor: Float? = null
    var viewWidth: Float? = null
    var viewHeight: Float? = null
    private var width = 8F
    private var currentPath = Path()


    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        _binding = FragmentDrawingCanvasBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     *
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // displays color picker
        binding.pallete.setOnClickListener {
            loadColorPicker()
        }
        title = requireArguments().getString("title")
        binding.Title.setText(title)

        // displays pen size / shape popup
        binding.paintBrush.setOnClickListener {
            showPopUp()
        }
        canvasView = binding.canvas
        //Creates a new bitmap, saves it
        myBitmap = myViewModel.newBitmap()
        binding.canvas.setBitmap(myBitmap!!)
        bitmapHeight = myBitmap!!.height
        bitmapWidth = myBitmap!!.width

        //Handles double tap on title
        gestureDetector = GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                binding.Title.isFocusableInTouchMode = true
                binding.Title.requestFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.Title, InputMethodManager.SHOW_IMPLICIT)
                return true
            }
        })
        binding.Title.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        //Handles when user is done editing title
        binding.Title.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                binding.Title.isFocusable = false
                true
            } else {
                false
            }
        }

        //Moves back to list fragment and saves drawing
        binding.backButton.setOnClickListener { onBackClicked()  }
        initBrush()
        initVars()
        canvasView!!.setOnTouchListener{_, event-> onCanvasTouch(event)}
    }

    private fun initBrush() {
        paintbrush.isAntiAlias = true
        paintbrush.color = currColor
        paintbrush.strokeWidth = width
        paintbrush.style = Paint.Style.STROKE
        paintbrush.strokeJoin = Paint.Join.ROUND
        paintbrush.strokeCap = Paint.Cap.ROUND
    }

    private fun initVars(){
        viewWidth = canvasView!!.width.toFloat()
        viewHeight = canvasView!!.height.toFloat()
        scaleFactor = min(viewWidth!! / bitmapWidth!!, viewHeight!! / bitmapHeight!!)
        offsetX = (viewWidth!! - bitmapWidth!! * scaleFactor!!) / 2
        offsetY = (viewHeight!! - bitmapHeight!! * scaleFactor!!) / 2
    }

    fun onCanvasTouch(event: MotionEvent): Boolean {
        val (bX, bY) = translatecoords(event.x, event.y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath.moveTo(bX, bY)
//                drawOnBitmap()
            }

            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(bX, bY)
                pathList.add(PaintedPath(currentPath, paintbrush.color, paintbrush.strokeWidth, paintbrush.strokeCap))
                drawOnBitmap()
            }

            MotionEvent.ACTION_UP -> {
                pathList.add(PaintedPath(currentPath, paintbrush.color, paintbrush.strokeWidth, paintbrush.strokeCap))
                currentPath.reset()
                drawOnBitmap()

            }
        }
        return true
    }

    private fun drawOnBitmap() {
        var canvas = Canvas(myBitmap!!)
        var tempBrush = Paint()
        for (path in pathList){
            tempBrush.color = path.color
            tempBrush.strokeWidth = path.width
            tempBrush.strokeCap = path.shape
            canvas.drawPath(path.path, tempBrush)
        }
        updateCanvasView()
    }


    private fun updateCanvasView() {
        canvasView!!.setBitmap(myBitmap!!)
    }

    private fun translatecoords(touchX: Float, touchY: Float): Pair<Float, Float> {
        val bitmapX = (touchX - offsetX!!) / scaleFactor!!
        val bitmapY = (touchY - offsetY!!) / scaleFactor!!
        return Pair(bitmapX, bitmapY)
    }

    /**
     *
     */
    private fun loadColorPicker() {
        AmbilWarnaDialog(requireActivity(), currColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    currColor = color
                    myViewModel.setColor(color)
                    paintbrush.color = color
                }
            }).show()
    }

    /**
     *
     */
    private fun showPopUp() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_layout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val thinPen: ImageButton = dialog.findViewById<ImageButton>(R.id.thinPen)
        val medPen: ImageButton = dialog.findViewById<ImageButton>(R.id.medPen)
        val thickPen: ImageButton = dialog.findViewById<ImageButton>(R.id.thickPen)

        val trianglePen: ImageButton = dialog.findViewById<ImageButton>(R.id.trianglePen)
        val squarePen: ImageButton = dialog.findViewById<ImageButton>(R.id.squarePen)
        val roundPen: ImageButton = dialog.findViewById<ImageButton>(R.id.roundPen)

        // size listeners //
        thinPen.setOnClickListener {
            currPenSize = thinWidth
            binding.canvas.setPenSize(currPenSize)
            dialog.hide()
        }
        medPen.setOnClickListener {
            currPenSize = medWidth
            binding.canvas.setPenSize(currPenSize)
            dialog.hide()
        }
        thickPen.setOnClickListener {
            currPenSize = thickWidth
            binding.canvas.setPenSize(currPenSize)
            dialog.hide()
        }

        // shape listeners //
        trianglePen.setOnClickListener {
            currPenShape = Paint.Cap.BUTT
            binding.canvas.setPenShape(currPenShape)
            dialog.hide()
        }
        squarePen.setOnClickListener {
            currPenShape = Paint.Cap.SQUARE
            binding.canvas.setPenShape(currPenShape)
            dialog.hide()
        }
        roundPen.setOnClickListener {
            currPenShape = Paint.Cap.ROUND
            binding.canvas.setPenShape(currPenShape)
            dialog.hide()
        }

        dialog.show()
    }

    /**
     *
     */
    fun onBackClicked() {
        //save drawing to viewmodel
        //go back to list
        findNavController().navigate(R.id.action_drawingCanvas_to_drawingsList)
    }

    /**
     *
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}