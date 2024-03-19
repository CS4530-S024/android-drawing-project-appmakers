package com.example.drawable
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
import kotlinx.coroutines.flow.Flow
import androidx.compose.foundation.layout.padding
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp



class DrawingsList : Fragment() {
    private var _binding: FragmentDrawingsListBinding? = null
    private val binding by lazy { _binding!! }
    private lateinit var recycler: RecyclerView
    private lateinit var myAdapter: DrawingAdapter
    private  val myViewModel : DrawableViewModel by activityViewModels()
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

        _binding = FragmentDrawingsListBinding.inflate(inflater, container, false)
        myViewModel.drawingsList.observe(viewLifecycleOwner){
            (recycler.adapter as DrawingAdapter).updateDrawings(it)
        }
        return binding.root

    }

    /**
     * Attaches listeners and restores saved items such as the list of drawings
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = binding.recycler
        swipe = object: Swiper(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT->{
                        myViewModel.removeDrawing(viewHolder.bindingAdapterPosition)

                    }
                }
            }
        }

        touchy = ItemTouchHelper(swipe)
        touchy.attachToRecyclerView(recycler)
        recycler.layoutManager = LinearLayoutManager(context)

        myAdapter = DrawingAdapter(listOf(), sendClick = {
            sendClick(it)
        })

        recycler.adapter = myAdapter
        binding.add.setOnClickListener{
            findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas, Bundle().apply {
                putString("New", "Drawing " + (myAdapter.itemCount + 1))
            })
        }
    }

    @Composable
    fun DrawingsList() {
        // Collect the Flow<List<Drawing>> and convert it to a state
        val drawings by myViewModel.drawingss.collectAsState(listOf())
        LazyColumn {
            items(drawings.size) { index ->
                val drawing = drawings[index]
                // Pass a lambda that calls the ViewModel's function
                DrawingItem(drawing = drawing, onItemClicked = { fileName ->
                    myViewModel.onDrawingClicked(fileName)
                })
            }
        }
    }

    @Composable
    fun DrawingItem(drawing: Drawing, onItemClicked: (String) -> Unit, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .clickable { onItemClicked(drawing.name) }
        ) {
            Image(
                bitmap = drawing.bitmap.asImageBitmap(),
                contentDescription = "Drawing image",
                // Here, you want to apply additional padding specific to the Image, not reuse the modifier parameter
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }

    /**
     * Sets the current bitmap, then goes to the canvas
     * @param pos the position of the clicked drawing
     */
    private fun sendClick(pos: Int){
        myViewModel.setCurrBitmap(pos)
        findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas, Bundle().apply {
            putString("Title", myViewModel.getDrawingTitle(pos))
        })
    }

    /**
     * Destroys the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
