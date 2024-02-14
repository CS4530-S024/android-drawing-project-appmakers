package com.example.drawable

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drawable.databinding.DrawingItemBinding

class DrawingAdapter(private var drawings: List<Drawing>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //todo(add necesssary callbacks)


    fun updateDrawings(newDrawings: List<Drawing>){
        drawings = newDrawings
        notifyDataSetChanged()
    }
    inner class DrawingViewHolder(val binding: DrawingItemBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DrawingViewHolder(DrawingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = drawings[position]
        //add date and title
    }

    /**
     *Gets the count of the items in the list
     */
    override fun getItemCount(): Int = drawings.size
}