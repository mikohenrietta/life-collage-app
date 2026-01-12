package com.example.lifecollage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lifecollage.R
import com.example.lifecollage.model.CollageItem

class CollageAdapter(
    private var onItemClickListener: ((CollageItem) -> Unit)? = null
) : ListAdapter<CollageItem, CollageAdapter.CollageViewHolder>(CollageDiffCallback()) {

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
        val item = getItem(position)

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

    fun setOnItemClickListener(listener: (CollageItem) -> Unit) {
        onItemClickListener = listener
    }
}

class CollageDiffCallback : DiffUtil.ItemCallback<CollageItem>() {
    override fun areItemsTheSame(oldItem: CollageItem, newItem: CollageItem): Boolean {

        return oldItem._id == newItem._id
    }

    override fun areContentsTheSame(oldItem: CollageItem, newItem: CollageItem): Boolean {
        return oldItem.title == newItem.title &&
                oldItem.description == newItem.description &&
                oldItem.rating == newItem.rating &&
                oldItem.date == newItem.date &&
                oldItem.imageUri == newItem.imageUri
    }
}