package com.maheshvenkat.pexels.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Photo(
    val id: Long,
    val originalUrl: String,
    val smallUrl: String,
    val tinyUrl: String,
    val portraitUrl: String,
    val photographerName: String,
    val photographerUrl: String,
) : Parcelable
