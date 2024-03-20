package com.example.drawable

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.drawable.databinding.FragmentDrawingsListBinding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController


class DrawingsList : Fragment() {
    private var _binding: FragmentDrawingsListBinding? = null
    private val binding by lazy { _binding!! }
    private lateinit var recycler: RecyclerView
    private lateinit var myAdapter: DrawingAdapter

    //    private  val myViewModel : DrawableViewModel by activityViewModels()
//    private val myViewModel: DrawableViewModel by activityViewModels{
//        DrawableViewModel.Factory((application as DrawableApplication).drawingRepository)
//    }
    private val myViewModel: DrawableViewModel by activityViewModels {
        val application = requireActivity().application as DrawableApplication
        DrawableViewModel.Factory(application.drawingRepository)
    }
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

        // Inflate the layout for this fragment
        val binding = FragmentDrawingsListBinding.inflate(layoutInflater)

        //ComposeView gives us a `Composable` context to run functions in
        //ComposeView gives us a `Composable` context to run functions in
        binding.composeView1.setContent {
            DrawingsList()
        }

        return binding.root

//        return ComposeView(requireContext()).apply {
//            setContent {
//                DrawingsList()
//            }
//        }



    }

    /**
     * Attaches listeners and restores saved items such as the list of drawings
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        recycler = binding.recycler

//        swipe = object: Swiper(requireContext()){
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                when(direction){
//                    ItemTouchHelper.LEFT->{
//                        myViewModel.removeDrawing(viewHolder.bindingAdapterPosition)
//
//                    }
//                }
//            }
//        }
//
//        touchy = ItemTouchHelper(swipe)
//
//        touchy.attachToRecyclerView(recycler)

        recycler.layoutManager = LinearLayoutManager(context)

//        myAdapter = DrawingAdapter(listOf(), sendClick = {
//            sendClick(it)
//        })

        recycler.adapter = myAdapter

        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas, Bundle().apply {
                putString("New", "Drawing " + (myAdapter.itemCount + 1))
            })
        }
    }

    @SuppressLint("NotConstructor")
    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun DrawingsList() {
        val drawings by myViewModel.drawings.collectAsState(initial = emptyList())
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = drawings, key = { drawing ->
                drawing.dPath.filePath // Assuming this is a unique key for each item
            }) { drawing ->
                val density = LocalDensity.current
                val swipeState = remember {
                    SwipeToDismissBoxState(
                        initialValue = SwipeToDismissBoxValue.EndToStart, // Assuming an enum or similar for state values
                        density = density,
                        confirmValueChange = { _ ->
                            true
                        },
                        positionalThreshold = { totalDistance ->
                            totalDistance / 4
                        }
                    )
                }

                SwipeToDismissBox(
                    state = swipeState,
                    enableDismissFromStartToEnd = true,
                    backgroundContent = {
                        // Background content goes here - e.g., an icon indicating swipe action
                    }, content = {
                        DrawingItem(drawing, onItemClicked = {
                            myViewModel.onDrawingClicked(drawing.dPath.filePath)
                        })
                    }
                )
            }
        }
    }


    @Composable
    fun DrawingItem(
        drawing: Drawing,
        onItemClicked: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .clickable { onItemClicked(drawing.dPath.filePath) }
        ) {
            Image(
                bitmap = drawing.bitmap.asImageBitmap(),
                contentDescription = "Drawing image",
                modifier = modifier.padding(bottom = 8.dp)
            )
        }
    }


    //    /**
//     * Sets the current bitmap, then goes to the canvas
//     * @param pos the position of the clicked drawing
//     */
//    private fun sendClick(pos: Int){
//        myViewModel.setCurrBitmap(pos)
//        findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas, Bundle().apply {
//            putString("Title", myViewModel.getDrawingTitle(pos))
//        })
//    }
    @PreviewLightDark
    @Preview(showBackground = true)
    @Composable
    fun DrawablePreview() {
        DrawingsList()
    }

    /**
     * Destroys the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
