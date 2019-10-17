package com.example.newsapp.miscellaneous

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("app:imageUrl")
fun loadImage(view: ImageView, url: String) {

    val context: Context = view.context
    Glide.with(context)
        .load(url)
        //.placeholder(R.drawable.spinner)
        .error(com.example.newsapp.R.drawable.area51gf_meme)
        .into(view)
}