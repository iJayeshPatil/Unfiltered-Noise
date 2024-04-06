package com.example.unfilterednoise.views.mainbottomnavigation.feedviews

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unfilterednoise.R
import com.example.unfilterednoise.databinding.ActivityNewsViewBinding
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity

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

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.posts_u_admin, menu)
        return true
    }
}