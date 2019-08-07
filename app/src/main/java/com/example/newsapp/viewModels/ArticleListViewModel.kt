package com.example.newsapp.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.newsapp.models.Article
import com.example.newsapp.repositories.ArticlesDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class ArticleListViewModel : ViewModel() {

    val articleList: BehaviorSubject<List<Article>> = BehaviorSubject.create()

    private var articlesToShow: Int = 10
    private var pageNumber: Int = 1

    fun loadArticles(
        orderBy: String = "newest",
        filterBy: String = "",
        isSearchMode: Boolean = false
    ) {
        val articlesNum: Int = if (isSearchMode) 10 else this.articlesToShow
        val page: Int = if (isSearchMode) 1 else this.pageNumber

        val myCompositeDisposable: CompositeDisposable = CompositeDisposable()

        myCompositeDisposable.add(
            ArticlesDataSource.loadArticlesRxStyle(page, articlesNum, orderBy, filterBy)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    articleList.onNext(it)
                },
                    { Log.e("Get Articles Error", " There was an error") },
                    { myCompositeDisposable?.clear() })
        )

        if (!isSearchMode) {
            if (this.articlesToShow == 200) {
                this.pageNumber += 1
                this.articlesToShow = 0
            }
            this.articlesToShow += 10
        }
    }
}
