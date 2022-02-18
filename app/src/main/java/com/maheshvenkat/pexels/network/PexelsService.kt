package com.maheshvenkat.pexels.network


import com.maheshvenkat.pexels.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Pexels API communication setup via Retrofit.
 */

private const val BASE_URL = "https://api.pexels.com/v1/"

interface PexelsService {
    /**
     * Get Photos from searched text
     */
    @GET("search/")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): SearchApiResponse

    companion object {

        fun create(): PexelsService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder().addInterceptor { chain ->
                val request =
                    chain.request().newBuilder().addHeader("Authorization", BuildConfig.API_KEY)
                        .build()
                chain.proceed(request)
            }.addInterceptor(logger).build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PexelsService::class.java)
        }
    }
}



