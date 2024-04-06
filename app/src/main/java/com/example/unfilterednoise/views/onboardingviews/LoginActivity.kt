package com.example.unfilterednoise.views.onboardingviews

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unfilterednoise.databinding.ActivityLoginBinding
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userEmail:String
    private lateinit var userPass:String
    private lateinit var firestore: FirebaseFirestore
    private lateinit var userName:String


    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.forgotPassT.setOnClickListener {
            forgotPassUser()
        }

        binding.dText.setOnClickListener {

            startActivity(Intent(applicationContext, SignUpActivity::class.java))
            finish()

        }

        binding.signInB.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser(){
        userEmail=binding.userEmailLogin.text.toString()
        userPass=binding.userPassLogin.text.toString()
        firebaseAuth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()

        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            binding.emailInputLayout.error="Please Enter Valid Email"
        }
        else{
            firebaseAuth.signInWithEmailAndPassword(userEmail,userPass).addOnSuccessListener {
                firestore.collection("UserDetails")
                    .whereEqualTo("UserEmail",userEmail).get().addOnSuccessListener {
                            documents ->
                        if(!documents.isEmpty){
                            val doc = documents.documents[0]
                            userName=doc.getString("UserName").toString()
                            val mIntent = Intent(applicationContext, MainNavActivity::class.java)
                            startActivity(mIntent)
                            finish()
                        }
                    }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun forgotPassUser(){
        userEmail=binding.userEmailLogin.text.toString()
        if (Patterns.EMAIL_ADDRESS.matcher(userEmail).matches() && userEmail.isNotEmpty()){
            firebaseAuth= FirebaseAuth.getInstance()

            firebaseAuth.sendPasswordResetEmail(userEmail).addOnSuccessListener {
                Snackbar.make(
                    binding.root, "Please Check your Inbox",
                    Snackbar.ANIMATION_MODE_SLIDE
                ).show()


            }.addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    it.message?.replaceAfter("this ", "email"),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        else if(userEmail.isEmpty()){
            binding.emailInputLayout.error="Please Enter Valid Email"
        }
        else{
            Toast.makeText(applicationContext, "Something Went Wrong", Toast.LENGTH_SHORT).show()
        }
    }



}