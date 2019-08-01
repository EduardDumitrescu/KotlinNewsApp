package com.example.newsapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapp.models.Article
import com.example.newsapp.repositories.ArticlesDataSource

class ArticleListViewModel : ViewModel() {

    private var articleList: MutableLiveData<MutableList<Article>> = MutableLiveData<MutableList<Article>>()
    var articlesToShow: Int = 10
    var pageNumber: Int = 1

    init {
        this.articleList.value = mutableListOf<Article>()
    }

    fun addArticles(articles: List<Article>) {

        val currentList: MutableList<Article> = this.articleList.value!!

        for (article in articles) {
            if(article in currentList)
                continue
            currentList.add(article)
        }
        this.articleList.value = currentList
    }

    fun clearArticles() {
        this.articleList.value?.clear()
    }

    fun getArticles() = this.articleList as LiveData<List<Article>>

    fun getFromServer(page: Int, pageSize: Int = 10, orderBy: String = "newest", filterBy: String = "") {
        this.articleList = ArticlesDataSource.getArticlesResponse(page, pageSize, orderBy, filterBy)
    }
}
