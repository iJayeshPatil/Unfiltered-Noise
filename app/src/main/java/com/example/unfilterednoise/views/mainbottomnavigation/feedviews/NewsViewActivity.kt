package com.example.unfilterednoise.views.mainbottomnavigation.feedviews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unfilterednoise.R
import com.example.unfilterednoise.databinding.ActivityNewsViewBinding
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity
import com.squareup.picasso.Picasso

class NewsViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsViewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityNewsViewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.materialToolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.materialToolbar.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, MainNavActivity::class.java))
        }

        val nTitle = intent.getStringExtra("title")
        val nImg = intent.getStringExtra("image")
        val nContent = intent.getStringExtra("content").toString()
        val nUrl = intent.getStringExtra("urlToWeb")

        binding.browseNews.setOnClickListener {

            val mIntent = Intent(Intent.ACTION_VIEW, Uri.parse(nUrl))
            startActivity(mIntent)

        }

        binding.newsTitle.text=nTitle
        Picasso.get().load(nImg).into(binding.newsImg)
        binding.newsContent.text=nContent

        binding.textSum.setOnClickListener {

            startActivity(Intent(applicationContext,SummarizeTestActivity::class.java))

        }

        binding.materialSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                val summary = Text2Summary.summarize( nContent , compressionRate = 0.5F)


                binding.newsContent.text=summary

            } else {

                binding.newsContent.text=nContent

            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.posts_u_admin, menu)
        return true
    }
}