package com.maheshvenkat.pexels.network

import com.google.gson.annotations.SerializedName
import com.maheshvenkat.pexels.db.DBPhoto
import com.maheshvenkat.pexels.models.Photo

/**
 * API Data class to hold pexel image responses from search Image API call.
 */
data class SearchApiResponse(
    @SerializedName("page") val page: Int = 0,
    @SerializedName("per_page") val total: Int = 0,
    @SerializedName("photos") val photos: List<PexelPhoto> = emptyList(),
    val nextPage: Int? = null
)


/**
 * API Data class to hold individual pexel image
 */
data class PexelPhoto(
    @field:SerializedName("id") val id: Long,
    @field:SerializedName("url") val url: String,
    @field:SerializedName("photographer") val photographer: String,
    @field:SerializedName("photographer_url") val photographerUrl: String,
    @field:SerializedName("photographer_id") val photographerId: Long,
    @field:SerializedName("avg_color") val color: String,
    @field:SerializedName("src") val photoInfo: PhotoInfo
)

/**
 * API Data class to hold Photo info
 */
data class PhotoInfo(
    @field:SerializedName("original") val original: String,
    @field:SerializedName("large2x") val extraLarge: String,
    @field:SerializedName("large") val large: String,
    @field:SerializedName("medium") val medium: String,
    @field:SerializedName("small") val small: String,
    @field:SerializedName("portrait") val portrait: String,
    @field:SerializedName("landscape") val landscape: String,
    @field:SerializedName("tiny") val tiny: String
)


fun List<PexelPhoto>.asDomainModel(): List<Photo> {
    return map {
        Photo(
            id = it.id,
            originalUrl = it.photoInfo.original,
            smallUrl = it.photoInfo.small,
            tinyUrl = it.photoInfo.tiny,
            portraitUrl = it.photoInfo.portrait,
            photographerName = it.photographer,
            photographerUrl = it.photographerUrl
        )
    }
}

fun List<PexelPhoto>.asDatabaseModel(queryString: String): List<DBPhoto> {
    return map {
        DBPhoto(
            id = it.id,
            originalUrl = it.photoInfo.original,
            smallUrl = it.photoInfo.small,
            tinyUrl = it.photoInfo.tiny,
            portraitUrl = it.photoInfo.portrait,
            photographer = it.photographer,
            photographerUrl = it.photographerUrl,
            searchedString = queryString
        )
    }
}


