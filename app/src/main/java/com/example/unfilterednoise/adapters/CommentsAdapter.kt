package com.example.unfilterednoise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.datamodels.Comments
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class CommentsAdapter(private val comments:List<Comments>): RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>(){
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
        holder.cDownB.setOnClickListener {

            val auth = FirebaseAuth.getInstance()
//            Toast.makeText(context, "UpVoted!", Toast.LENGTH_SHORT).show()

        }
        holder.cUpB.setOnClickListener {

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
