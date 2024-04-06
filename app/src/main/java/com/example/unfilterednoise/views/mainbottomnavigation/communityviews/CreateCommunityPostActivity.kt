package com.example.unfilterednoise.views.mainbottomnavigation.communityviews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.unfilterednoise.databinding.ActivityCreateCommunityPostBinding
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateCommunityPostActivity : AppCompatActivity() {

    private lateinit var userEmail: String
    private lateinit var userName: String
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorageRef: StorageReference
    private var imageUri: Uri? = null

    private lateinit var binding: ActivityCreateCommunityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityCreateCommunityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.createPostAppBar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.createPostAppBar.setNavigationOnClickListener {
            finish()
        }
        binding.createPostAppBar.title="Create Community Post"



        binding.addImageB.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        binding.postB.setOnClickListener {
            postCommunityPost()
        }

        initVars()
    }

    private fun initVars() {
        firestore = FirebaseFirestore.getInstance()
        firebaseStorageRef = FirebaseStorage.getInstance().reference.child("PostsPics")
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {

        imageUri = it
        binding.imageViewPost.visibility= View.VISIBLE
        Picasso.get().load(imageUri).into(binding.imageViewPost)
    }

    private fun postCommunityPost() {

        if(binding.postContent.text.toString().isEmpty()){
            Snackbar.make(
                binding.root,
                "Write some content to your Post",
                Snackbar.ANIMATION_MODE_SLIDE
            ).show()
        }
        else{
            auth = FirebaseAuth.getInstance()
            userEmail = auth.currentUser?.email.toString()
            val currentDate = (SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())).format(Date())

            firestore = FirebaseFirestore.getInstance()
            firestore.collection("UserDetails").whereEqualTo("UserEmail", userEmail).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val doc = documents.documents[0]
                        userName = doc.getString("UserName").toString()
                    }
                }

            binding.progressBarCreatePost.visibility = View.VISIBLE
            if (imageUri != null) {
                firebaseStorageRef = firebaseStorageRef.child(System.currentTimeMillis().toString())
                imageUri?.let {
                    firebaseStorageRef.putFile(it).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseStorageRef.downloadUrl.addOnSuccessListener { uri ->

                                val map = HashMap<String, Any>()
                                map["PostImg"] = uri.toString()
                                map["PostTitle"] = binding.postTitle.text.toString()
                                map["PostContent"] = binding.postContent.text.toString()
                                map["CreatedBy"] = auth.currentUser?.uid.toString()
                                map["CreatedOn"] = currentDate
                                map["PostLikeCount"] = 0
                                map["PostCommentCount"] = 0

                                firestore.collection("CommunityPosts").add(map)
                                    .addOnCompleteListener { firestoreTask ->
                                        if (firestoreTask.isSuccessful) {
                                            Snackbar.make(
                                                binding.root,
                                                "Build Successful!!",
                                                Snackbar.ANIMATION_MODE_SLIDE
                                            ).show()
                                            val mIntent = Intent(
                                                applicationContext,
                                                MainNavActivity                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     ::class.java
                                            )
                                            startActivity(mIntent)
                                            finish()
                                        }
                                        else {
                                            Snackbar.make(
                                                binding.root,
                                                firestoreTask.exception?.message.toString(),
                                                Snackbar.ANIMATION_MODE_SLIDE
                                            ).show()
                                        }
                                        binding.progressBarCreatePost.visibility = View.GONE
                                    }
                            }
                        }
                        else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                            binding.progressBarCreatePost.visibility = View.GONE
                        }
                    }
                }
            }
            else {

                val map = HashMap<String, Any>()
                map["PostContent"] = binding.postContent.text.toString()
                map["PostTitle"] = binding.postTitle.text.toString()
                map["CreatedBy"] = auth.currentUser?.uid.toString()
                map["CreatedOn"] = currentDate
                map["PostImg"] = ""

                firestore.collection("CommunityPosts").add(map)
                    .addOnCompleteListener { firestoreTask ->
                        if (firestoreTask.isSuccessful) {
                            Snackbar.make(
                                binding.root,
                                "Build Successful!!",
                                Snackbar.ANIMATION_MODE_SLIDE
                            ).show()
                            val mIntent = Intent(applicationContext, MainNavActivity::class.java)
                            startActivity(mIntent)
                            finish()

                        }
                        else {
                            Snackbar.make(
                                binding.root,
                                "Build Unsuccessful!!",
                                Snackbar.ANIMATION_MODE_SLIDE
                            ).show()
                        }

                    }
            }

        }

    }
}