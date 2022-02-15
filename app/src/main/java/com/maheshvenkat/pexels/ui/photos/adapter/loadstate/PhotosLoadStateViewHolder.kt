package com.maheshvenkat.pexels.ui.photos.adapter.loadstate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.maheshvenkat.pexels.R
import com.maheshvenkat.pexels.databinding.PhotosLoadStateFooterViewItemBinding


class PhotosLoadStateViewHolder(
    private val binding: PhotosLoadStateFooterViewItemBinding,
    tryAgain: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.tryAgainButton.setOnClickListener { tryAgain.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMessage.text = loadState.error.localizedMessage
        }
        binding.progressBarPhotosLoadStateFooter.isVisible = loadState is LoadState.Loading
        binding.tryAgainButton.isVisible = loadState is LoadState.Error
        binding.errorMessage.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, tryAgain: () -> Unit): PhotosLoadStateViewHolder {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photos_load_state_footer_view_item, parent, false)
            val binding = PhotosLoadStateFooterViewItemBinding.bind(view)

            return PhotosLoadStateViewHolder(binding, tryAgain)
        }
    }
}