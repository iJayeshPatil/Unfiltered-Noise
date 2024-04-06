package com.example.unfilterednoise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.utils.n_api.Article
import com.squareup.picasso.Picasso

class LatestNewsAdapter: RecyclerView.Adapter<LatestNewsAdapter.NewsViewHolder>() {

    private var articles: List<Article> = listOf()

    fun setArticles(articles: List<Article>) {
        this.articles = articles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.breaking_news_feed_card, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.headlineFeedCV)
        private val imgView : ImageView = itemView.findViewById(R.id.imgNewsFeed)

        fun bind(article: Article) {
            titleTextView.text = article.title
            Picasso.get().load(article.urlToImage).into(imgView)
        }
    }

}