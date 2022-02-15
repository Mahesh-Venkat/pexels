package com.maheshvenkat.pexels.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DBPhoto::class, PageKeys::class],
    version = 1,
    exportSchema = false
)
abstract class PhotosDatabase : RoomDatabase() {

    abstract fun photosDao(): PhotoDao
    abstract fun pageKeysDao(): PageKeysDao

    companion object {

        @Volatile
        private var INSTANCE: PhotosDatabase? = null

        fun getInstance(context: Context): PhotosDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                PhotosDatabase::class.java, "Pexels.db"
            ).build()
    }
}