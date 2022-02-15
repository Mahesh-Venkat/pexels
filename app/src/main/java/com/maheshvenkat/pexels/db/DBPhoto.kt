package com.maheshvenkat.pexels.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maheshvenkat.pexels.models.Photo


/**
 *  * This class  defines the Room photos table, where the photo [id] is the primary key.
 */
@Entity(tableName = "photos")
data class DBPhoto(
    @PrimaryKey val id: Long,
    val originalUrl: String,
    val photographer: String,
    val photographerUrl: String,
    val smallUrl: String,
    val portraitUrl: String,
    val searchedString: String = "",
)

fun List<DBPhoto>.asDomainModel(): List<Photo> {
    return map {
        Photo(
            id = it.id,
            originalUrl = it.originalUrl,
            smallUrl = it.smallUrl,
            portraitUrl = it.portraitUrl,
        )
    }
}


fun DBPhoto.asDomainModel(): Photo {
    return Photo(
        id = this.id,
        originalUrl = this.originalUrl,
        smallUrl = this.smallUrl,
        portraitUrl = this.portraitUrl,
    )
}
