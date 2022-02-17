package com.maheshvenkat.pexels.ui.photographer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maheshvenkat.pexels.databinding.FragmentPhotoInfoBinding


class PhotoInfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        @Suppress("UNUSED_VARIABLE")
        val binding = FragmentPhotoInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val selectedPhotographer =
            PhotoInfoFragmentArgs.fromBundle(requireArguments()).selectedPhoto
        val viewModelFactory = PhotoInfoViewModelFactory(selectedPhotographer)
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(PhotoInfoViewModel::class.java)

        return binding.root
    }
}