package com.example.unfilterednoise.views.mainbottomnavigation.communityviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.adapters.CommunityPostAdapter
import com.example.unfilterednoise.databinding.FragmentCommunityBinding
import com.example.unfilterednoise.datamodels.CommunityPost
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class CommunityFragment : Fragment(R.layout.fragment_community) {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var postsAdapter : CommunityPostAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var postCount:String
    private val posts :MutableList<CommunityPost> = mutableListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCommunityBinding.bind(view)
        binding.progressBarFeed.visibility= View.VISIBLE
        recyclerView = binding.feedRecycler
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postsAdapter = CommunityPostAdapter(posts) { post ->
            val mIntent = Intent(context, CommunityPostViewActivity::class.java)
            mIntent.putExtra("postId", post.id)
            startActivity(mIntent)
        }
        recyclerView.adapter=postsAdapter
        loadPosts()

        binding.createPostB.setOnClickListener {
            createFeedPost()
        }

    }

    private fun loadPosts(){


        firestore = FirebaseFirestore.getInstance()

        firestore.collection("CommunityPosts").get().addOnSuccessListener {
                documents->
            for(document in documents){

                postCount = documents.count().toString()
                val postImg = document.getString("PostImg") ?: ""
                val postUserUid = document.getString("CreatedBy") ?: ""
                val postContent = document.getString("PostContent") ?: ""
                val postTitle = document.getString("PostTitle") ?: ""

                getUser(postUserUid) { userName, userIcon, userHeading ->

                    val post = CommunityPost(
                        document.id,
                        userName,
                        userHeading,
                        postTitle,
                        postContent,
                        postImg,
                        userIcon
                    )
                    posts.add(post)
                    postsAdapter.notifyItemRangeChanged(0, postCount.toInt())
                }

            }
            binding.progressBarFeed.visibility= View.GONE

        }.addOnFailureListener{
            it.message?.let { it1 ->
                Snackbar.make(
                    binding.root,
                    it1, Snackbar.ANIMATION_MODE_SLIDE
                ).show()
            }
        }

    }

    private fun getUser(uid: String, callback: (String, String, String) -> Unit) {
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("UserDetails").whereEqualTo("UserUID", uid)
            .get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    val userName = doc.getString("UserName").toString()
                    val userIcon = doc.getString("UserProfilePic").toString()
                    val userHeading = doc.getString("UserRole").toString()
                    callback(userName, userIcon, userHeading)
                }
            }
    }

    private fun createFeedPost() {

        startActivity(Intent(context, CreateCommunityPostActivity::class.java))

    }


}