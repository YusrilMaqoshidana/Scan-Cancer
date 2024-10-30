package com.dicoding.asclepius.data.remote.model

import com.google.gson.annotations.SerializedName

data class ArticlesItem(
    @field:SerializedName("publishedAt") val publishedAt: String?,
    @field:SerializedName("author") val author: String?,
    @field:SerializedName("urlToImage") val urlToImage: String?,
    @field:SerializedName("description") val description: String?,
    @field:SerializedName("title") val title: String?,
    @field:SerializedName("url") val url: String?,
) {
    class Builder {
        private var publishedAt: String? = null
        private var author: String? = null
        private var urlToImage: String? = null
        private var description: String? = null
        private var title: String? = null
        private var url: String? = null

        fun publishedAt(publishedAt: String?) = apply { this.publishedAt = publishedAt }
        fun author(author: String?) = apply { this.author = author }
        fun urlToImage(urlToImage: String?) = apply { this.urlToImage = urlToImage }
        fun description(description: String?) = apply { this.description = description }
        fun title(title: String?) = apply { this.title = title }
        fun url(url: String?) = apply { this.url = url }
        fun build() =
            ArticlesItem(publishedAt, author, urlToImage, description, title, url)
    }
}