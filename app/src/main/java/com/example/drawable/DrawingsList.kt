package com.example.drawable

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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.drawable.databinding.FragmentDrawingsListBinding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
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

        val onDeleteButtonClicked: (DrawingPath) -> Unit = { dPath ->
            myViewModel.removeDrawing(dPath)

        }

        viewLifecycleOwner.lifecycleScope.launch {
            myViewModel.count.collect { countValue ->
                currentCount = countValue
            }
        }

        binding.composeView1.setContent {
            val drawings by myViewModel.drawings.collectAsState(initial = emptyList())
            DrawingsListContent(Modifier.padding(16.dp), drawings, onClicked, onDeleteButtonClicked)
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
    fun DrawingsListContent(modifier: Modifier = Modifier, drawings: List<Drawing>, onClick: (DrawingPath) -> Unit, onDeleteClicked: (DrawingPath) -> Unit)
    {

        Column()
        {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
                    .fillMaxSize()
                  //  .border(border = BorderStroke(width = 2.dp, color = Color.Black))
            )
            {
                items(items = drawings){ drawing ->
                    DrawingListItem(drawing, onClick = { onClick(drawing.dPath) }, onDeleteClicked = {onDeleteClicked(drawing.dPath)})
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
        onClick: () -> Unit,
        onDeleteClicked: () -> Unit,
        )
    {
        var showMenu by remember { mutableStateOf(false) }
        ElevatedCard(
            onClick = { onClick()},
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .fillMaxWidth() // This makes the width fill the maximum available space
                .height(dimensionResource(id = R.dimen.card_item_height))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Row(modifier = Modifier.padding(all = 12.dp),
                verticalAlignment = Alignment.CenterVertically){
                // Add drawing preview
                Image(
                    bitmap = drawing.bitmap.asImageBitmap(),
                    contentDescription = "Drawing Preview",
                    contentScale = ContentScale.Fit)

                //Add horizontal spacer between drawing preview and title column
                Spacer(modifier = Modifier.width(10.dp))

                //Add column for title and the modification date
                Column {
                    // Add drawing title
                    Text(text = drawing.dPath.name,
                        fontSize = 22.sp,
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

                // Add container for the more options button
                Box(
                    contentAlignment = Alignment.TopEnd, // Aligns the IconButton to the end (right)
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .width(100.dp)
                        .height(100.dp)
                ){
                    // Delete button
                    // FloatingActionButton(onClick = { onDeleteClicked() },
                    FloatingActionButton(onClick = { showMenu = !showMenu },
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp),
                        shape = CircleShape
                    ) {
                        Icon( painter = painterResource(id = R.drawable.more_options_default), contentDescription = "More Options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Delete",
                                    fontSize = 18.sp)},
                            onClick = {
                                showMenu = false
                                onDeleteClicked()
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.trash_default), // Use the Material Icons Delete icon
                                    contentDescription = "Delete"
                                )
                            }
                        )
                    }
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
