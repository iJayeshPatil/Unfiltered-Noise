package com.example.unfilterednoise.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.utils.news_api.Article
import com.example.unfilterednoise.views.mainbottomnavigation.feedviews.NewsViewActivity
import com.squareup.picasso.Picasso

class ForYouNewsAdapter(private val context: Context, private var articles: List<Article>) :
    RecyclerView.Adapter<ForYouNewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.newsTitleFY)
        private val imgView : ImageView = itemView.findViewById(R.id.newsIconImg)

        fun bind(article: Article) {
            titleTextView.text = article.title
            Picasso.get().load(article.urlToImage).into(imgView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.for_you_news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)

        holder.itemView.setOnClickListener {
            // Handle click event
            val intent = Intent(context, NewsViewActivity::class.java).apply {
                putExtra("title", article.title)
                putExtra("image", article.urlToImage)
                putExtra("content", article.description)
                putExtra("urlToWeb",article.url)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun setArticles(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}