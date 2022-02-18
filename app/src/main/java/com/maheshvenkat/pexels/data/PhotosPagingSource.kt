package com.maheshvenkat.pexels.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.maheshvenkat.pexels.models.Photo
import com.maheshvenkat.pexels.network.PexelsService
import com.maheshvenkat.pexels.network.asDomainModel
import retrofit2.HttpException
import java.io.IOException


class PhotosPagingSource(
    private val service: PexelsService,
    private val query: String
) : PagingSource<Int, Photo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val position = params.key ?: PHOTOS_STARTING_PAGE_INDEX
        val apiQuery = query
        return try {
            val response = service.searchPhotos(apiQuery, position, params.loadSize)
            val photos = response.photos.asDomainModel()
            val nextKey = if (photos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = photos,
                prevKey = if (position == PHOTOS_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}