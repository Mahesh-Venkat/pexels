package com.maheshvenkat.pexels.ui.photos.adapter.photos

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.maheshvenkat.pexels.models.Photo

/**
 * Adapter for the list of Photos
 */
class PhotosAdapter(private val onClickListener: OnClickListener) :
    PagingDataAdapter<Photo, PhotoViewHolder>(Photo_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoItem = getItem(position)
        if (photoItem != null) {
            holder.bind(photoItem)

            holder.itemView.setOnClickListener {
                onClickListener.onClick(photoItem)
            }
        }
    }

    companion object {
        private val Photo_COMPARATOR = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean =
                oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (photo: Photo) -> Unit) {
        fun onClick(photo: Photo) = clickListener(photo)
    }
}