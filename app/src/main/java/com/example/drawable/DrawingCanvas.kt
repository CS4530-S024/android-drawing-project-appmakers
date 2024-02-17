package com.example.drawable
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
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
import java.util.Date

class DrawingCanvas : Fragment() {


    data class PaintedPath(val path: Path, val color: Int, val width: Float, val shape: Paint.Cap)
    private var _binding: FragmentDrawingCanvasBinding? = null
    private val binding by lazy { _binding!! }
    private var currColor: Int? = null
    private var title: String? = null
    private var state: String? = null
    private var canvasView: CanvasView? = null
    private lateinit var gestureDetector: GestureDetector
    private  val myViewModel : DrawableViewModel by activityViewModels()

    //bitmap drawing vars
    private var myBitmap : Bitmap? = null
    private var isDrag = false
    private var offsetX: Float? = null
    private var offsetY: Float? = null
    private val medWidth: Float = 10F
    private val thinWidth: Float = 2F
    private val thickWidth: Float = 25F
    private var currPenSize: Float = medWidth
    private var currPenShape: Paint.Cap = Paint.Cap.ROUND
    private var paintbrush = Paint()
    private var pathList = ArrayList<PaintedPath>()
    private var bitmapWidth: Int? = null
    private var bitmapHeight: Int? = null
    private var viewWidth: Float? = null
    private var viewHeight: Float? = null
    private var width = 8F
    private var currentPath = Path()
    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("MM-dd-yyyy")
    private var currentDate: Date? = null


    /**
     * Creates the view
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
     * Attaches listeners and restores saved items such as the bitmap or color
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // displays color picker
        binding.pallete.setOnClickListener {
            loadColorPicker()
        }

        //Evaluates the drawing state and sets the title and other vars accordingly
        state = requireArguments().getString("New")
        if(state != null){
            title = state
            if(myViewModel.currBitmap.value == null){
                val bitmap = createNewBitmap()
                myViewModel.updateBitmap(bitmap)
                currColor = Color.BLACK
                myViewModel.updateColor(currColor!!)
            }
        }else{
            title = requireArguments().getString("Title")
        }

        myBitmap = myViewModel.currBitmap.value
        currColor = myViewModel.currColor.value
        binding.Title.setText(title)

        myViewModel.currColor.observe(viewLifecycleOwner) { color ->
            currColor = color
            paintbrush.color = color
        }

        //observe changes on bitmap
        myViewModel.currBitmap.observe(viewLifecycleOwner) { bitmap ->
            myBitmap = bitmap // Assign it to fragment's bitmap variable
            binding.canvas.setBitmap(bitmap)
        }

        // displays pen size / shape popup
        binding.paintBrush.setOnClickListener {
            showPopUp()
        }

        canvasView = binding.canvas
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
        //Handles drawing on canvas
        canvasView!!.setOnTouchListener{_, event-> onCanvasTouch(event)}

        //Initializes things to draw
        initBrush()
        initVars()
    }

    /**
     * Initializes brush properties
     */
    private fun initBrush() {
        paintbrush.isAntiAlias = true
        paintbrush.color = currColor!!
        paintbrush.strokeWidth = width
        paintbrush.style = Paint.Style.STROKE
        paintbrush.strokeJoin = Paint.Join.ROUND
        paintbrush.strokeCap = Paint.Cap.ROUND
    }

    /**
     * Initializes variables for drawing
     */
    private fun initVars(){
        viewWidth = canvasView!!.width.toFloat()
        viewHeight = canvasView!!.height.toFloat()
        val swidth = viewWidth!! / bitmapWidth!!
        val sheight = viewHeight!! / bitmapHeight!!
        offsetX = (viewWidth!! - bitmapWidth!! * swidth) / 2
        offsetY = (viewHeight!! - bitmapHeight!! * sheight) / 2
    }

