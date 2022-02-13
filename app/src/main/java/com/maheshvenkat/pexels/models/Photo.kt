package com.maheshvenkat.pexels.models

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Photo(
    val id: Long,
    val originalUrl: String,
    val smallUrl: String,
    val portraitUrl: String?,
)