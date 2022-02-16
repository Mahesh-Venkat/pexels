package com.maheshvenkat.pexels.ui.photographer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotographerInfoViewModel(selectedPhotographer: PhotographerInfo) : ViewModel() {
    private val _selectedPhotographer = MutableLiveData<PhotographerInfo>()

    val selectedPhoto: LiveData<PhotographerInfo>
        get() = _selectedPhotographer

    init {
        _selectedPhotographer.value = selectedPhotographer
    }
}