package com.example.lifecollage.adapter

import android.content.Intent
import com.example.lifecollage.model.CollageItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.lifecollage.R
import com.example.lifecollage.ui.DetailActivity

class CollageAdapter(
    private var items: MutableList<CollageItem>,
    private var onItemClickListener: ((CollageItem) -> Unit)? = null

) : RecyclerView.Adapter<CollageAdapter.CollageViewHolder>() {

    class CollageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleText)
        val description: TextView = view.findViewById(R.id.descriptionText)
        val date: TextView = view.findViewById(R.id.dateText)
        val rating: TextView = view.findViewById(R.id.ratingText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collage, parent, false)
        return CollageViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollageViewHolder, position: Int) {
        val item = items[position]

        val imageView = holder.itemView.findViewById<ImageView>(R.id.itemImage)

        if (item.imageUri != null) {
            imageView.setImageURI(item.imageUri.toUri())
            imageView.visibility = View.VISIBLE
        } else {
            imageView.setImageURI(null)
            imageView.visibility = View.GONE
        }

        holder.title.text = item.title
        holder.description.text = item.description
        holder.date.text = item.date
        holder.rating.text = item.rating

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setOnItemClickListener(listener: (CollageItem) -> Unit) {
        onItemClickListener = listener
    }
    fun updateList(newList: MutableList<CollageItem>) {
        items = newList
        notifyDataSetChanged()
    }
}
