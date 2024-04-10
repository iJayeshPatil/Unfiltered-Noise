package com.example.unfilterednoise.views.onboardingviews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.adapters.PreferenceAdapter
import com.example.unfilterednoise.adapters.TopicsAdapter
import com.example.unfilterednoise.databinding.ActivityPreferenceSelectionBinding
import com.example.unfilterednoise.datamodels.TopicPref
import com.example.unfilterednoise.datamodels.Topics
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity
import com.example.unfilterednoise.views.mainbottomnavigation.feedviews.TopicNewsActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class PreferenceSelectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreferenceSelectionBinding

    private lateinit var topicCount:  String
    private lateinit var recyclerView: RecyclerView
    private lateinit var preferenceAdapter: PreferenceAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userEmail:String
    private lateinit var userRole:String
    private val prefs: MutableList<TopicPref> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityPreferenceSelectionBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()
        userEmail= auth.currentUser?.email.toString()

        recyclerView = binding.recyclerPref
        recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        preferenceAdapter = PreferenceAdapter(prefs) {

        }
        recyclerView.adapter = preferenceAdapter
        loadCommunitiesFromFirestore()

        binding.finishB.setOnClickListener {

            saveSelectedPreferences()
            startActivity(Intent(applicationContext,MainNavActivity::class.java))

        }




    }

    private fun saveSelectedPreferences() {
        val selectedTopics = prefs.filter { it.isSelected }.map { it.id }
        val userId = auth.currentUser?.uid ?: return

        val query = FirebaseFirestore.getInstance().collection("UserDetails").whereEqualTo("UserUID", userId)
        query.get().addOnSuccessListener { querySnapshot ->
            for (document in querySnapshot) {
                FirebaseFirestore.getInstance().collection("UserDetails").document(document.id)
                    .update("selectedTopics", selectedTopics)
                    .addOnSuccessListener {
                        Log.d("PreferenceSelection", "Successfully updated user preferences for document ID: ${document.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("PreferenceSelection", "Error updating user preferences for document ID: ${document.id}", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.e("PreferenceSelection", "Error finding user document to update preferences.", e)
        }
    }


    private fun loadCommunitiesFromFirestore() {
        try {
            val db = FirebaseFirestore.getInstance()
            val communitiesCollection = db.collection("Topics")

            communitiesCollection.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        topicCount= documents.count().toString()

                        val name = document.getString("TopicName") ?: ""
                        val iconUrl = document.getString("TopicIcon") ?: ""

                        val topic = TopicPref(document.id, name, iconUrl)
                        prefs.add(topic)
                        preferenceAdapter.notifyItemRangeChanged(0, topicCount.toInt())

                    }

//                    binding.progressBarForumLoad.visibility= View.GONE

                }
                .addOnFailureListener { exception ->
                    Log.e("TAG", "Error getting documents: ", exception)
                    Snackbar.make(binding.root,"Unable to get communities, try again later",
                        Snackbar.ANIMATION_MODE_SLIDE).show()
                }

        }
        catch (e: Exception){
            Toast.makeText(applicationContext, "Something Went Wrong", Toast.LENGTH_SHORT).show()
        }
    }
}