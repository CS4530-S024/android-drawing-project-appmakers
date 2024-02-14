package com.example.drawable

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.drawable.databinding.FragmentDrawingCanvasBinding
import yuku.ambilwarna.AmbilWarnaDialog

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class DrawingCanvas : Fragment() {
    private var _binding: FragmentDrawingCanvasBinding? = null
    private val binding by lazy { _binding!! }
    private var currColor: Int = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        _binding = FragmentDrawingCanvasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pallete.setOnClickListener {
            loadColorPicker()
        }

        binding.paintBrush.setOnClickListener {
            showPopUp()
        }
    }

    private fun loadColorPicker() {
        AmbilWarnaDialog(requireActivity(), currColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    currColor = color
                }
            }).show()
    }

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

//    override fun onDestroy() {
//        super.onDestroy()
//    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}