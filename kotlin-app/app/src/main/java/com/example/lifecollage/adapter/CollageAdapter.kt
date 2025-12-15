package com.example.lifecollage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.lifecollage.R
import com.example.lifecollage.model.CollageItem

class CollageAdapter(
    private var items: MutableList<CollageItem>,
    private var onItemClickListener: ((CollageItem) -> Unit)? = null
) : RecyclerView.Adapter<CollageAdapter.CollageViewHolder>() {
    val currentItems: List<CollageItem>
        get() = items
    class CollageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleText)
        val description: TextView = view.findViewById(R.id.descriptionText)
        val date: TextView = view.findViewById(R.id.dateText)
        val rating: TextView = view.findViewById(R.id.ratingText)
        val imageView: ImageView = view.findViewById(R.id.itemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collage, parent, false)
        return CollageViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollageViewHolder, position: Int) {
        val item = items[position]

        // Image
        val uri = item.imageUri
        if (uri != null) {
            holder.imageView.visibility = View.VISIBLE
            holder.imageView.setImageURI(uri.toUri())
        } else {
            holder.imageView.visibility = View.GONE
            holder.imageView.setImageURI(null)
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

    fun setItems(newList: List<CollageItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged() // call once at start
    }

    fun addItem(item: CollageItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun updateItemAt(position: Int, item: CollageItem) {
        if (position < 0 || position >= items.size) return
        items[position] = item
        notifyItemChanged(position)
    }

    fun removeItemAt(position: Int) {
        if (position < 0 || position >= items.size) return
        items.removeAt(position)
        notifyItemRemoved(position)
    }
}
