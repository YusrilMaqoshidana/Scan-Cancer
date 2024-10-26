package com.dicoding.asclepius.data.remote.model

data class Response(
	val totalResults: Int? = null,
	val articles: List<ArticlesItem?>? = null,
	val status: String? = null
)

data class Source(
	val name: String? = null,
	val id: Any? = null
)

data class ArticlesItem(
	val publishedAt: String? = null,
	val author: Any? = null,
	val urlToImage: Any? = null,
	val description: String? = null,
	val source: Source? = null,
	val title: String? = null,
	val url: String? = null,
	val content: String? = null
)

