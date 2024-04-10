package com.example.unfilterednoise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.datamodels.Like
import com.example.unfilterednoise.datamodels.LikedUsers
import com.squareup.picasso.Picasso

class LikesAdapter(private val likedUsers: List<LikedUsers>) : RecyclerView.Adapter<LikesAdapter.LikedUserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_sheet_posts_like, parent, false)
        return LikedUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: LikedUserViewHolder, position: Int) {
        val likedUser = likedUsers[position]
        holder.userNameTextView.text=likedUser.userName
        Picasso.get()
            .load(likedUser.userIcon)
            .into(holder.userIconImageView)

    }

    override fun getItemCount(): Int {
        return likedUsers.size
    }

    inner class LikedUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userIconImageView: ImageView = itemView.findViewById(R.id.userIconLike)
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameLiked)

    }
}
