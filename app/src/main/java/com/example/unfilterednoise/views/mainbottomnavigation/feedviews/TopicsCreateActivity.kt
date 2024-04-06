package com.example.unfilterednoise.views.mainbottomnavigation.feedviews

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
import com.example.unfilterednoise.databinding.ActivityTopicsCreateBinding
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

class TopicsCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopicsCreateBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorageRef: StorageReference
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTopicsCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.progressBarForum.visibility = View.GONE

        binding.iconImgPlaceHolder.setOnClickListener {
            resultLauncher.launch("image/*")
        }



        binding.createForumB.setOnClickListener {
            createForum()
        }

        initVars()
    }

    private fun initVars() {
        firestore = FirebaseFirestore.getInstance()
        firebaseStorageRef = FirebaseStorage.getInstance().reference.child("ForumIcons")
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        imageUri = it
        binding.iconImgPlaceHolder.setImageURI(it)
    }


    private fun createForum() {
        auth= FirebaseAuth.getInstance()

        val currentDate = (SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())).format(Date())

        binding.progressBarForum.visibility = View.VISIBLE
        firebaseStorageRef = firebaseStorageRef.child(System.currentTimeMillis().toString())


        if(binding.communityName.text.isNullOrEmpty()){
            binding.forumNameEditLayout.error="Enter Topic Name!"
        }

        else if(imageUri == null){

            Snackbar.make(binding.root,"Select an Icon for the Topic", Snackbar.ANIMATION_MODE_SLIDE).show()

        }
        else {
            imageUri.let {
                if (it != null) {
                    firebaseStorageRef.putFile(it).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseStorageRef.downloadUrl.addOnSuccessListener { uri ->

                                val map = HashMap<String, Any>()
                                map["TopicIcon"] = uri.toString()
                                map["TopicName"] = binding.communityName.text.toString()
                                map["CreatedBy"] = auth.currentUser?.uid.toString()
                                map["CreatedOn"] = currentDate

                                firestore.collection("Topics").add(map)
                                    .addOnCompleteListener { firestoreTask ->
                                        if (firestoreTask.isSuccessful) {
                                            Snackbar.make(
                                                binding.root,
                                                "Topic Created Successfully",
                                                Snackbar.ANIMATION_MODE_SLIDE
                                            ).show()
                                            binding.progressBarForum.visibility = View.GONE

                                            val mIntent = Intent(applicationContext, MainNavActivity::class.java)
                                            startActivity(mIntent)

                                        }

                                        else {
                                            Snackbar.make(
                                                binding.root,
                                                firestoreTask.exception?.message.toString(),
                                                Snackbar.ANIMATION_MODE_SLIDE
                                            ).show()
                                        }
                                        binding.progressBarForum.visibility = View.GONE
                                        Picasso.get().load(it).into(binding.iconImgPlaceHolder)
                                    }
                            }
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                            binding.progressBarForum.visibility = View.GONE
                            Picasso.get().load(it).into(binding.iconImgPlaceHolder)
                        }
                    }
                }
                else{

                }
            }
        }
    }
}
