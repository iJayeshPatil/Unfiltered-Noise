package com.example.unfilterednoise.views.mainbottomnavigation.communityviews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unfilterednoise.R
import com.example.unfilterednoise.databinding.ActivityEditCommunityPostBinding
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class EditCommunityPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCommunityPostBinding

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userUid:String
    private lateinit var pId:String
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseStorageRef : StorageReference
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditCommunityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)


        pId = intent.getStringExtra("postId").toString()

        firestore= FirebaseFirestore.getInstance()

        binding.createPostToolBar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.createPostToolBar.setNavigationOnClickListener {
            finish()
        }
        binding.createPostToolBar.title="Edit Community Post"

        firestore.collection("CommunityPosts").document(pId).get().addOnSuccessListener {
                documents ->

            userUid = documents.getString("CreatedBy").toString()
            val postTitle = documents.getString("PostTitle").toString()
            val postContent = documents.getString("PostContent").toString()
            val postImg = documents.getString("PostImg").toString()


            binding.postTitle.setText(postTitle)
            binding.postContent.setText(postContent)

            if (postImg != ""){
                binding.imagesCreate.visibility= View.VISIBLE
                Picasso.get().load(postImg).into(binding.imagesCreate)
            }
        }

        binding.saveForumPost.setOnClickListener {

            saveUpdate()

        }

        binding.addImage.setOnClickListener {
            resultLauncher.launch("image/*")
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

        if (it != null) {
            imageUri = it
            binding.imagesCreate.visibility= View.VISIBLE
            Picasso.get().load(imageUri).into(binding.imagesCreate)
        }


    }

    private fun saveUpdate(){

        if (binding.postTitle.text.toString().isEmpty()){
            Snackbar.make(binding.root,"Give a nice Title to your Post", Snackbar.ANIMATION_MODE_SLIDE).show()
        }
        else if(binding.postContent.text.toString().isEmpty()){
            Snackbar.make(binding.root,"Write some content to your Post", Snackbar.ANIMATION_MODE_SLIDE).show()
        }
        else{


            auth = FirebaseAuth.getInstance()

            firestore = FirebaseFirestore.getInstance()

            binding.progressBarCreatePost.visibility = View.VISIBLE
            if (imageUri != null) {
                firebaseStorageRef = firebaseStorageRef.child(System.currentTimeMillis().toString())
                imageUri?.let {
                    firebaseStorageRef.putFile(it).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseStorageRef.downloadUrl.addOnSuccessListener { uri ->

                                firestore.collection("CommunityPosts").document(intent.getStringExtra("postId").toString())
                                    .get().addOnSuccessListener {
                                            doc ->
                                        val document = doc.reference
                                        document.update("PostTitle",binding.postTitle.text.toString())
                                        document.update("PostContent",binding.postContent.text.toString())
                                        document.update("PostImg",uri)
                                        Toast.makeText(applicationContext, "Success!", Toast.LENGTH_SHORT).show()
                                        val mIntent = Intent(applicationContext, MainNavActivity::class.java)
                                        mIntent.putExtra("communityId",intent.getStringExtra("communityId").toString())
                                        startActivity(mIntent)
                                        finish()
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

                firestore.collection("CommunityPosts").document(intent.getStringExtra("postId").toString())
                    .get().addOnSuccessListener {
                            doc ->
                        val picUri =doc.getString("PostImg").toString()

                        if (picUri != ""){

                            val document = doc.reference
                            document.update("PostTitle",binding.postTitle.text.toString())
                            document.update("PostContent",binding.postContent.text.toString())
                            document.update("PostImg",picUri)
                            val mIntent = Intent(applicationContext, MainNavActivity::class.java)
                            startActivity(mIntent)
                            finish()

                        }
                        else{

                            val document = doc.reference
                            document.update("PostTitle",binding.postTitle.text.toString())
                            document.update("PostContent",binding.postContent.text.toString())
                            document.update("PostImg","")
                            val mIntent = Intent(applicationContext, MainNavActivity::class.java)
                            mIntent.putExtra("communityId",intent.getStringExtra("communityId").toString())
                            startActivity(mIntent)
                            finish()

                        }


                    }
            }

        }

    }

}
