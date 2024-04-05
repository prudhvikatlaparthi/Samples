package com.pru.ktorteams.android

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.pru.ktorteams.Post
import com.pru.ktorteams.android.databinding.PostItemBinding

class PostListAdapter(private val posts: MutableList<Post>) :
    RecyclerView.Adapter<PostListAdapter.PostListViewHolder>() {

    inner class PostListViewHolder
    constructor(
        private val binding: PostItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Post) = with(item) {
            binding.tvID.text = id.toString()
            binding.tvTitle.text = title
            binding.tvBody.text = body
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListViewHolder {
        val binding = PostItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return PostListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostListViewHolder, position: Int) {
        holder.bind(posts.get(position))
    }

    override fun getItemCount(): Int {
        return posts.size
    }

}