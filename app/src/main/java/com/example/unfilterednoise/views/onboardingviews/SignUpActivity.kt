package com.example.unfilterednoise.views.onboardingviews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unfilterednoise.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var userEmail: String
    private lateinit var userPass: String
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.hText.setOnClickListener {

            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()

        }

        binding.signUpB.setOnClickListener {
            createUser()
        }

    }

    private fun createUser(){

        userEmail=binding.userEmailSignUp.text.toString()
        userPass=binding.userPassSignUp.text.toString()

        if(userEmail.isEmpty()){
            binding.emailInputLayout.error="Enter Valid Email"
        }
        else if(userPass.isEmpty()){
            binding.passInputLayout.error="Enter Valid Email"
        }
        else if(userPass.length < 8 || userPass.isEmpty())
        {
            binding.passInputLayout.error="Minimum 8 Character Password"
        }
        else if(!userPass.matches(".*[A-Z].*".toRegex()))
        {
            binding.passInputLayout.error="Must Contain 1 Upper-case Character"
        }
        else if(!userPass.matches(".*[a-z].*".toRegex()))
        {
            binding.passInputLayout.error="Must Contain 1 Lower-case Character"
        }
        else if(!userPass.matches(".*[@#\$%^&+=].*".toRegex()))
        {
            binding.passInputLayout.error="Must Contain 1 Special Character"
        }
        else if(!userPass.matches(".*[0-9].*".toRegex()))
        {
            binding.passInputLayout.error="Must Contain 1 Numeric Character"
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            binding.emailInputLayout.error="Please Enter Valid Email"
        }
        else{
            try {
                auth= FirebaseAuth.getInstance()
                firestore= FirebaseFirestore.getInstance()

                auth.createUserWithEmailAndPassword(userEmail , userPass).addOnSuccessListener {

                    val userDetails: MutableMap<String, Any> = HashMap()
                    userDetails["UserName"]=""
                    userDetails["UserEmail"]=userEmail
                    userDetails["UserRole"]="Beginner"
                    userDetails["UserProfilePic"]=""
                    userDetails["UserUID"]=auth.currentUser!!.uid
                    firestore.collection("UserDetails").add(userDetails).addOnSuccessListener {
                        val mIntent = Intent(applicationContext, ProfileSignUpActivity::class.java)
                        startActivity(mIntent)
                        finish()
                    }.addOnFailureListener {
                        Log.d("userDetails", it.toString())
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, it.message.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("createUser", it.toString())
                }
            }catch (e:Exception){
                Toast.makeText(
                    applicationContext,
                    "Something went wrong, Try Again",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

    }

}