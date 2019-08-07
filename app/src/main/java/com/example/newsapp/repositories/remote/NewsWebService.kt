package com.example.newsapp.repositories.remote

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsWebService {

    @GET("/search")
    fun getArticlesWithRx(
        @Query("api-key") key: String,
        @Query("q") queue: String,
        @Query("show-tags") tags: String,
        @Query("order-by") order: String,
        @Query("show-fields") fields: String,
        @Query("page-size") pageSize: Int,
        @Query("page") page: Int
    ): Observable<NewsResponse>

    companion object {
        private val BASE_URL: String
            get() = "http://content.guardianapis.com/"

        fun create(): NewsWebService {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(NewsWebService::class.java)
        }
    }
}