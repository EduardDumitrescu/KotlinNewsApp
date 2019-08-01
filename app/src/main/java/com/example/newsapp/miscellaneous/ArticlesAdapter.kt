package com.example.newsapp.miscellaneous

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.newsapp.R
import com.example.newsapp.models.Article
import kotlinx.android.synthetic.main.article.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ArticlesAdapter(articleList: List<Article>, private val isAtBottom: BooleanObserver? = null) :
    RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {

    companion object {
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        private val outputDateFormat = SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.US)
    }

    var articlesList: MutableList<Article>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView: View = LayoutInflater.from(parent.context).inflate(R.layout.article, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return articlesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == articlesList.size - 1)
            isAtBottom?.value = true

        val article: Article = articlesList[position]
        holder.bindArticle(article)
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private var article: Article? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val context: Context = itemView.context
            // Convert the String URL into a URI object (to pass into the Intent constructor)
            var articleUri: Uri? = null
            articleUri = Uri.parse(this.article?.url)


            // Create a new intent to view the news story URI
            val websiteIntent: Intent = Intent(Intent.ACTION_VIEW, articleUri)

            // Send the intent to launch a new activity
            context.startActivity(websiteIntent)
        }

        fun bindArticle(article: Article) {
            this.article = article

            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            Glide.with(itemView)
                .load(article.imgUrl)
                //.placeholder(R.drawable.spinner)
                .error(R.drawable.area51gf_meme)
                .into(view.thumbnail)
            //holder.thumbnailImageView.setImageBitmap(article.imgBitmap)

            // Display the title of the current article in that TextView
            view.title.text = article.title
            //Display the section of the current article in that TextView
            view.section.text = article.section
            //Display the author of the current article in that TextView
            view.author.text = article.author

            //Parse the String which holds the date and time (original "2018-04-15T08:35:35Z" to
            //"2018-04-15" and "08:35:35", and from "08:35:35" to "08:35")
            val originalDate: String = article.publicationDate
            try {
                val d: Date? = inputDateFormat.parse(originalDate)
                val formattedDateTime: String = outputDateFormat.format(d!!)

                // Display the date of the current news story in that TextView
                view.date.text = formattedDateTime
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

    }

    init {
        articlesList = articleList.toMutableList()
    }

    fun clearData() {
        for (index in (articlesList.size - 1) downTo 0) {
            articlesList.removeAt(index)
            //notifyItemRemoved(index)
        }
        notifyDataSetChanged()
    }

    fun addItems(articles: List<Article>) {
        for (index in 0 until articles.size) {
            if (articles[index] in articlesList)
                continue
            articlesList.add(articles[index])
            //notifyItemInserted(index)
        }
        notifyDataSetChanged()
    }
}