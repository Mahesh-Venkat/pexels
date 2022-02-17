package com.maheshvenkat.pexels.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maheshvenkat.pexels.db.DBPhoto
import com.maheshvenkat.pexels.db.PhotosDatabase
import com.maheshvenkat.pexels.network.PexelsService
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

const val PHOTOS_STARTING_PAGE_INDEX = 1

class PhotosRepository(private val service: PexelsService, private val database: PhotosDatabase) {

    /**
     * Search Photos whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    fun getSearchResultStream(query: String): Flow<PagingData<DBPhoto>> {
        Timber.d("Pexels, New query: $query")

        val pagingSourceFactory = { database.photosDao().photos() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = PhotosRemoteMediator(
                query,
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}