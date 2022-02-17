package com.maheshvenkat.pexels.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<DBPhoto>)

    @Query("SELECT * FROM photos")
    fun photos(): PagingSource<Int, DBPhoto>

    @Query("DELETE FROM photos")
    suspend fun clearPhotos()
}