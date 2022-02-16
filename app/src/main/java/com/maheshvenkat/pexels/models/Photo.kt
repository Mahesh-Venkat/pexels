package com.maheshvenkat.pexels.models

import com.maheshvenkat.pexels.ui.photographer.PhotographerInfo

data class Photo(
    val id: Long,
    val originalUrl: String,
    val smallUrl: String,
    val portraitUrl: String,
    val photographerName: String,
    val photographerUrl: String,
)

fun Photo.getPhotographerInfo(): PhotographerInfo {
    return PhotographerInfo(
        name = this.photographerName,
        websiteInfo = this.photographerUrl
    )
}