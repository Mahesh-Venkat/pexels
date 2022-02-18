package com.maheshvenkat.pexels.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maheshvenkat.pexels.db.PhotosDatabase
import com.maheshvenkat.pexels.models.Photo
import com.maheshvenkat.pexels.network.PexelsService
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

const val PHOTOS_STARTING_PAGE_INDEX = 1
const val NETWORK_PAGE_SIZE = 30


class PhotosRepository(private val service: PexelsService, private val database: PhotosDatabase) {

    /**
     * Intentionally commented this code, as there is a bug with network -> Cache  and Cache -> Network
     * I will be enabling the initial just Network fetch for now and leaving the database access in this file and everywhere
     */

//    /**
//     * Search Photos whose names match the query, exposed as a stream of data that will emit
//     * every time we get more data from the network and cache it for you in the database.
//     */
//    fun getSearchResultStream(query: String): Flow<PagingData<DBPhoto>> {
//        Timber.d("Pexels, New query: $query")
//
//        val pagingSourceFactory = { database.photosDao().photos() }
//
//        @OptIn(ExperimentalPagingApi::class)
//        return Pager(
//            config = PagingConfig(
//                pageSize = NETWORK_PAGE_SIZE,
//                enablePlaceholders = false
//            ),
//            remoteMediator = PhotosRemoteMediator(
//                query,
//                service,
//                database
//            ),
//            pagingSourceFactory = pagingSourceFactory
//        ).flow
//    }

    /**
     * Search Photos whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network
     */
    fun getSearchResultStream(query: String): Flow<PagingData<Photo>> {
        Timber.d("Pexels, New query: $query")

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { PhotosPagingSource(service, query) }
        ).flow
    }
}