    /**
     * Handles touch events on the canvas
     */
    private fun onCanvasTouch(event: MotionEvent): Boolean {
        val (bX, bY) = translatecoords(event.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath.moveTo(bX, bY)
                isDrag = false
                drawOnBitmap()
            }

            MotionEvent.ACTION_MOVE -> {
//                currentPath.lineTo(bX, bY)
//                pathList.add(PaintedPath(currentPath, paintbrush.color, paintbrush.strokeWidth, paintbrush.strokeCap))
//                drawOnBitmap()
                isDrag = true // Set flag to indicate dragging
                currentPath.lineTo(bX, bY)
                // Add to pathList only when significant movement has occurred to avoid duplicate paths
                if (!pathList.contains(PaintedPath(currentPath, paintbrush.color, paintbrush.strokeWidth, paintbrush.strokeCap))) {
                    pathList.add(PaintedPath(Path(currentPath), paintbrush.color, paintbrush.strokeWidth, paintbrush.strokeCap))
                }
                drawOnBitmap()
            }

            MotionEvent.ACTION_UP -> {
                if (isDrag) {
                    pathList.add(PaintedPath(Path(currentPath), paintbrush.color, paintbrush.strokeWidth, paintbrush.strokeCap))
                    currentPath.reset()
                } else {
                    drawDot(bX, bY)
                }
                drawOnBitmap()
            }
        }
        return true
    }

    /**
     * Creates a new white bitmap
     */
    private fun createNewBitmap(): Bitmap {
        return Bitmap.createBitmap(2000, 2000, Bitmap.Config.ARGB_8888)
    }

    /**
     * Handles drawing on the bitmap
     */
    private fun drawOnBitmap() {
        val canvas = Canvas(myBitmap!!)
        val tempBrush = Paint()
        tempBrush.style = Paint.Style.STROKE
        for (path in pathList){
            tempBrush.color = path.color
            tempBrush.strokeWidth = path.width
            tempBrush.strokeCap = path.shape
            canvas.drawPath(path.path, tempBrush)
        }
        myViewModel.updateBitmap(myBitmap!!)
        updateCanvasView()
    }

    private fun drawDot(x: Float, y: Float) {
        val dotPath = Path()
        dotPath.addCircle(x, y, paintbrush.strokeWidth / 2, Path.Direction.CW)
        pathList.add(PaintedPath(dotPath, paintbrush.color, paintbrush.strokeWidth, paintbrush.strokeCap))
        
    }

    /**
     * Updates the canvas with the bitmap
     */
    private fun updateCanvasView() {
        canvasView!!.setBitmap(myBitmap!!)
    }

    /**
     * Translates view coordinates to bitmap coordinates
     * @param touchX x coordinate in the view
     * @param touchY y coordinate in the view
     */
    private fun translatecoords(touchX: Float, touchY: Float): Pair<Float, Float> {
        val bitmapX = touchX - offsetX!!
        val bitmapY = touchY - offsetY!!
        return Pair(bitmapX, bitmapY)
    }


    /**
     * Displays the color picker
     */
    private fun loadColorPicker() {
        AmbilWarnaDialog(requireActivity(), currColor!!,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    myViewModel.updateColor(color)
                }
            }).show()
    }


    /**
     * Displays the pop up for changing sizes and shape
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
            setPenSize(currPenSize)
            dialog.hide()
        }
        medPen.setOnClickListener {
            currPenSize = medWidth
            setPenSize(currPenSize)
            dialog.hide()
        }
        thickPen.setOnClickListener {
            currPenSize = thickWidth
            setPenSize(currPenSize)
            dialog.hide()
        }

        // shape listeners //
        trianglePen.setOnClickListener {
            currPenShape = Paint.Cap.BUTT
            setPenShape(currPenShape)
            dialog.hide()
        }
        squarePen.setOnClickListener {
            currPenShape = Paint.Cap.SQUARE
            setPenShape(currPenShape)
            dialog.hide()
        }
        roundPen.setOnClickListener {
            currPenShape = Paint.Cap.ROUND
            setPenShape(currPenShape)
            dialog.hide()
        }

        dialog.show()
    }

    /**
     * Sets the size of the pen
     * @param penSize the new pen size
     */
    private fun setPenSize(penSize: Float) {
        paintbrush.strokeWidth = penSize
        currPenSize = paintbrush.strokeWidth
        currentPath = Path()
    }

    /**
     * Sets the shape of the pen
     * @param penShape the new pen shape
     */
    private fun setPenShape(penShape: Paint.Cap) {
        paintbrush.strokeCap = penShape
        currPenShape = paintbrush.strokeCap
        currentPath = Path()
    }


    /**
     * Saves the drawing when back is clicked, then goes back to the list of drawings
     */
    private fun onBackClicked() {
        if (pathList.size > 0) {
            currentDate = Date()
            val dateString = dateFormat.format(currentDate)
            val d = Drawing(binding.Title.text.toString(), myBitmap!!, dateString)
            if (state != null) {
                myViewModel.add(d)
            } else {
                myViewModel.fixOrder(d)
            }
        }
        findNavController().navigate(R.id.action_drawingCanvas_to_drawingsList)
    }

    /**
     * Destroys the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}