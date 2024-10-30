package com.dicoding.asclepius.utils

object FormatPercentage {
    fun formatPercentage(value: Float): String {
        return "%.2f%%".format(value * 100)
    }
}