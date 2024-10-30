package com.dicoding.asclepius.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatDate {
    fun formatDate(timestamp: String): String {
        return try {
            val milliseconds = timestamp.toLong()
            val date = Date(milliseconds)
            val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
            sdf.format(date)
        } catch (e: Exception) {
            "Invalid Date"
        }
    }

    fun formatCardDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        val date = inputFormat.parse(input)
        return date?.let { outputFormat.format(it) } ?: ""
    }
}