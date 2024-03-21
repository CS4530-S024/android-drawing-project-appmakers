package com.example.drawable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DrawingsList : Fragment() {
    private var _binding: FragmentDrawingsListBinding? = null
    private val binding by lazy { _binding!! }
    private var currentCount: Int = 0
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

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
        val onClicked: (DrawingPath) -> Unit = { dpath ->
            myViewModel.setCurrBitmap(dpath)
            findNavController().navigate(
                R.id.action_drawingsList_to_drawingCanvas,
                Bundle().apply {
                    putString("Title", dpath.name)
                }
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            myViewModel.count.collect { countValue ->
                currentCount = countValue
            }
        }

        binding.composeView1.setContent {
            val drawings by myViewModel.drawings.collectAsState(initial = emptyList())
            DrawingsListContent(Modifier.padding(16.dp), drawings, onClicked)
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
                putString("New", "Drawing " + (currentCount + 1))
            })
        }
    }

    /**
     * The DrawingsList as a Composable item.
     */
    @Composable
    fun DrawingsListContent(modifier: Modifier = Modifier, drawings: List<Drawing>, onClick: (DrawingPath) -> Unit)
    {
        Column()
        {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = modifier
                    .fillMaxSize()
                    .border(border = BorderStroke(width = 2.dp, color = Color.Black))
            )
            {
                items(items = drawings){ drawing ->
                    DrawingListItem(drawing, onClick = { onClick(drawing.dPath) })
                }
            }
        }
    }

    /**
     *
     */
    @Composable
    fun DrawingListItem(
        drawing: Drawing,
        onClick: () -> Unit)
    {
        ElevatedCard(
            onClick = { onClick()},
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth() // This makes the width fill the maximum available space
                .height(100.dp)
        ) {
            Row(modifier = Modifier.padding(all = 8.dp)){
                // Add drawing preview
                Image(
                    bitmap = drawing.bitmap.asImageBitmap(),
                    contentDescription = "Drawing Preview",
                    modifier = Modifier
                        .size(50.dp),
                    contentScale = ContentScale.Fit)

                //Add horizontal spacer between drawing preview and title column
                Spacer(modifier = Modifier.width(8.dp))

                //Add column for title and the modification date
                Column {
                    // Add drawing title
                    Text(text = drawing.dPath.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    // Add a vertical space between the drawing title and the modified date
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = dateFormat.format(drawing.dPath.modDate),
                        style = TextStyle(
                        color = Color.Gray, // Change the color as needed
                        fontSize = 20.sp    // Change the size as needed
                    )
                    )
                }
            }
        }
    }

    /**
     * Destroys the view
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
