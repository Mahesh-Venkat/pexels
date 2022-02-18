package com.maheshvenkat.pexels

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.maheshvenkat.pexels.data.PhotosRepository
import com.maheshvenkat.pexels.db.PhotosDatabase
import com.maheshvenkat.pexels.network.PexelsService
import com.maheshvenkat.pexels.ui.photos.ViewModelFactory

/**
 * Class that handles object creation.
 * Like this, objects can be passed as parameters in the constructors and then replaced for
 * testing, where needed.
 */
object Injection {

    /**
     * Creates an instance of PhotoRepository based on the PexelsService
     */
    private fun providePhotoRepository(context: Context): PhotosRepository {
        return PhotosRepository(PexelsService.create(), PhotosDatabase.getInstance(context))
    }

    /**
     * Provides the [ViewModelProvider.Factory] that is then used to get a reference to
     * ViewModel objects.
     */
    fun provideViewModelFactory(
        context: Context,
        owner: SavedStateRegistryOwner
    ): ViewModelProvider.Factory {
        return ViewModelFactory(owner, providePhotoRepository(context))
    }
}
