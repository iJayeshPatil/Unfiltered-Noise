package com.example.unfilterednoise.views.mainbottomnavigation.feedviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unfilterednoise.R
import com.example.unfilterednoise.databinding.ActivitySummarizeTestBinding
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity

class SummarizeTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySummarizeTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivitySummarizeTestBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.appBarT.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.appBarT.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, MainNavActivity::class.java))
        }

        binding.summarizeTexts.setOnClickListener {

            if(binding.BoxText.text == null){

                Toast.makeText(applicationContext, "Enter Text", Toast.LENGTH_SHORT).show()

            }
            else{

                val text = binding.BoxText.text.toString()
                val summary = Text2Summary.summarize( text , compressionRate = 0.5F)
                binding.cardSummarize.visibility=View.VISIBLE
                binding.textToSum.text=summary

            }

        }

    }
}