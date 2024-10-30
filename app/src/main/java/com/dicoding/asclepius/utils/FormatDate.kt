package com.dicoding.asclepius.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object FormatDate {
    private const val INPUT_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val OUTPUT_FORMAT = "dd MMMM yyyy"

    fun formatDate(inputDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat(INPUT_FORMAT, Locale.getDefault())
            val outputFormat = SimpleDateFormat(OUTPUT_FORMAT, Locale.getDefault())
            val date: Date? = inputFormat.parse(inputDate)
            if (date != null) {
                outputFormat.format(date)
            } else {
                "Invalid date"
            }
        } catch (e: Exception) {
            "Error formatting date: ${e.message}"
        }
    }

    fun formatToDateOnly(dateString: String, inputPattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'", outputPattern: String = "dd MMM yyyy"): String {
        return try {
            val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString // return the original string if parsing fails
        }
    }
}