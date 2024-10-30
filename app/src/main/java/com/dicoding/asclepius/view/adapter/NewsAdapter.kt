package com.dicoding.asclepius.view.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.remote.model.ArticlesItem
import com.dicoding.asclepius.databinding.CardNewsBinding
import com.dicoding.asclepius.utils.FormatDate.formatToDateOnly

class NewsAdapter(private var newsItem: List<ArticlesItem>) :
    RecyclerView.Adapter<NewsAdapter.MyNewsViewHolder>() {

    class MyNewsViewHolder(private val binding: CardNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(news: ArticlesItem) {
            with(binding) {
                textTitle.text = news.title
                textDescription.text = news.description
                textAuthor.text = news.author
                Log.d("NewsAdapter", "Binding news item: ${news.publishedAt}")
                textDate.text = formatToDateOnly(news.publishedAt.toString())
                Glide.with(itemView.context)
                    .load(news.urlToImage)
                    .into(imageNews)

                itemView.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, news.url?.toUri())
                    itemView.context.startActivity(browserIntent)
                }

            }
        }
    }

    fun setNewsList(newsList: List<ArticlesItem>) {
        val diffCallback = NewsDiffCallback(newsItem, newsList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        newsItem = newsList // Update the newsItem reference
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNewsViewHolder {
        val binding = CardNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyNewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsItem.size
    }

    override fun onBindViewHolder(holder: MyNewsViewHolder, position: Int) {
        val news = newsItem[position]
        holder.bind(news)
    }

    private class NewsDiffCallback(
        private val oldNewsList: List<ArticlesItem>,
        private val newNewsList: List<ArticlesItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldNewsList.size
        override fun getNewListSize(): Int = newNewsList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldNewsList[oldItemPosition].title == newNewsList[newItemPosition].title
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldNewsList[oldItemPosition] == newNewsList[newItemPosition]
        }
    }
}
