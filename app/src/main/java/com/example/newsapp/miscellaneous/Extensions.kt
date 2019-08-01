package com.example.newsapp.miscellaneous
import android.content.Context
import android.widget.Toast
import com.example.newsapp.models.Article
import java.lang.StringBuilder

fun String.convertToArticle() : Article {
    val arr: List<String> = this.split(',')
    return Article(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5])
}

fun String.covertToArticleList() : List<Article> {
    val articleList: MutableList<Article> = mutableListOf<Article>()
    for (row in this.split('\n')) {
        articleList.add(row.convertToArticle())
    }
    return articleList
}

fun String.doToast(context: Context, isLong: Boolean = false) {
    Toast.makeText(context, this, if(isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun List<Article>.toFileString() : String {
    val articlesList: List<Article> = this
    val sb: StringBuilder = StringBuilder()
    for (index in 0 until articlesList.size) {
        sb.append(articlesList[index].toCommaSeparatedString())
        if(index != articlesList.size - 1)
            sb.append("\n")
    }
    return sb.toString()
}
