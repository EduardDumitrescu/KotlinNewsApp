package com.example.newsapp.repositories.remote

import com.example.newsapp.models.Article
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("response")
    @Expose
    val response: Response) {

    data class Response( @SerializedName("currentPage")
                         val currentPage: Int,
                         @SerializedName("results")
                         val articles: List<Article>,
                         @SerializedName("pages")
                         @Expose
                         var pages: Int)
}