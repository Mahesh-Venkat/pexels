package com.maheshvenkat.pexels.ui.photoinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.maheshvenkat.pexels.MainActivity
import com.maheshvenkat.pexels.R
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
        (requireActivity() as MainActivity).title = getString(R.string.page_title_photo_info)

        val selectedPhotographer =
            PhotoInfoFragmentArgs.fromBundle(requireArguments()).selectedPhoto
        val viewModelFactory = PhotoInfoViewModelFactory(selectedPhotographer)
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(PhotoInfoViewModel::class.java)

        return binding.root
    }
}