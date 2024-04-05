package com.pru.newsapp.mvvm.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pru.touchnote.R
import com.pru.touchnote.data.model.Data
import kotlinx.android.synthetic.main.item_user.view.*


class UsersAdapter : RecyclerView.Adapter<UsersAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    private var onClickListener: ((Data) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val user = differ.currentList[position]
        holder.itemView.apply {
            tv_name.text =
                if (TextUtils.isEmpty(user.name)) "UnKnown" else user.name
            tv_email.text =
                if (TextUtils.isEmpty(user.email)) "UnKnown" else user.email
            tv_gender.text =
                if (TextUtils.isEmpty(user.gender)) "UnKnown" else user.gender
            tv_status.text =
                if (TextUtils.isEmpty(user.status)) "UnKnown" else user.status

            setOnClickListener {
                onClickListener?.let { it(user) }
            }
        }
    }

    fun setOnClickListener(listener: (Data) -> Unit) {
        onClickListener = listener
    }
}