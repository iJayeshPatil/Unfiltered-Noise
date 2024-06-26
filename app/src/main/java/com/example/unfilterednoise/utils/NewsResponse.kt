package com.example.unfilterednoise.utils

import com.example.unfilterednoise.utils.news_api.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)