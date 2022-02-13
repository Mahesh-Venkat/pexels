package com.maheshvenkat.pexels.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Pexels API communication setup via Retrofit.
 */

private const val BASE_URL = "https://api.pexels.com/v1/"

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

interface PexelsService {
    /**
     * Get Photos from searched text
     */
    @GET("search/")
    suspend fun searchRepos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): SearchApiResponse

    companion object {

        fun create(): PexelsService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(PexelsService::class.java)
        }
    }
}



