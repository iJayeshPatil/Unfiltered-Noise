package com.example.unfilterednoise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unfilterednoise.R
import com.example.unfilterednoise.datamodels.TopicPref
import com.example.unfilterednoise.datamodels.Topics
import com.squareup.picasso.Picasso

class PreferenceAdapter(private val topics: List<TopicPref>, private val onItemClick: (TopicPref) -> Unit) : RecyclerView.Adapter<PreferenceAdapter.PreferenceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_preference_items, parent, false)
        return PreferenceViewHolder(view)
    }



    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {

        val topic = topics[position]
        holder.prefNameTextView.text = topic.forumName

        Picasso.get()
            .load(topic.formIcon)
            .into(holder.prefIconImageView)

        holder.itemView.setOnClickListener {
            topic.isSelected = !topic.isSelected
            holder.prefNameTextView.isChecked = topic.isSelected
            onItemClick(topic)
        }
    }
    override fun getItemCount(): Int {
        return topics.size
    }
    inner class PreferenceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prefIconImageView: ImageView = itemView.findViewById(R.id.prefIcon)
        val prefNameTextView: CheckBox = itemView.findViewById(R.id.prefName)
    }
}