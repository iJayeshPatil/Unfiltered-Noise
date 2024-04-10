package com.example.unfilterednoise.views.onboardingviews

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.unfilterednoise.R
import com.example.unfilterednoise.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userEmail:String
    private lateinit var userName:String
    private lateinit var userRole:String
    private lateinit var imgViewPlaceholder: ImageView
    private lateinit var imgURL:String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        userEmail = auth.currentUser?.email.toString()
        firestore = FirebaseFirestore.getInstance()

        imgViewPlaceholder = binding.imgViewP


        binding.toSettingPage.setOnClickListener {
            startActivity(Intent(context, ProfileSettingsActivity::class.java))
            activity?.finish()
        }





        if (userEmail.isNotEmpty()) {
            firestore.collection("UserDetails")
                .whereEqualTo("UserEmail", userEmail).get().addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val doc = documents.documents[0]
                        userName = doc.getString("UserName").toString()
                        userRole = doc.getString("UserRole").toString()
                        imgURL = doc.getString("UserProfilePic").toString()
                        Picasso.get().load(imgURL).into(binding.imgViewP)
                        Log.d("imgurl", "onViewCreated: $imgURL")
                        if (userName.isNotEmpty()) {
                            binding.userNameP.text = userName
                            binding.userRole.text = userRole
                            countUserPosts(auth.currentUser?.uid.toString())
                            countUserComments(auth.currentUser?.uid.toString())

                            if (userRole == "Admin") {
                                binding.userRole.setTextColor(Color.parseColor("#226CFF"))
                            } else {
                                binding.userRole.setTextColor(Color.parseColor("#FFB517"))
                            }
                        }
                    }
                }

        }

    }

    private fun countUserPosts(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("CommunityPosts")
            .whereEqualTo("CreatedBy", userId)
            .get()
            .addOnSuccessListener { documents ->
                val numberOfPosts = documents.size()
                binding.postCountP.text=numberOfPosts.toString()

            }
            .addOnFailureListener { exception ->
                Log.d("FirestoreDemo", "Error getting documents: ", exception)
            }
    }

    private fun countUserComments(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val postsRef = db.collection("CommunityPosts")
        var totalComments = 0

        postsRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.collection("Comments")
                    .whereEqualTo("UserId", userId)
                    .get()
                    .addOnSuccessListener { comments ->
                        totalComments += comments.size()
                        binding.commentCountP.text=totalComments.toString()
                        Log.d("CommentsCount", "Total comments so far: $totalComments")
                    }
            }
        }
    }


}