package com.maheshvenkat.pexels.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.maheshvenkat.pexels.db.DBPhoto
import com.maheshvenkat.pexels.db.PageKeys
import com.maheshvenkat.pexels.db.PhotosDatabase
import com.maheshvenkat.pexels.network.PexelsService
import com.maheshvenkat.pexels.network.asDatabaseModel
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PhotosRemoteMediator(
    private val query: String,
    private val service: PexelsService,
    private val photosDatabase: PhotosDatabase
) : RemoteMediator<Int, DBPhoto>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DBPhoto>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                //Keep a tab on the -1 here
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: PHOTOS_STARTING_PAGE_INDEX
            }
        }
        val apiQuery = query

        try {
            val apiResponse = service.searchPhotos(apiQuery, page, state.config.pageSize)

            val photos = apiResponse.photos
            val endOfPaginationReached = photos.isEmpty()
            photosDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    photosDatabase.pageKeysDao().clearPageKeys()
                    photosDatabase.photosDao().clearPhotos()
                }
                val prevKey = if (page == PHOTOS_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = photos.map {
                    PageKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                photosDatabase.pageKeysDao().insertAll(keys)
                photosDatabase.photosDao().insertAll(photos.asDatabaseModel(apiQuery))
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, DBPhoto>): PageKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { photo ->
                // Get the remote keys of the last item retrieved
                photosDatabase.pageKeysDao().pageKeysById(photo.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, DBPhoto>): PageKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { photo ->
                // Get the remote keys of the first items retrieved
                photosDatabase.pageKeysDao().pageKeysById(photo.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, DBPhoto>
    ): PageKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { photoId ->
                photosDatabase.pageKeysDao().pageKeysById(photoId)
            }
        }
    }
}