package com.example.newsapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.newsapp.models.Article
import com.example.newsapp.repositories.ArticlesDataSource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class ArticleListViewModel : ViewModel() {

    private val articleList: BehaviorSubject<List<Article>> = BehaviorSubject.create()

    private val querySubject: Subject<String> = PublishSubject.create()

    private val orderSubject: Subject<String> = PublishSubject.create()

    private val loadArticlesSubject: Subject<Boolean> = PublishSubject.create()

    private var articlesToShow: Int = 10
    private var pageNumber: Int = 1


    init {
        Observables.combineLatest(
            querySubject,
            orderSubject,
            loadArticlesSubject
        ) { q, o, _ ->
            Pair(q, o)
        }.debounce(200, TimeUnit.MILLISECONDS).switchMap {
            ArticlesDataSource.loadArticlesRxStyle(this.pageNumber, this.articlesToShow, it.second, it.first)
        }.distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(articleList)
    }

    fun setQuery(query: String) {
        resetCount()
        querySubject.onNext(query)
    }


    fun setOrder(order: String) {
        orderSubject.onNext(order)
    }

    fun loadMoreArticles(isSearchMode: Boolean = false) {
        if (!isSearchMode) {
            this.articlesToShow += 10
            if (this.articlesToShow > 200) {
                this.pageNumber += 1
                this.articlesToShow = 10
            }
        } else {
            resetCount()
        }
        loadArticlesSubject.onNext(true)
    }

    private fun resetCount() {
        this.pageNumber = 1
        this.articlesToShow = 10
    }

    fun getArticles(): Observable<List<Article>> = articleList
}
