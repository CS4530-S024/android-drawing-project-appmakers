package com.example.drawable

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drawable.databinding.DrawingItemBinding

class DrawingAdapter(private var drawings: List<Drawing>): RecyclerView.Adapter<DrawingAdapter.DrawingViewHolder>() {

    /**
     *
     */
    fun updateDrawings(newDrawings: List<Drawing>){
        drawings = newDrawings
        notifyDataSetChanged()
    }

    inner class DrawingViewHolder(val binding: DrawingItemBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawingViewHolder {
        return DrawingViewHolder(DrawingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: DrawingViewHolder, position: Int) {
        val item = drawings[position]
        holder.binding.dateView.text = item.date
        holder.binding.title.text = item.name
        holder.binding.drawing.setImageBitmap(item.bitmap)
    }

    /**
     *Gets the count of the items in the list
     */
    override fun getItemCount(): Int = drawings.size

}
