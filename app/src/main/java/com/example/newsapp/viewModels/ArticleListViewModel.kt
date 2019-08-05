package com.example.newsapp.viewModels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapp.models.Article
import com.example.newsapp.repositories.ArticlesDataSource

class ArticleListViewModel : ViewModel() {

    private val articleList: MutableLiveData<MutableList<Article>> = ArticlesDataSource.articlesList
    private var articlesToShow: Int = 10
    private var pageNumber: Int = 1

    fun addArticles(articles: List<Article>) {

        val currentList: MutableList<Article> = this.articleList.value?.toMutableList()!!

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

    fun loadArticles(orderBy: String = "newest", filterBy: String = "") {
        ArticlesDataSource.loadArticlesResponse(this.pageNumber, this.articlesToShow, orderBy, filterBy)
        if (this.articlesToShow == 200) {
            this.pageNumber += 1
            this.articlesToShow = 0
        }
        this.articlesToShow += 10
    }
}
