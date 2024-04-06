package com.example.unfilterednoise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.datamodels.CommunityPost
import com.squareup.picasso.Picasso

class CommunityPostAdapter(private val posts : List<CommunityPost>, private val onItemClick: (CommunityPost) -> Unit ): RecyclerView.Adapter<CommunityPostAdapter.PostsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.community_feed, parent, false)
        return PostsViewHolder(view)
    }


    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {

        val post = posts[position]
        holder.posterName.text=post.userName
        holder.posterHeading.text=post.userHeading
        holder.postContent.text=post.postContent
        holder.postTitle.text=post.postTitle

        if (post.postUserIcon != ""){
            Picasso.get().load(post.postUserIcon).into(holder.userIcon)
        }

        if(post.postImg != ""){
            Picasso.get().load(post.postImg).into(holder.postContentImage)
        }

        holder.itemView.setOnClickListener{onItemClick(post)}

    }

    override fun getItemCount(): Int {
        return posts.size
    }

    inner class PostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val posterHeading: TextView = itemView.findViewById(R.id.userHeadingFeed)
        val postTitle: TextView = itemView.findViewById(R.id.postTitle)
        val postContentImage : ImageView = itemView.findViewById(R.id.postContentImage)
        val posterName: TextView = itemView.findViewById(R.id.userNameFeed)
        val postContent: TextView = itemView.findViewById(R.id.postContentFeed)
        val userIcon: ImageView = itemView.findViewById(R.id.userIconFeed)

    }


}