package com.example.unfilterednoise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.datamodels.Topics
import com.squareup.picasso.Picasso

class TopicsAdapter(private val communities: List<Topics>, private val onItemClick: (Topics) -> Unit) : RecyclerView.Adapter<TopicsAdapter.CommunityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.topics_list, parent, false)
        return CommunityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {

        val community = communities[position]
        holder.communityNameTextView.text = community.forumName

        Picasso.get()
            .load(community.formIcon)
            .into(holder.communityIconImageView)

        holder.itemView.setOnClickListener { onItemClick(community) }
    }

    override fun getItemCount(): Int {
        return communities.size
    }

    inner class CommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val communityIconImageView: ImageView = itemView.findViewById(R.id.forumIconImg)
        val communityNameTextView: TextView = itemView.findViewById(R.id.forumNameCF)
    }
}
