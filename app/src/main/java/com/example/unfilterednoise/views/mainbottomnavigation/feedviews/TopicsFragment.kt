package com.example.unfilterednoise.views.mainbottomnavigation.feedviews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.adapters.TopicsAdapter
import com.example.unfilterednoise.databinding.FragmentTopicsBinding
import com.example.unfilterednoise.datamodels.Topics
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

class TopicsFragment : Fragment(R.layout.fragment_topics) {

    private lateinit var communityCount:  String
    private lateinit var recyclerView: RecyclerView
    private lateinit var communityAdapter: TopicsAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userEmail:String
    private lateinit var userRole:String
    private val communities: MutableList<Topics> = mutableListOf()

    private lateinit var binding: FragmentTopicsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentTopicsBinding.bind(view)


        binding.createForum.visibility= View.GONE
        binding.progressBarForumLoad.visibility= View.VISIBLE

        auth= FirebaseAuth.getInstance()
        userEmail= auth.currentUser?.email.toString()

        checkRole()




        binding.createForum.setOnClickListener {
            startActivity(Intent(context,TopicsCreateActivity::class.java))
        }

        recyclerView = binding.topicRecycler
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        communityAdapter = TopicsAdapter(communities) { community ->
            val intent = Intent(requireContext(), TopicNewsActivity::class.java)
            intent.putExtra("communityId", community.id)
            startActivity(intent)
        }
        recyclerView.adapter = communityAdapter
        loadCommunitiesFromFirestore()

    }

    private fun loadCommunitiesFromFirestore() {
        try {
            val db = FirebaseFirestore.getInstance()
            val communitiesCollection = db.collection("Topics")

            communitiesCollection.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {

                        communityCount= documents.count().toString()

                        val name = document.getString("TopicName") ?: ""
                        val iconUrl = document.getString("TopicIcon") ?: ""

                        val community = Topics(document.id, name, iconUrl)
                        communities.add(community)
                        communityAdapter.notifyItemRangeChanged(0, communityCount.toInt())

                    }

                    binding.progressBarForumLoad.visibility= View.GONE

                }
                .addOnFailureListener { exception ->
                    Log.e("TAG", "Error getting documents: ", exception)
                    Snackbar.make(binding.root,"Unable to get communities, try again later",
                        Snackbar.ANIMATION_MODE_SLIDE).show()
                }

        }
        catch (e: Exception){
            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkRole() {
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("UserDetails").whereEqualTo("UserEmail", userEmail)
            .get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    userRole = doc.getString("UserRole").toString()
                    if ((userRole == "Admin") or (userRole == "CommunityCreator")) {
                        binding.createForum.visibility = View.VISIBLE
                    } else {
                        binding.createForum.visibility = View.GONE
                    }
                }
            }
    }

}