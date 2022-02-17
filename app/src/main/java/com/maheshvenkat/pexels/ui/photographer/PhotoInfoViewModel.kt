package com.maheshvenkat.pexels.ui.photographer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maheshvenkat.pexels.models.Photo

class PhotoInfoViewModel(selectedPhoto: Photo) : ViewModel() {
    private val _selectedPhotographer = MutableLiveData<Photo>()

    val selectedPhoto: LiveData<Photo>
        get() = _selectedPhotographer

    init {
        _selectedPhotographer.value = selectedPhoto
    }
}