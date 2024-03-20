package com.example.drawable
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.drawable.databinding.FragmentDrawingsListBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
<<<<<<< Updated upstream
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.annotation.Sampled
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.DismissValue.DismissedToEnd
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

=======
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
>>>>>>> Stashed changes

/**
 * This class contains methods and functionality for displaying the new
 * implementation of our DrawingsList.
 */
class DrawingsList : Fragment() {
    private var _binding: FragmentDrawingsListBinding? = null
<<<<<<< Updated upstream
    private val binding by lazy { _binding!! }
/*    private val myViewModel: DrawableViewModel by activityViewModels {
        val application = requireActivity().application as DrawableApplication
        DrawableViewModel.Factory(application.drawingRepository)
    }*/
    private lateinit var swipe: Swiper
    private lateinit var touchy: ItemTouchHelper
=======
>>>>>>> Stashed changes

    /**
     * Creates the view that contains the DrawingsList/Items. The newer
     * version of the RecyclerView from our previous implementation.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
<<<<<<< Updated upstream
        _binding = FragmentDrawingsListBinding.inflate(inflater, container, false)
//        myViewModel.drawingsList.observe(viewLifecycleOwner){
//            (recycler.adapter as DrawingAdapter).updateDrawings(it)
//        }
        return ComposeView(requireContext()).apply {
            setContent {
                drawingsList(onDrawingClick = { drawingName ->
                    // Handle the click event, e.g., navigate with the drawing name
                })
            }
        }
=======
        val binding = FragmentDrawingsListBinding.inflate(layoutInflater)

        binding.composeView1.setContent {
            DrawingsList(Modifier.padding(16.dp)) {
                findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas)
            }
        }

>>>>>>> Stashed changes
        return binding.root

    }

    /**
     * The DrawingsList as a Composable item.
     */
<<<<<<< Updated upstream
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*recycler = binding.recycler
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = myAdapter*/
        binding.add.setOnClickListener{
            findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas, Bundle().apply {
                putString("New Drawing", "1")
            })
        }
    }

    @SuppressLint("NotConstructor")
    @Composable
<<<<<<< Updated upstream
    @OptIn(ExperimentalMaterialApi::class)
    fun drawingsList(onDrawingClick: (String) -> Unit) {
//        // Collect the list of drawings from the Flow
//        val drawings by myViewModel.drawings.collectAsState(initial = emptyList())
//        LazyColumn {
//
//            items(drawings){drawing ->
//                DrawingItem(drawing = drawing, onItemClicked = onDrawingClick)
//            }
//        }
        val drawings by myViewModel.drawings.collectAsState(initial = emptyList())

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = drawings, key = { drawing ->
                drawing.dPath.filePath // Assuming this is a unique key for each item
            }) { drawing ->
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                            // Perform the delete action on swipe. Adjust this as needed.
                            // For example, using a ViewModel function:
                            myViewModel.removeDrawing(drawing)
                        }
                        true
                    }
                )
                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                    background = {
                        // Background content goes here - e.g., an icon indicating swipe action
                    },
                    dismissContent = {
                        DrawingItem(drawing = drawing, onItemClicked = onDrawingClick)
                    }
                )
=======
    fun DrawingsList() {
        // Collect the Flow<List<Drawing>> and convert it to a state
        val drawings by myViewModel.drawings.collectAsState(listOf())
        LazyColumn {
            items(drawings) { drawing ->
                // Pass a lambda that calls the ViewModel's function
                DrawingItem(drawing, { fileName ->
                    myViewModel.onDrawingClicked(fileName)
                })
>>>>>>> Stashed changes
=======
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
>>>>>>> Stashed changes
            }
        }
    }

<<<<<<< Updated upstream


    @Composable
    fun DrawingItem(drawing: Drawing, onItemClicked: (String) -> Unit, modifier: Modifier = Modifier) {
        Column(
        modifier = Modifier
            .padding(16.dp)
            .clickable { onItemClicked(drawing.dPath.filePath) }
    ) {
        Image(
            bitmap = drawing.bitmap.asImageBitmap(),
            contentDescription = "Drawing image",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        // Add more UI elements as needed
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
=======
    @Composable
    fun DrawingItem(drawing: Drawing, onClick: () -> Unit) {

    }
>>>>>>> Stashed changes

    /**
     * Destroys the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
