package com.example.unfilterednoise.views.mainbottomnavigation.communityviews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.unfilterednoise.databinding.ActivityCommuntiyReportPostsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityReportPostsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommuntiyReportPostsBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCommuntiyReportPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}