package com.example.newsapp.miscellaneous

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.newsapp.BR
import com.example.newsapp.models.Article
import com.example.newsapp.viewModels.ArticleListViewModel
import com.example.newsapp.views.MainActivity
import java.text.SimpleDateFormat
import java.util.*


class ArticlesAdapter(private val isAtBottom: BooleanObserver? = null) :
    RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {

    companion object {
        private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        private val outputDateFormat = SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.US)
    }

    private var articlesList: List<Article> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //val inflatedView: View = LayoutInflater.from(parent.context).inflate(R.layout.article, parent, false)


        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            layoutInflater,
            com.example.newsapp.R.layout.article,
            parent,
            false
        )

        //return ViewHolder(inflatedView)
        return ViewHolder(binding)
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

    class ViewHolder(val binding: ViewDataBinding /*private val view: View*/) :
        RecyclerView.ViewHolder(binding.root/*view*/) /*, View.OnClickListener*/ {
        private var article: Article? = null

//        init {
//            binding.root.setOnClickListener(this)
//        }

//        override fun onClick(p0: View?) {
//            val context: Context = itemView.context
//            // Convert the String URL into a URI object (to pass into the Intent constructor)
//            var articleUri: Uri? = null
//            articleUri = Uri.parse(this.article?.url)
//
//
//            // Create a new intent to view the news story URI
//            val websiteIntent: Intent = Intent(Intent.ACTION_VIEW, articleUri)
//
//            // Send the intent to launch a new activity
//            context.startActivity(websiteIntent)
//        }

        fun bindArticle(article: Article) {
            this.article = article

            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            val vm = ViewModelProviders.of(binding.root.context as MainActivity)
                .get(ArticleListViewModel::class.java)

//            Glide.with(itemView)
//                .load(article.imgUrl)
//                //.placeholder(R.drawable.spinner)
//                .error(com.example.newsapp.R.drawable.area51gf_meme)
//                .into(binding.root.thumbnail)

            binding.setVariable(BR.article, article)
            binding.setVariable(BR.mainViewModel, vm)
            binding.executePendingBindings()

//            Glide.with(itemView)
//                .load(article.imgUrl)
//                //.placeholder(R.drawable.spinner)
//                .error(R.drawable.area51gf_meme)
//                .into(view.thumbnail)
//            //holder.thumbnailImageView.setImageBitmap(article.imgBitmap)
//
//            // Display the title of the current article in that TextView
//            view.title.text = article.title
//            //Display the section of the current article in that TextView
//            view.section.text = article.section
//            //Display the author of the current article in that TextView
//            view.author.text = article.author
//
//            //Parse the String which holds the date and time (original "2018-04-15T08:35:35Z" to
//            //"2018-04-15" and "08:35:35", and from "08:35:35" to "08:35")
//            val originalDate: String = article.publicationDate
//            try {
//                val d: Date? = inputDateFormat.parse(originalDate)
//                val formattedDateTime: String = outputDateFormat.format(d!!)
//
//                // Display the date of the current news story in that TextView
//                view.date.text = formattedDateTime
//            } catch (e: ParseException) {
//                e.printStackTrace()
//            }
        }

    }

    fun clearData() {
        articlesList = emptyList()
        notifyDataSetChanged()
    }

    fun setArticles(articles: List<Article>) {
        articlesList = articles

        notifyDataSetChanged()
    }
}