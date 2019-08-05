package com.example.newsapp.views

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.miscellaneous.ArticlesAdapter
import com.example.newsapp.miscellaneous.BooleanObserver
import com.example.newsapp.models.Article
import com.example.newsapp.repositories.ArticlesFileRepo
import com.example.newsapp.viewModels.ArticleListViewModel

class MainActivity : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Constant value for the articles loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    companion object {
        private const val ARTICLES_LOADER_ID = 1
    }

    /** Adapter for the list of articles */
    private lateinit var mAdapter: ArticlesAdapter

    /** TextView that is displayed when the list is empty  */
    private lateinit var mEmptyStateTextView: TextView

    private lateinit var loadingSpinner: View

    private lateinit var articlesRecycleView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager

    private lateinit var isAtBottom: BooleanObserver

    private val viewModel: ArticleListViewModel by lazy {
        ViewModelProviders.of(this).get(ArticleListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isAtBottom = BooleanObserver()
        isAtBottom.onValueChanged = { oldValue, newValue ->
            if (newValue) {
                isAtBottom.value = false
                getArticles()
                //Toast.makeText(this, "It's at bottom", Toast.LENGTH_SHORT).show()
            }
        }

        loadingSpinner = findViewById(R.id.loading_spinner)
        //loadingSpinner.visibility = View.GONE

        articlesRecycleView = findViewById(R.id.list)

        //articlesRecycleView.itemAnimator = SlideInLeftAnimator()

        //setRecyclerViewScrollListener()

        //mEmptyStateTextView = findViewById(R.id.empty_view)
        //articlesRecycleView.empty_view = mEmptyStateTextView

        mAdapter = ArticlesAdapter(viewModel.getArticles().value!!, isAtBottom)
        mLayoutManager = LinearLayoutManager(applicationContext)
        articlesRecycleView.layoutManager = mLayoutManager

        //articlesRecycleView.adapter = AlphaInAnimationAdapter(mAdapter)
        articlesRecycleView.adapter = mAdapter

        // Obtain a reference to the SharedPreferences file for this app
        val prefs: SharedPreferences = getSharedPreferences("filters", 0)

        // PreferenceManager.getDefaultSharedPreferences(this)

        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this)

        // Get a reference to the ConnectivityManager to check state of network connectivity
        val cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Get details on the currently active default data network
        var networkInfo: NetworkInfo? = cm?.activeNetworkInfo

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {

            getArticles()

        } else {
            // Otherwise, display error. First, hide loading indicator so error message will be visible
            loadingSpinner.visibility = View.GONE

            // Update empty state with "no connection" error message
            //mEmptyStateTextView.setText(R.string.no_internet_connection)

            loadFromInternal()
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        if (key == getString(R.string.settings_filter_by_key) || key == getString(R.string.settings_order_by_key)) {
            // Clear the ListView as a new query will be kicked off
            mAdapter.clearData()

            // Hide the empty state text view as the loading indicator will be displayed
//            mEmptyStateTextView.visibility = View.GONE

            // Show the loading indicator while new data is being fetched
            loadingSpinner.visibility = View.VISIBLE

            getArticles()
        }
    }

    private fun getArticles() {
        val sharedPrefs: SharedPreferences = getSharedPreferences("filters", 0)

        //PreferenceManager.getDefaultSharedPreferences(this)

        val filterBy: String? = sharedPrefs.getString(
            getString(R.string.settings_filter_by_key),
            ""
        )

        val orderBy: String? = sharedPrefs.getString(
            getString(R.string.settings_order_by_key),
            getString(R.string.settings_order_by_default)
        )

        viewModel.loadArticles(orderBy!!, filterBy!!)
        viewModel.getArticles().observe(this, Observer {
            val articles: List<Article> = it

            if (articles.isNotEmpty()) {
                //this.runOnUiThread { mAdapter.notifyDataSetChanged() }
                /* val lel = Thread(Runnable {mAdapter.notifyDataSetChanged()})
                 lel.run()*/
                //lel.start()
                //Runnable { mAdapter.notifyDataSetChanged() }
                mAdapter.addItems(articles)
                val h = this.window.decorView.handler
                h.post {
                    mAdapter.notifyDataSetChanged()
                }
            }

            loadingSpinner.visibility = View.GONE
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.action_settings) {
            val settingsIntent: Intent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadFromExternal() {
        lateinit var cachedNews: List<Article>
        if(isWritePermissionGranted())
            cachedNews = ArticlesFileRepo.getFromExternal(this)!!
        else {
            Toast.makeText(this, " Permission to write on external was not granted ", Toast.LENGTH_LONG).show()
            return
        }
    }

    fun loadFromInternal() {
        val cachedNews: List<Article> = ArticlesFileRepo.getFromInternal(this)

        if (cachedNews.isEmpty())
            Toast.makeText(this,"No local news saved", Toast.LENGTH_LONG).show()
        else {
            Toast.makeText(this,"Showing offline news", Toast.LENGTH_LONG).show()
            mAdapter.addItems(cachedNews)
        }
    }

    private fun showPermissionRationale(): Boolean {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
            return true
        return false
    }

    private fun requestExternalWritePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun isWritePermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        )
            return false
        return true
    }

    private fun setRecyclerViewScrollListener() {
        articlesRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                if (totalItemCount == mLayoutManager.findLastVisibleItemPosition() + 1) {
                    Toast.makeText(applicationContext, "Has scrolled to bottom", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
