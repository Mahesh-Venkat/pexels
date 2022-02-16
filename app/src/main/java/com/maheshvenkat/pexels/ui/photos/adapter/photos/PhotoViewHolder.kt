package com.maheshvenkat.pexels.ui.photos.adapter.photos

import android.net.ConnectivityManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maheshvenkat.pexels.R
import com.maheshvenkat.pexels.models.Photo


/**
 * View Holder for a Photo RecyclerView list item.
 */
class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val imageView: ImageView = view.findViewById(R.id.photoImage)
    private var photo: Photo? = null

    fun bind(photo: Photo?) {
        if (photo == null) {
            imageView.visibility = View.GONE
        } else {
            showImageData(photo)
        }
    }

    private fun showImageData(photo: Photo) {
        val imgUri =
            getUriBaseOnNetworkConnectivity(photo)

        this.photo = photo

        Glide.with(imageView.context)
            .load(imgUri)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(imageView)
    }

    private fun getUriBaseOnNetworkConnectivity(photo: Photo): Uri {
        val connectivityManager =
            getSystemService(imageView.context, ConnectivityManager::class.java)
        val imgUri =
            if (connectivityManager?.isActiveNetworkMetered == true) {
                photo.smallUrl.toUri().buildUpon().scheme("https").build()
            } else {
                photo.originalUrl.toUri().buildUpon().scheme("https").build()
            }

        return imgUri
    }

    companion object {
        fun create(parent: ViewGroup): PhotoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photos_grid_view_item, parent, false)
            return PhotoViewHolder(view)
        }
    }
}