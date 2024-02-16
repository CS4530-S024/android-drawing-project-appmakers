package com.example.drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.drawable.databinding.FragmentDrawingsListBinding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager


class DrawingsList : Fragment() {
    private var _binding: FragmentDrawingsListBinding? = null
    private val binding by lazy { _binding!! }
    private lateinit var recycler: RecyclerView
    private lateinit var myAdapter: DrawingAdapter
    private  val myViewModel : DrawableViewModel by activityViewModels()
    private lateinit var swipe: Swiper
    private lateinit var touchy: ItemTouchHelper


    /**
     *
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
     *
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

    /**
     *
     */
    private fun sendClick(pos: Int){
        myViewModel.setCurrBitmap(pos)
        findNavController().navigate(R.id.action_drawingsList_to_drawingCanvas, Bundle().apply {
            putString("Title", myViewModel.getDrawingTitle(pos))
        })
    }

    /**
     *
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
