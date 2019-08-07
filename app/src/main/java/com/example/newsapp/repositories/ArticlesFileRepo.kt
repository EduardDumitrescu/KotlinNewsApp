package com.example.newsapp.repositories

import android.content.Context
import android.os.Environment
import com.example.newsapp.models.Article
import com.example.newsapp.miscellaneous.covertToArticleList
import com.example.newsapp.miscellaneous.toFileString
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream

object ArticlesFileRepo {

    private const val internalFileName: String = "ArticlesListInternal"
    private const val externalFileName: String = "ArticlesListExternal"

    fun saveToInternal(context: Context, articles: List<Article>) {
        val fileContents = articles.toFileString()

        context.openFileOutput(internalFileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun getFromInternal(context: Context): List<Article> {
        var articles: MutableList<Article> = mutableListOf<Article>()

        context.openFileInput(internalFileName).use {
            articles.addAll(BufferedReader(it.reader()).readText().covertToArticleList())
        }
        return articles
    }

    fun saveToExternal(context: Context, articles: List<Article>): Boolean {

        //Checking the availability state of the External Storage.
        val state = Environment.getExternalStorageState()
        if (!isExternalStorageWritable()) {

            //If it isn't mounted - we can't write into it.
            return false
        }

        //Create a new file that points to the root directory, with the given name:
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "$externalFileName"
        )

        //This point and below is responsible for the write operation
        var outputStream: FileOutputStream? = null
        try {
            file.createNewFile()
            //second argument of FileOutputStream constructor indicates whether
            //to append or create new file if one exists
            outputStream = FileOutputStream(file, true)

            outputStream!!.write(articles.toFileString().toByteArray())
            outputStream!!.flush()
            outputStream!!.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    fun getFromExternal(context: Context): List<Article>? {
        if (!isExternalStorageReadable())
            return null

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "$externalFileName"
        )
        return BufferedReader(file.reader()).readText().covertToArticleList()
    }

    //Checks if external storage is available for read and write

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    //Checks if external storage is available to at least read

    private fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }
}
