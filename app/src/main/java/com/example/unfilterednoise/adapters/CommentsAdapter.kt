package com.example.unfilterednoise.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.datamodels.Comments
import com.example.unfilterednoise.datamodels.Like
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
class CommentsAdapter(private val comments:List<Comments>,private val postId: String): RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.CommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comments_list, parent , false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsAdapter.CommentsViewHolder, position: Int) {
        val comment = comments[position]

        holder.commentUserName.text=comment.userName
        holder.commentUserRole.text=comment.userRole
        holder.commentContent.text=comment.comment
        holder.commentUpVoteCn.text=comment.commentUpVote.toString()
        holder.commentDownVoteCn.text=comment.commentDownVote.toString()
        holder.cUpB.setOnClickListener {




            val auth = FirebaseAuth.getInstance()
            val userUid = auth.currentUser?.uid.toString()

            val postRef = FirebaseFirestore.getInstance().collection("CommunityPosts").document(postId).collection("Comments").document(comment.id)
            val likeRef = postRef.collection("Likes").document(userUid)

            likeRef.get().addOnSuccessListener {docSnap ->
                if (docSnap.exists()){
                    likeRef.delete()
                    holder.cUpB.setImageResource(R.drawable.thumb_up_fill0)
                    postRef.update("CommentLikeCount", FieldValue.increment(-1))
                    val likeRun = holder.commentUpVoteCn.text.toString()
                    holder.commentUpVoteCn.text= (likeRun.toInt()-1).toString()


                }
                else {
                    likeRef.set(Like(postId, userUid))
                    holder.cUpB.setImageResource(R.drawable.thumb_up_fill100)
                    postRef.update("CommentLikeCount", FieldValue.increment(1))
                    val likeRun = holder.commentUpVoteCn.text.toString()
                    holder.commentUpVoteCn.text= (likeRun.toInt()+1).toString()
                }

            }



        }
        holder.cDownB.setOnClickListener {

            val auth = FirebaseAuth.getInstance()
            val userUid = auth.currentUser?.uid.toString()

            val postRef = FirebaseFirestore.getInstance().collection("CommunityPosts").document(postId).collection("Comments").document(comment.id)
            val likeRef = postRef.collection("CommentReaction").document(userUid)

            likeRef.get().addOnSuccessListener {docSnap ->
                if (docSnap.exists()){
                    likeRef.delete()
                    holder.cUpB.setImageResource(R.drawable.thumb_down_fill0)
                    postRef.update("CommentDisLikeCount", FieldValue.increment(-1))
                    val likeRun = holder.commentUpVoteCn.text.toString()
                    holder.commentUpVoteCn.text= (likeRun.toInt()-1).toString()


                }
                else {
                    likeRef.set(Like(postId, userUid))
                    holder.cUpB.setImageResource(R.drawable.thumb_down_fill)
                    postRef.update("CommentDisLikeCount", FieldValue.increment(1))
                    val likeRun = holder.commentUpVoteCn.text.toString()
                    holder.commentUpVoteCn.text= (likeRun.toInt()+1).toString()
                }

            }

        }

        if (comment.userPFP != ""){
            Picasso.get()
                .load(comment.userPFP)
                .into(holder.commentUserIcon)
        }


    }

    override fun getItemCount(): Int {
        return comments.size
    }

    inner class CommentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val commentUserIcon: ImageView = itemView.findViewById(R.id.commentPFP)
        val commentContent : TextView = itemView.findViewById(R.id.commentText)
        val commentUserName : TextView = itemView.findViewById(R.id.commenterUser)
        val commentUserRole  : TextView = itemView.findViewById(R.id.commenterRole)
        val commentUpVoteCn : TextView = itemView.findViewById(R.id.commentUpCount)
        val commentDownVoteCn : TextView = itemView.findViewById(R.id.commentUpCount)
        val cUpB : ImageButton =itemView.findViewById(R.id.commentUpB)
        val cDownB : ImageButton =itemView.findViewById(R.id.commentDownB)
        val sol : ImageButton = itemView.findViewById(R.id.commentSolution)

    }

}
