package com.example.newsapp.repositories

import com.example.newsapp.models.Article
import com.example.newsapp.repositories.remote.NewsWebService
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

object ArticlesDataSource {

    private val newsService by lazy {
        NewsWebService.create()
    }

    fun loadArticlesRxStyle(page: Int = 1, pageSize: Int = 10, orderBy: String = "newest", filterBy: String = "") : Observable<List<Article>> {
        return newsService.getArticlesWithRx("test", filterBy, "contributor", orderBy, "thumbnail", pageSize, page)
                          .debounce(2000, TimeUnit.MILLISECONDS)
                          .switchMap { s -> Observable.just(s.response.articles) }
    }
}