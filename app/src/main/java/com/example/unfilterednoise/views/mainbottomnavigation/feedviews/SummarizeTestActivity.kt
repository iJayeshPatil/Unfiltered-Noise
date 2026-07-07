package com.example.unfilterednoise.views.mainbottomnavigation.feedviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.unfilterednoise.R
import com.example.unfilterednoise.databinding.ActivitySummarizeTestBinding
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity
import io.shubham0204.text2summary.Text2Summary
import kotlinx.coroutines.launch

class SummarizeTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySummarizeTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySummarizeTestBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.appBarT.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.appBarT.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, MainNavActivity::class.java))
        }

        binding.summarizeTexts.setOnClickListener {
            val text = binding.BoxText.text.toString().trim()

            // Better check for empty text inputs
            if (text.isEmpty()) {
                Toast.makeText(applicationContext, "Enter Text", Toast.LENGTH_SHORT).show()
            } else {
                // Wrap the suspend function in lifecycleScope.launch
                lifecycleScope.launch {
                    try {
                        val summary = Text2Summary.summarize(text, compressionRate = 0.5F)

                        binding.cardSummarize.visibility = View.VISIBLE
                        binding.textToSum.text = summary
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(applicationContext, "Error summarizing text", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}