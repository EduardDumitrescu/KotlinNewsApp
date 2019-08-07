package com.example.newsapp.views

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.miscellaneous.ArticlesAdapter
import com.example.newsapp.miscellaneous.BooleanObserver
import com.example.newsapp.miscellaneous.afterTextChanged
import com.example.newsapp.models.Article
import com.example.newsapp.repositories.ArticlesFileRepo
import com.example.newsapp.viewModels.ArticleListViewModel
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Constant value for the articles loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    companion object {
        private const val ARTICLES_LOADER_ID = 1
    }

    private lateinit var disposable: Disposable
    /** Adapter for the list of articles */
    private lateinit var mAdapter: ArticlesAdapter

    private lateinit var mLayoutManager: LinearLayoutManager

    private lateinit var isAtBottom: BooleanObserver

    private val viewModel: ArticleListViewModel by lazy {
        ViewModelProviders.of(this).get(ArticleListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        error_text.isVisible = false

        search_bar.afterTextChanged(viewModel::setQuery)

        viewModel.setOrder("newest")
        viewModel.setQuery("")
        viewModel.loadMoreArticles()

        isAtBottom = BooleanObserver()
        isAtBottom.onValueChanged = { _, newValue ->
            if (newValue) {
                isAtBottom.value = false
                viewModel.loadMoreArticles()
            }
        }

        loading_spinner.isVisible = false

        mAdapter = ArticlesAdapter(isAtBottom)
        mLayoutManager = LinearLayoutManager(applicationContext)


        disposable = viewModel.getArticles().subscribe {
            mAdapter.setArticles(it)
        }

        articles_list.layoutManager = mLayoutManager
        articles_list.adapter = mAdapter

        // Obtain a reference to the SharedPreferences file for this app
        val prefs: SharedPreferences = getSharedPreferences("filters", 0)

        // PreferenceManager.getDefaultSharedPreferences(this)

        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this)

//        // Get a reference to the ConnectivityManager to check state of network connectivity
//        val cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//
//        // Get details on the currently active default data network
//        var networkInfo: NetworkInfo? = cm?.activeNetworkInfo
//
//        // If there is a network connection, fetch data
//        if (networkInfo != null && networkInfo.isConnectedOrConnecting) {
//            loading_spinner.isVisible = true
//            getArticles()
//
//        } else {
//            // Otherwise, display error. First, hide loading indicator so error message will be visible
//            loading_spinner.isVisible = false
//
//            // Update empty state with "no connection" error message
//            error_text.text = R.string.no_internet_connection.toString()
//
//            //loadFromInternal()
//        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        if (key == getString(R.string.settings_order_by_key)) {
            // Clear the ListView as a new query will be kicked off


            // Hide the empty state text view as the loading indicator will be displayed
//            mEmptyStateTextView.visibility = View.GONE

            // Show the loading indicator while new data is being fetched
            //loading_spinner.isVisible = true

            viewModel.setOrder(prefs.getString("order_by", "newest")!!)
        }
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
        if (isWritePermissionGranted())
            cachedNews = ArticlesFileRepo.getFromExternal(this)!!
        else {
            Toast.makeText(this, " Permission to write on external was not granted ", Toast.LENGTH_LONG).show()
            return
        }
    }

    fun loadFromInternal() {
        val cachedNews: List<Article> = ArticlesFileRepo.getFromInternal(this)

        if (cachedNews.isEmpty())
            Toast.makeText(this, "No local news saved", Toast.LENGTH_LONG).show()
        else {
            Toast.makeText(this, "Showing offline news", Toast.LENGTH_LONG).show()
            mAdapter.setArticles(cachedNews)
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
        articles_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                if (totalItemCount == mLayoutManager.findLastVisibleItemPosition() + 1) {
                    Toast.makeText(applicationContext, "Has scrolled to bottom", Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }
}
