package com.maheshvenkat.pexels.ui.photographer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.maheshvenkat.pexels.R
import com.maheshvenkat.pexels.databinding.FragmentPhotographerInfoBinding


class PhotographerInfoFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        @Suppress("UNUSED_VARIABLE")
        val binding = FragmentPhotographerInfoBinding.inflate(inflater)

        val selectedPhotographer =
            PhotographerInfoFragmentArgs.fromBundle(requireArguments()).selectedPhotographer
        val viewModelFactory = PhotographerInfoViewModelFactory(selectedPhotographer)
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(PhotographerInfoViewModel::class.java)

        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}