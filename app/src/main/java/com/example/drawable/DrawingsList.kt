package com.example.drawable

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.drawable.databinding.FragmentDrawingsListBinding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

class DrawingsList : Fragment() {
    private var _binding: FragmentDrawingsListBinding? = null
    private val binding by lazy { _binding!! }
    private lateinit var swipe: Swiper
    private lateinit var touchy: ItemTouchHelper


    /**
     * Creates the view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrawingsListBinding.inflate(layoutInflater)

        val myViewModel: DrawableViewModel by activityViewModels {
            val application = requireActivity().application as DrawableApplication
            DrawableViewModel.Factory(application.drawingRepository)
        }

        binding.composeView1.setContent {
            DrawingsList(Modifier.padding(16.dp), viewModel = myViewModel) {
                findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas)
            }
        }

        return binding.root
    }

    /**
     * Attaches listeners and restores saved items such as the list of drawings
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas, Bundle().apply {
                putString("New", "Drawing ")
            })
        }
    }

    /**
     * The DrawingsList as a Composable item.
     */
    @SuppressLint("NotConstructor")
    @Composable
    fun DrawingsList(modifier: Modifier = Modifier,
                     viewModel: DrawableViewModel = viewModel(
                         viewModelStoreOwner = LocalContext.current.findActivity()
                     ),
                     onClick: () -> Unit)
    {
        Column(modifier = modifier.padding(16.dp))
        {
            val drawings by viewModel.drawings.collectAsState(initial = emptyList())

            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize())
            {
                for (drawing in drawings) {
                    item {
                        DrawingItem(drawing, onClick = onClick)
                    }
                }
            }
        }
    }

    @Composable
    fun DrawingItem(drawing: Drawing, onClick: () -> Unit) {

    }

    /**
     * Destroys the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
