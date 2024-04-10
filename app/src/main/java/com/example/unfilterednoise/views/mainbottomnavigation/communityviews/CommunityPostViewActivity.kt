package com.example.unfilterednoise.views.mainbottomnavigation.communityviews

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.adapters.CommentsAdapter
import com.example.unfilterednoise.adapters.LikesAdapter
import com.example.unfilterednoise.databinding.ActivityCommunityPostViewBinding
import com.example.unfilterednoise.databinding.BottomSheetPostsLikeBinding
import com.example.unfilterednoise.datamodels.Comments
import com.example.unfilterednoise.datamodels.Like
import com.example.unfilterednoise.datamodels.LikedUsers
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommunityPostViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityPostViewBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userUid:String
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var posterImgIcon: ImageView
    private lateinit var pId:String
    private lateinit var recyclerView: RecyclerView
    private lateinit var likeAdapter : LikesAdapter
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var commentCount:String
    private val likedUser :MutableList<LikedUsers> = mutableListOf()
    private val comments :MutableList<Comments> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityPostViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pId = intent.getStringExtra("postId").toString()

        firestore= FirebaseFirestore.getInstance()

        setSupportActionBar(binding.vPostToolbar)
        binding.vPostToolbar.title=null
        binding.vPostToolbar.setNavigationIcon(com.google.android.material.R.drawable.abc_ic_ab_back_material)
        binding.vPostToolbar.setNavigationOnClickListener {
            finish()
        }

        firestore.collection("CommunityPosts").document(pId).get().addOnSuccessListener {
                documents ->

            userUid = documents.getString("CreatedBy").toString()
            val postTitle = documents.getString("PostTitle").toString()
            val postContent = documents.getString("PostContent").toString()
            val postImg = documents.getString("PostImg").toString()
            val postLike = documents.get("PostLikeCount")
            val postComment = documents.get("PostCommentCount")

            binding.postViewPostTitle.text = postTitle
            binding.postViewPostContent.text = postContent
            binding.likeCountTextV.text=postLike.toString()
            binding.commentCountTextV.text=postComment.toString()

            recyclerView = binding.commentsRecycler
            commentsAdapter= CommentsAdapter(comments,pId)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = commentsAdapter
            loadCommentsFromFirestore()

            if (postImg != ""){
                Picasso.get().load(postImg).into(binding.postViewPostImage)
            }
            else{
                binding.postViewPostImage.visibility= View.GONE
            }

            val postRef = FirebaseFirestore.getInstance().collection("CommunityPosts").document(pId)
            val likeRef = postRef.collection("Likes").document(userUid)
            likeRef.get().addOnSuccessListener {docSnapLike ->
                if (docSnapLike.exists()){
                    binding.postLikeButton.setImageResource(R.drawable.round_favorite_24)
                }

            }


            binding.postLikeButton.setOnClickListener {
                val postRef = FirebaseFirestore.getInstance().collection("CommunityPosts").document(pId)
                val likeRef = postRef.collection("Likes").document(userUid)

                likeRef.get().addOnSuccessListener {docSnap ->
                    if (docSnap.exists()){
                        likeRef.delete()
                        binding.postLikeButton.setImageResource(R.drawable.favorite)
                        postRef.update("PostLikeCount", FieldValue.increment(-1))
                        val likeRun = binding.likeCountTextV.text.toString()
                        binding.likeCountTextV.text= (likeRun.toInt()-1).toString()


                    }
                    else {
                        likeRef.set(Like(pId, userUid))
                        binding.postLikeButton.setImageResource(R.drawable.round_favorite_24)
                        postRef.update("PostLikeCount", FieldValue.increment(1))
                        val likeRun = binding.likeCountTextV.text.toString()
                        binding.likeCountTextV.text= (likeRun.toInt()+1).toString()
                    }

                }

            }



            firestore.collection("UserDetails").whereEqualTo("UserUID",userUid).get().addOnSuccessListener {
                for (document in it){
                    val uName = document.getString("UserName")
                    val pfp = document.getString("UserProfilePic").toString()
                    val role = document.getString("UserRole")

                    Picasso.get().load(pfp).into(binding.postViewPosterImgIcon)

                    binding.postViewPosterName.text=uName

                    if (role == "Admin"){
                        binding.postViewPosterRole.setTextColor(Color.parseColor("#226CFF"))
                    }

                    binding.postViewPosterRole.text=role

                }
            }

        }


        binding.postComment.setOnClickListener {

            val currentDate = (SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())).format(Date())

            val postRef = FirebaseFirestore.getInstance().collection("CommunityPosts").document(pId)


            val commentText = binding.commentBoxText.text.toString()
            if (commentText.isNotEmpty()){

                val map = HashMap<String, Any>()
                map["UserId"]=auth.currentUser?.uid.toString()
                map["Comment"]=commentText
                map["CurrentTime"]=currentDate
                map["CommentUpVoteCount"]=0
                map["CommentDownVoteCount"]=0

                firestore.collection("CommunityPosts")
                    .document(pId).collection("Comments").add(map).addOnSuccessListener {
                        Toast.makeText(applicationContext, "Comment Added!", Toast.LENGTH_SHORT).show()
                        binding.commentBoxText.text?.clear()
                        val commentRun = binding.likeCountTextV.text.toString()
                        binding.commentCountTextV.text= (commentRun.toInt()+1).toString()
                        postRef.update("PostCommentCount", FieldValue.increment(1))

                    }.addOnFailureListener {
                        Toast.makeText(applicationContext, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                    }

            }
            else{

                Toast.makeText(applicationContext, "Comment Can't be empty", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun loadCommentsFromFirestore(){
        val pId = intent.getStringExtra("postId").toString()

        firestore = FirebaseFirestore.getInstance()

        firestore.collection("CommunityPosts")
            .document(pId).collection("Comments").get().addOnSuccessListener {
                    documents->
                for(document in documents){
                    val commentId = document.id.toString()
                    commentCount = documents.count().toString()
                    val commentText = document.getString("Comment") ?: ""
                    val commentUserUid = document.getString("UserId") ?: ""
                    val upVoteCN = document.get("CommentUpVoteCount") ?: ""
                    val downVoteCN = document.get("CommentDownVoteCount") ?: ""

                    getUser(commentUserUid) { userName, userRole, userPfp ->

                        val comment = Comments(commentId,userName, userRole, userPfp, commentText, commentUserUid, upVoteCN.toString(), downVoteCN.toString() )
                        comments.add(comment)
                        commentsAdapter.notifyItemRangeChanged(0, commentCount.toInt())
                    }

                }


            }.addOnFailureListener{
                it.message?.let { it1 ->
                    Snackbar.make(binding.root,
                        it1, Snackbar.ANIMATION_MODE_SLIDE).show()
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
                    val userRole = doc.getString("UserRole").toString()
                    val userPfp = doc.getString("UserProfilePic").toString()
                    callback(userName, userRole, userPfp)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        var role :String
        firestore=FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
        pId = intent.getStringExtra("postId").toString()
        var userUi:String

        try {
            firestore.collection("CommunityPosts").document(pId).get().addOnSuccessListener {
                    documents ->
                userUi = documents.getString("CreatedBy").toString()

                firestore.collection("UserDetails").whereEqualTo("UserUID", auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                    for (document in it){
                        role = document.getString("UserRole").toString()

                        if (role == "Admin" && userUi == auth.currentUser?.uid.toString()) {
                            menuInflater.inflate(R.menu.posts_u_admin, menu)
                        }

                        else if(role == "Admin"){
                            menuInflater.inflate(R.menu.posts_admin, menu)

                        }

                        else if(userUi == auth.currentUser?.uid.toString()){
                            menuInflater.inflate(R.menu.posts_user, menu)
                        }

                        else {
                            menuInflater.inflate(R.menu.posts_others, menu)
                        }
                    }
                }

            }

        }
        catch (e :Exception){
            Log.d("Menu Post", "onCreateOptionsMenu: $e")
        }

        return super.onCreateOptionsMenu(menu)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.deletePostUser ->{

                firestore = FirebaseFirestore.getInstance()
                firestore.collection("CommunityPosts").document(intent.getStringExtra("postId").toString()).delete()
                Toast.makeText(applicationContext, "Post Deleted!", Toast.LENGTH_SHORT).show()

            }

            R.id.reportPost ->{


                val map = HashMap<String, Any>()
                map["ReportedPost"]=intent.getStringExtra("postId").toString()
                map["ReportedBy"]=auth.currentUser?.uid.toString()
                firestore = FirebaseFirestore.getInstance()
                auth=FirebaseAuth.getInstance()
                firestore.collection("Reported Posts").add(map).addOnSuccessListener {
                    Toast.makeText(applicationContext, "Post Reported!", Toast.LENGTH_SHORT).show()
                }

            }
            R.id.deletePostUser ->{

                firestore = FirebaseFirestore.getInstance()
                firestore.collection("CommunityPosts").document(intent.getStringExtra("postId").toString()).delete()
                Toast.makeText(applicationContext, "Post Deleted!", Toast.LENGTH_SHORT).show()

            }

            R.id.editPostUser ->{
                val mIntent = Intent(applicationContext, EditCommunityPostActivity::class.java)
                mIntent.putExtra("postId",intent.getStringExtra("postId").toString())
                startActivity(mIntent)
            }

            R.id.delPostAd->{

                MaterialAlertDialogBuilder(this)
                    .setTitle("Deleting Post")
                    .setMessage("Are you sure, you want to delete the post?")

                    .setNegativeButton("No") { dialog, which ->
                        // Respond to negative button press
                        dialog.dismiss()
                    }
                    .setPositiveButton("Yes") { dialog, which ->
                        firestore = FirebaseFirestore.getInstance()
                        firestore.collection("CommunityPosts").document(intent.getStringExtra("postId").toString()).delete()
                        Toast.makeText(applicationContext, "Post Deleted!", Toast.LENGTH_SHORT).show()
                    }
                    .show()


            }

            R.id.pinPost ->{
                // TODO: Admin post in
                Toast.makeText(applicationContext, "User Post Pin", Toast.LENGTH_SHORT).show()
            }

            R.id.likedPost ->{

//                showLikedUsersBottomSheet()

            }

        }

        return super.onOptionsItemSelected(item)
    }




}


//private fun showLikedUsersBottomSheet() {
//    val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_post_likes_layout, null)
//    val recyclerView = bottomSheetView.findViewById<RecyclerView>(R.id.likeRecycler)
//    val likedUsers = mutableListOf<LikedUsers>()
//    val adapterLike = LikesAdapter(likedUsers)
//    // Set a LinearLayoutManager to the RecyclerView
//    recyclerView.layoutManager = LinearLayoutManager(this)
//
//    // Fetch the list of liked users from Firestore and pass it to the adapter
//    val postRef = FirebaseFirestore.getInstance().collection("CommunityPosts").document(pId)
//    postRef.collection("Likes").get().addOnSuccessListener { querySnapshot ->
//        for (document in querySnapshot) {
//            val userUid = document.id
//            getUser(userUid) { userName, _, userPfp ->
//                val likedUser = LikedUsers(userName, userPfp)
//                likedUsers.add(likedUser)
//                adapterLike.notifyDataSetChanged()
//            }
//        }
//        recyclerView.adapter = adapterLike
//    }.addOnFailureListener { exception ->
//        Log.e("showLikedUsersBottomSheet", "Error fetching liked users", exception)
//        // Handle failure
//    }
//
//    val dialog = BottomSheetDialog(this)
//    dialog.setContentView(bottomSheetView)
//    dialog.show()
//}