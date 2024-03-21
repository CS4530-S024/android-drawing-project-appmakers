package com.example.drawable

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.drawable.databinding.FragmentDrawingsListBinding
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.remember
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


class DrawingsList : Fragment() {
    private var _binding: FragmentDrawingsListBinding? = null
    private val binding by lazy { _binding!! }


    //    private  val myViewModel : DrawableViewModel by activityViewModels()
//    private val myViewModel: DrawableViewModel by activityViewModels{
//        DrawableViewModel.Factory((application as DrawableApplication).drawingRepository)
//    }
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
        val myViewModel: DrawableViewModel by activityViewModels {
            val application = requireActivity().application as DrawableApplication
            DrawableViewModel.Factory(application.drawingRepository)
        }
        // Inflate the layout for this fragment
        val binding = FragmentDrawingsListBinding.inflate(layoutInflater)
        //ComposeView gives us a `Composable` context to run functions in
        binding.composeView1.setContent {
            DrawingsList(myViewModel)
        }

        return binding.root
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


//        myAdapter = DrawingAdapter(listOf(), sendClick = {
//            sendClick(it)
//        })


//        binding.add.setOnClickListener {
//            findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas, Bundle().apply {
//                putString("New", "Drawing " + (myAdapter.itemCount + 1))
//            })
//        }
    }

    @SuppressLint("NotConstructor")
    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun DrawingsList(myViewModel: DrawableViewModel) {
        val drawings by myViewModel.drawings.collectAsState(initial = emptyList())
        LazyColumn(modifier = Modifier.fillMaxSize()) {

            items(items = drawings, key = { drawing ->
                drawing.dPath.filePath // Assuming this is a unique key for each item
            }
            ) { drawing ->
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
//                        DrawingItem(drawing, onItemClicked = {
//                            myViewModel.onDrawingClicked(drawing.dPath.filePath)
//                        })
                    }
                )
            }
        }
    }


//    @Composable
//    fun DrawingItem(
////        drawing: Drawing,
////        onItemClicked: (String) -> Unit,
////        modifier: Modifier = Modifier
//        ball : String
//    ) {
////        Column(
////            modifier = modifier
////                .padding(16.dp)
////                .clickable { onItemClicked(drawing.dPath.filePath) }
////        ) {
////            Image(
////                bitmap = drawing.bitmap.asImageBitmap(),
////                contentDescription = "Drawing image",
////                modifier = modifier.padding(bottom = 8.dp)
////            )
////        }
//        val expanded = remember { mutableStateOf(false) }
//
//
//        Surface(
//            color = MaterialTheme.colorScheme.primary,
//            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
//        ) {
//
//            Column(
//                modifier = Modifier
//                    .padding(24.dp)
//                    .fillMaxWidth()
//            ) {
//
//                Row {
//
//                    Column(
//                        modifier = Modifier
//                            .weight(1f)
//                    ) {
//                        Text(
//                            text = ball, style = MaterialTheme.typography.bodySmall.copy(
//                                fontWeight = FontWeight.ExtraBold
//                            )
//                        )
//                    }
//
//                    OutlinedButton(onClick = { expanded.value = !expanded.value }) {
//                        Text(if (expanded.value) "Show less" else "Show more")
//                    }
//                }
//
////                if (expanded.value) {
////
////                    Column(
////                        modifier = Modifier.padding(
////                            bottom = extraPadding.coerceAtLeast(0.dp)
////                        )
////                    ) {
////                        Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
////                    }
////
////                }
//            }
//
//        }
//
//    }
@Composable
fun DrawingListItem(
    drawing: Drawing)
{
    ElevatedCard(
        onClick = { /* Do something */ },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .size(width = 240.dp, height = 100.dp)
    ) {
        Row(modifier = Modifier.padding(all = 8.dp)){
            // Add drawing preview
            Image(
                bitmap = drawing.bitmap.asImageBitmap(),
                contentDescription = "Drawing Preview",
                modifier = Modifier
                    .size(50.dp))


            //Add horizontal spacer between drawing preview and title column
            Spacer(modifier = Modifier.width(8.dp))

            //Add column for title and the modification date
            Column {
                // Add drawing title
                Text(text = "New Drawing",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                // Add a vertical space between the drawing title and the modified date
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Last modified: [Date]")
            }
        }

    }
}


@Composable
fun DrawingItem(
        drawing: Drawing,
        onItemClicked: (String) -> Unit,
        modifier: Modifier = Modifier,
     ball: String) {
//        Column(
//            modifier = modifier
//                .padding(16.dp)
//                .clickable { onItemClicked(drawing.dPath.filePath) }
//        ) {
//            Image(
//                bitmap = drawing.bitmap.asImageBitmap(),
//                contentDescription = "Drawing image",
//                modifier = modifier.padding(bottom = 8.dp)
//            )
//        }
    val expanded = remember { mutableStateOf(false) }


    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {

            Row(modifier = Modifier.padding(all = 8.dp)) {
                // Add drawing preview
//                Image(
//                    imageBitmap = drawing.bitmap,
//                    contentDescription = "Drawing Preview",
//                    modifier = Modifier
//                        .size(50.dp)
//                        .clip(CircleShape)


                //Add horizontal spacer between the drawing and the title column
                


//                Column(
//                    modifier = Modifier
//                        .weight(1f)
//                ) {
//                    Text(
//                        text = ball, style = MaterialTheme.typography.bodySmall.copy(
//                            fontWeight = FontWeight.ExtraBold
//                        )
//                    )
//                }

//                OutlinedButton(onClick = { expanded.value = !expanded.value }) {
//                    Text(if (expanded.value) "Show less" else "Show more")
//                }
            }

//                if (expanded.value) {
//
//                    Column(
//                        modifier = Modifier.padding(
//                            bottom = extraPadding.coerceAtLeast(0.dp)
//                        )
//                    ) {
//                        Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.")
//                    }
//
//                }
        }

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
//        DrawingsList()
        //DrawingItem("belo")
    }
    @PreviewLightDark
    @Preview(showBackground = true)
    @Composable
    fun DrawPreview() {
//        DrawingsList()

       // DrawingListItem(Drawing())
    }

    /**
     * Destroys the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}