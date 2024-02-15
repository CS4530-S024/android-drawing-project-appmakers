package com.example.drawable
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
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
import androidx.navigation.fragment.findNavController
import com.example.drawable.databinding.FragmentDrawingCanvasBinding
import yuku.ambilwarna.AmbilWarnaDialog


class DrawingCanvas : Fragment() {
    private var _binding: FragmentDrawingCanvasBinding? = null
    private val binding by lazy { _binding!! }
    private var currColor: Int = Color.BLACK
    private var title: String? = null
    private var canvas: CanvasView? = null
    private lateinit var gestureDetector: GestureDetector

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pallete.setOnClickListener {
            loadColorPicker()
        }
        title = requireArguments().getString("title")
        binding.Title.setText(title)

        //Displays color picker
        binding.paintBrush.setOnClickListener {
            showPopUp()
        }
        canvas = binding.canvas

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
                    binding.canvas.setColor(color)
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

        thinPen.setOnClickListener {
            // emit signal to change pen size to thin
            dialog.hide()
        }
        medPen.setOnClickListener {
            // emit signal to change pen size to medium
            dialog.hide()
        }
        thickPen.setOnClickListener {
            // emit signal to change pen size to thick
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