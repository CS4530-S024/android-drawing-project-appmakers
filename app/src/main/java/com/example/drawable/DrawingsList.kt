package com.example.drawable

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.drawable.databinding.FragmentDrawingsListBinding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import java.text.DateFormat

class DrawingsList : Fragment() {
    private var _binding: FragmentDrawingsListBinding? = null
    private val binding by lazy { _binding!! }

    private lateinit var swipe: Swiper
    private lateinit var touchy: ItemTouchHelper
    private var currentCount: Int = 0



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

        viewLifecycleOwner.lifecycleScope.launch {
            myViewModel.count.collect { countValue ->
                currentCount = countValue
            }
        }

        binding.composeView1.setContent {
            DrawingsListContent(Modifier.padding(16.dp), viewModel = myViewModel)
        }
// {
//                findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas)
//            }
        return binding.root
    }

    /**
     * Attaches listeners and restores saved items such as the list of drawings
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Binding and navigation for the + button
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas, Bundle().apply {
                putString("New", "Drawing " + (currentCount + 1))
            })
        }

        //
    }

    /**
     * The DrawingsList as a Composable item.
     */
    @Composable
    fun DrawingsListContent(modifier: Modifier = Modifier,
                     viewModel: DrawableViewModel = viewModel(
                         viewModelStoreOwner = LocalContext.current.findActivity()
                     ))
    {
        Column(modifier = modifier.padding(16.dp))
        {
            val drawings by viewModel.drawings.collectAsState(initial = emptyList())

            LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize())
            {

                items(items = drawings){ drawing ->
                    // * Original
                    DrawingListItem(drawing, onClick = { onClicked(drawing) }, getModDate(drawing))
                }
            }
        }
    }


    @Composable
    fun DrawingListItem(
        drawing: Drawing,
        onClick: () -> Unit,
        date: String)
    {
        ElevatedCard(
            onClick = { onClick() },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier.fillMaxWidth()
                .size(width = 240.dp, height = 100.dp)
        ) {
            Row(modifier = Modifier.padding(all = 8.dp)){
                // Add drawing preview
//                Image(
//                    bitmap = drawing.bitmap.asImageBitmap(),
//                    contentDescription = "Drawing Preview",
//                    modifier = Modifier
//                        .size(50.dp)
//                )
                Image(
                    painter = BitmapPainter(drawing.bitmap.asImageBitmap()),
                    contentScale = ContentScale.Fit,
                    contentDescription = "Drawing Preview" )

                //Add horizontal spacer between drawing preview and title column
                Spacer(modifier = Modifier.width(20.dp))

                //Add column for title and the modification date
                Column {
                    // Add drawing title
                    Text(text = "[ Drawing Title! ]",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    // Add a vertical space between the drawing title and the modified date
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "Last modified: $date")
                }

            }

        }
    }

    private fun getModDate(drawing: Drawing): String{
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        return sdf.format(drawing.dPath.modDate)
    }

    private fun onClicked(drawing:Drawing){
        findNavController().navigate(
            R.id.action_drawingsList_to_drawingCanvas,
            Bundle().apply {
               //putString("New", "Drawing ${currentCount + 1}")
                putString("Existing","${drawing.dPath.name}");
            }
        )
    }

    /**
     * Destroys the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
