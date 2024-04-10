package com.example.unfilterednoise.views.onboardingviews

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.unfilterednoise.AboutUsActivity
import com.example.unfilterednoise.R
import com.example.unfilterednoise.databinding.ActivityProfileSettingsBinding
import com.example.unfilterednoise.views.mainbottomnavigation.MainNavActivity
import com.example.unfilterednoise.views.mainbottomnavigation.feedviews.NewsViewActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private  var isEditUserName = false
    var isFirstChange = true
    private lateinit var imageUri: Uri
    private lateinit var userEmail:String
    private lateinit var userName:String
    private lateinit var userProfileSU:String
    private lateinit var usernameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityProfileSettingsBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth= FirebaseAuth.getInstance()
        firebaseStorage= FirebaseStorage.getInstance()
        firestore= FirebaseFirestore.getInstance()
        storageReference=firebaseStorage.reference

        binding.materialToolbarSettings.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        binding.materialToolbarSettings.setNavigationOnClickListener {
            startActivity(Intent(applicationContext, MainNavActivity::class.java))
        }
        binding.materialToolbarSettings.title="Profile Settings"

        binding.AboutUs.setOnClickListener {

            startActivity(Intent(applicationContext,AboutUsActivity::class.java))

        }


        binding.logOutText.setOnClickListener {


            MaterialAlertDialogBuilder(this)
                .setTitle("logging out")
                .setMessage("Are you sre you want to log out")
                .setIcon(R.drawable.logout_fill0)


                .setNegativeButton("No") { dialog, which ->
                    // Respond to negative button press
                    dialog.cancel()

                }
                .setPositiveButton("Yes") { dialog, which ->
                                Firebase.auth.signOut()
            finish()
            val mIntent = Intent(applicationContext, MainActivity::class.java)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mIntent)
                }
                .show()


//            Firebase.auth.signOut()
//            finish()
//            val mIntent = Intent(applicationContext, MainActivity::class.java)
//            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(mIntent)

        }



        binding.editPFP.setOnClickListener {
            resultLauncher.launch("image/*")
        }
        initVars()


        binding.changePass.setOnClickListener {
            auth.sendPasswordResetEmail(auth.currentUser?.email.toString()).addOnSuccessListener {
                Snackbar.make(
                    binding.root, "Please Check your Inbox",
                    Snackbar.ANIMATION_MODE_SLIDE
                ).show()
            }.addOnFailureListener {
                Snackbar.make(
                    binding.root, "Something Went Wrong",
                    Snackbar.ANIMATION_MODE_SLIDE
                ).show()
            }
        }

        binding.preference.setOnClickListener {
            startActivity(Intent(applicationContext,PreferenceSelectionActivity::class.java))

        }

        binding.RateUs.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=com.nextwave.wcc3")
            )
            startActivity(intent)

        }

        binding.Support.setOnClickListener {
            supportEmail()
        }

        binding.WhatsNew.setOnClickListener {
            startActivity(Intent(applicationContext,NewsViewActivity::class.java))
        }

        userEmail= auth.currentUser?.email.toString()
        binding.userEmailText.setText(userEmail)

        if(userEmail.isNotEmpty()){
            getUserName()
        }

        binding.userEmailText.setOnClickListener {
            Toast.makeText(applicationContext, "Unlocks Soon!", Toast.LENGTH_SHORT).show()
        }



        usernameEditText=binding.userNameText

        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isFirstChange) {
                    isFirstChange = false
                } else {
                    isEditUserName = true
                }
                updateSubmitButtonVisibility()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.saveEdit.setOnClickListener {

            firestore.collection("UserDetails").whereEqualTo("UserEmail",userEmail)
                .get().addOnSuccessListener {
                    for (document in it){
                        val documentRef = document.reference
                        documentRef.update("UserName", binding.userNameText.text.toString()).addOnSuccessListener {
                            Snackbar.make(
                                binding.root, "UserName Updated Successfully",
                                Snackbar.ANIMATION_MODE_SLIDE
                            ).show()
                            getUserName()
                            isEditUserName=false
                            updateSubmitButtonVisibility()
                        }
                    }

                }
        }
    }


    private fun initVars() {
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("UserPfp")
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        if (it != null) {
            imageUri = it
            Picasso.get().load(imageUri).into(binding.setImagePlaceholder)
            saveProfile()
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

        binding.progressBarSSpfp.visibility = View.VISIBLE

        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        storageReference = storageReference.child(System.currentTimeMillis().toString())
        imageUri?.let {
            storageReference.putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageReference.downloadUrl.addOnSuccessListener { uriImg ->

                        val imgURi= uriImg

                        firestore.collection("UserDetails").whereEqualTo("UserUID",
                            auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                                doc->
                            for (document in doc){
                                val documentRef = document.reference
                                documentRef.update("UserProfilePic", imgURi)
                                    .addOnSuccessListener {
                                        binding.progressBarSSpfp.visibility= View.GONE
                                        Snackbar.make(
                                            binding.root, "Profile Picture Updated",
                                            Snackbar.ANIMATION_MODE_SLIDE
                                        ).show()

                                    }.addOnFailureListener {
                                            e->
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




    private fun supportEmail(){
        val recipientEmail = "jayeshpatil87870@gmail.com"
        val subject = "Coders Assemble Android Feedback"
        val body = "Device Information:\n Device: " +
                "Device: ${Build.DEVICE}\n" +
                "Manufacturer: ${Build.MANUFACTURER}\n" +
                "Model: ${Build.MODEL}\n" +
                "Android Version: ${Build.VERSION.RELEASE}\n---- Please keep the above text intact----\n\n"

        val mIntent = Intent(Intent.ACTION_SENDTO)
        mIntent.data = Uri.parse("mailto:") // This ensures only email apps handle the intent
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mIntent.putExtra(Intent.EXTRA_TEXT, body)
        startActivity(mIntent)
    }

    private fun updateSubmitButtonVisibility(){
        if (isEditUserName){
            binding.saveEdit.visibility= View.VISIBLE
        }
        else{
            binding.saveEdit.visibility= View.GONE
        }
    }

    private fun getUserName(){
        firestore= FirebaseFirestore.getInstance()
        firestore.collection("UserDetails").whereEqualTo("UserEmail",userEmail)
            .get().addOnSuccessListener {
                    documents ->
                if(!documents.isEmpty){
                    val doc = documents.documents[0]
                    userName=doc.getString("UserName").toString()
                    binding.userNameText.setText(userName)
                    userProfileSU=doc.getString("UserProfilePic").toString()
                    Picasso.get().load(userProfileSU).into(binding.setImagePlaceholder)
                }
            }
    }

}