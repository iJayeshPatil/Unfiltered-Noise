package com.example.unfilterednoise.views.mainbottomnavigation.feedviews

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.adapters.ForYouNewsAdapter
import com.example.unfilterednoise.adapters.LatestNewsAdapter
import com.example.unfilterednoise.databinding.FragmentFeedBinding
import com.example.unfilterednoise.utils.NewsApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FeedFragment : Fragment(R.layout.fragment_feed) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LatestNewsAdapter
    private lateinit var newsApiService: NewsApiService

    private lateinit var recyclerView1: RecyclerView
    private lateinit var adapter1: ForYouNewsAdapter


    private lateinit var binding: FragmentFeedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentFeedBinding.bind(view)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        newsApiService = retrofit.create(NewsApiService::class.java)


        recyclerView = binding.latestNewsFeedRecycler
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        adapter = LatestNewsAdapter()
        recyclerView.adapter = adapter

        recyclerView1 = binding.newsFeedRecycler
        recyclerView1.layoutManager = LinearLayoutManager(requireContext())
        adapter1 = ForYouNewsAdapter()
        recyclerView1.adapter = adapter1


        fetchNewsData()


    }

    private fun fetchNewsData() {
        val apiKey = "50c6ece1470348e794d680c15137e8ae"
        val sources = "bbc-news"
        val page = 1


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = newsApiService.getNews(sources, page, apiKey)
                withContext(Dispatchers.Main) {
                    adapter.setArticles(response.articles)
                    adapter1.setArticles(response.articles)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching news", e)
                // Handle error
            }
        }
    }

}