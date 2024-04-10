package com.example.unfilterednoise.views.onboardingviews

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.unfilterednoise.databinding.ActivityProfileSignUpBinding
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProfileSignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSignUpBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorageRef: StorageReference
    private lateinit var imageUri : Uri
    private lateinit var userName: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityProfileSignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.userImageSelection.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        initVars()

        binding.completeProfileB.setOnClickListener {

            saveProfile()

        }

    }

    private fun initVars() {
        firestore = FirebaseFirestore.getInstance()
        firebaseStorageRef = FirebaseStorage.getInstance().reference.child("UserPfp")
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        if (it != null) {
            imageUri = it
            Picasso.get().load(imageUri).into(binding.userImageSelected)
        }
        else{
            Toast.makeText(
                applicationContext,
                "Please Select a Profile Picture",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun saveProfile(){

        binding.progressBarpfpS.visibility = View.VISIBLE

        userName=binding.userNameSignUp.text.toString()
        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        firebaseStorageRef = firebaseStorageRef.child(System.currentTimeMillis().toString())

        if (userName.isEmpty()){

            binding.userNameInputLayout.error="Enter an unique user name"

        }
        else{

        imageUri.let {
            firebaseStorageRef.putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseStorageRef.downloadUrl.addOnSuccessListener { uriImg ->

                        firestore.collection("UserDetails").whereEqualTo("UserUID",
                            auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                                doc->
                            for (document in doc){
                                val documentRef = document.reference
                                documentRef.update(mapOf(
                                    "UserProfilePic" to uriImg,
                                    "UserName" to userName

                                ))
                                    .addOnSuccessListener {
                                        binding.progressBarpfpS.visibility= View.GONE
                                        Snackbar.make(
                                            binding.root, "Profile Picture Added Successfully",
                                            Snackbar.ANIMATION_MODE_SLIDE
                                        ).show()
                                        startActivity(
                                            Intent(
                                                applicationContext,
                                                PreferenceSelectionActivity::class.java
                                            )
                                        )

                                        finish()

                                    }.addOnFailureListener { e->
                                        Log.d("saveProfile: ", e.toString())
                                        Toast.makeText(
                                            applicationContext,
                                            "Unable to add Profile Picture",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }

                        }.addOnFailureListener {
                            Toast.makeText(
                                applicationContext,
                                "Unable to find user",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }
    }
}