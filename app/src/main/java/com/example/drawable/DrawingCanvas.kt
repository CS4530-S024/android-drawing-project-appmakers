package com.example.drawable
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drawable.databinding.FragmentDrawingCanvasBinding
import yuku.ambilwarna.AmbilWarnaDialog


class DrawingCanvas : Fragment() {

    private var _binding: FragmentDrawingCanvasBinding? = null
    private val binding by lazy { _binding!! }
    private var currColor: Int = Color.BLACK
    private var title: String? = null

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
        title = requireArguments().getString("title")
        binding.Title.setText(title)
    }

    private fun loadColorPicker(){
        AmbilWarnaDialog(requireActivity(), currColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }
                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    currColor = color
                }
            }).show()
    }


    private fun onBackClicked(){
        //save drawing to viewmodel
        //go back to list
        findNavController().navigate(R.id.action_drawingCanvas_to_drawingsList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}