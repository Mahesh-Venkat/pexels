package com.maheshvenkat.pexels.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "page_keys")
data class PageKeys(
    @PrimaryKey
    val id: Long,
    val prevKey: Int?,
    val nextKey: Int?
)