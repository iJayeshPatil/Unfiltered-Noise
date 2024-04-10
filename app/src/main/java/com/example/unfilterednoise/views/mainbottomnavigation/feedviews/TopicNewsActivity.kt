package com.example.unfilterednoise.views.mainbottomnavigation.feedviews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.adapters.ForYouNewsAdapter
import com.example.unfilterednoise.databinding.ActivityTopicNewsBinding
import com.example.unfilterednoise.utils.NewsApiService
import com.example.unfilterednoise.utils.news_api.Article
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TopicNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopicNewsBinding
    private lateinit var pid: String
    private lateinit var firestore: FirebaseFirestore
    private lateinit var newsApiService: NewsApiService
    private lateinit var recyclerView1: RecyclerView
    private lateinit var adapter1: ForYouNewsAdapter
    private var article :MutableList<Article> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        binding= ActivityTopicNewsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        pid=intent.getStringExtra("topicId").toString()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        newsApiService = retrofit.create(NewsApiService::class.java)


        recyclerView1 = binding.topicNewsRecycler
        recyclerView1.layoutManager = LinearLayoutManager(this)
        adapter1 = ForYouNewsAdapter(applicationContext,article)
        recyclerView1.adapter = adapter1
        fetchNewsData()





    }

    private fun getTopicName(uid: String, onSuccess: (String) -> Unit) {
        firestore=FirebaseFirestore.getInstance()
        firestore.collection("Topics")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val topicName = document.getString("TopicName") ?: ""
                onSuccess(topicName)
            }
    }

    private fun fetchNewsData() {

        getTopicName(pid){
            val spec= it
            val apiKey = "50c6ece1470348e794d680c15137e8ae"


            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = newsApiService.getNewsSpecific(spec, apiKey)
                    withContext(Dispatchers.Main) {
                        adapter1.setArticles(response.articles)

                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error fetching news", e)
                    // Handle error
                }
            }


        }

    }

}