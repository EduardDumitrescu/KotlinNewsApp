package com.example.newsapp.repositories

import androidx.lifecycle.MutableLiveData
import com.example.newsapp.models.Article
import com.example.newsapp.repositories.remote.NewsResponse
import com.example.newsapp.repositories.remote.NewsWebService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ArticlesDataSource {

    private val newsService by lazy {
        NewsWebService.create()
    }


    val articlesList: MutableLiveData<MutableList<Article>> by lazy {
        val list = MutableLiveData<MutableList<Article>>()
        list.value = mutableListOf<Article>()
        list
    }


    fun loadArticlesResponse(page: Int = 1, pageSize: Int = 10, orderBy: String = "newest", filterBy: String = "") {

        newsService.getArticles("test", filterBy, "contributor", orderBy, "thumbnail", pageSize, page)
            .enqueue(object : Callback<NewsResponse> {
                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    //articlesList.value = null
                }

                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    val newsResponse: NewsResponse = response.body()!!
                    articlesList.value = newsResponse.response.articles.toMutableList()
                }
            })
    }
}