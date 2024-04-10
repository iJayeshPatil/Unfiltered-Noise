package com.example.unfilterednoise

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unfilterednoise.databinding.ActivityAboutUsBinding
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity

class AboutUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBarT.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.appBarT.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, MainNavActivity::class.java))
        }

        binding.rights.setOnClickListener {
            val nUrl = "https://opensource.org/license/mit"

            val mIntent = Intent(Intent.ACTION_VIEW, Uri.parse(nUrl))
            startActivity(mIntent)

        }

        binding.terms.setOnClickListener {

            val nUrl = "https://docs.google.com/document/d/1KtI56phs9gwVvrFmfRVC7P994QNGT7OenVaEu60yniU/edit?usp=sharing"

            val mIntent = Intent(Intent.ACTION_VIEW, Uri.parse(nUrl))
            startActivity(mIntent)

        }

    }
}