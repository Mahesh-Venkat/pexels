package com.maheshvenkat.pexels.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maheshvenkat.pexels.models.Photo
import com.maheshvenkat.pexels.network.PexelsService
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class PexelRepository(private val service: PexelsService) {

    /**
     * Search Photos whose names match the query, exposed as a stream of data that will emit
     * every time we get more data from the network.
     */
    fun getSearchResultStream(query: String): Flow<PagingData<Photo>> {
        Timber.d("Pexels, New query: $query")

        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { PexelPagingsource(service, query) }
        ).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 30
    }
}