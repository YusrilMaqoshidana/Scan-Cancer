package com.dicoding.asclepius.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.remote.model.ArticlesItem
import com.dicoding.asclepius.databinding.CardHistoryBinding
import com.dicoding.asclepius.databinding.CardNewsBinding
import com.dicoding.asclepius.utils.FormatDate.formatCardDate
import com.dicoding.asclepius.view.news.NewsViewModel

class NewsAdapter(private val viewModel: NewsViewModel) :
    RecyclerView.Adapter<NewsAdapter.MyNewsViewHolder>() {
    private val newsList = mutableListOf<ArticlesItem>()

    inner class MyNewsViewHolder(private val binding: CardNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(news: ArticlesItem) {
            with(binding) {
                textTitle.text = news.title
                textDescription.text = news.description
                textAuthor.text = news.author
                textDate.text = formatCardDate(news.publishedAt.toString())
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
        val diffCallback = NewsDiffCallback(newsList, newsList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.newsList.clear()
        this.newsList.addAll(newsList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyNewsViewHolder {
        val binding = CardNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyNewsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: MyNewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.bind(news)
    }

    private class NewsDiffCallback(
        private val oldNewsList: List<ArticlesItem>,
        private val newNewsList: List<ArticlesItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldNewsList.size
        override fun getNewListSize(): Int = newNewsList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldNewsList[oldItemPosition] == newNewsList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldNewsList[oldItemPosition] == newNewsList[newItemPosition]
        }
    }
}