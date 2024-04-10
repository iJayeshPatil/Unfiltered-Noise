package com.example.unfilterednoise.views.onboardingviews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.unfilterednoise.databinding.ActivityMainBinding
import com.example.unfilterednoise.utils.networks.ConnectivityObserver
import com.example.unfilterednoise.utils.networks.NetworkConnectivityObserver
import com.example.unfilterednoise.views.ConnectionLostActivity
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {

     private lateinit var connectivityObserver: ConnectivityObserver

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        connectivityObserver.observe().onEach {

            if (it == ConnectivityObserver.Status.Unavailable){

                Log.d("no net", "onCreate: net")

                startActivity(Intent(applicationContext, ConnectionLostActivity::class.java))

            }
            else{
                onResume()
            }


        }.launchIn(lifecycleScope)

        binding= ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)




        firebaseAuth= FirebaseAuth.getInstance()

        binding.loginB.setOnClickListener {

            startActivity(Intent(applicationContext, LoginActivity::class.java))

        }

        binding.registerB.setOnClickListener {

            startActivity(Intent(applicationContext, SignUpActivity::class.java))

        }

        checkUserLogin()
    }

    private fun checkUserLogin(){


        if(firebaseAuth.currentUser != null){
            val uid = firebaseAuth.currentUser?.uid.toString()

            getUserName(uid){userName->

                if (userName == ""){

                    startActivity(Intent(applicationContext,ProfileSignUpActivity::class.java))
                    finish()
                }
                else{

                    startActivity(Intent(applicationContext,MainNavActivity::class.java))
                    finish()

                }

            }
        }
    }

    private fun getUserName(uid: String, callback: (String) -> Unit) {
        firestore = FirebaseFirestore.getInstance()
        firestore.collection("UserDetails").whereEqualTo("UserUID", uid)
            .get().addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    val userName = doc.getString("UserName").toString()

                    callback(userName)
                }
            }
    }
}