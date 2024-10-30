package com.dicoding.asclepius.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "confidenceScore")
    val confidenceScore: Float,

    @ColumnInfo(name = "imageUri", typeAffinity = ColumnInfo.BLOB)
    val imageUri: ByteArray,

    @ColumnInfo(name = "timestamp")
    val timestamp: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HistoryEntity

        if (id != other.id) return false
        if (category != other.category) return false
        if (confidenceScore != other.confidenceScore) return false
        if (!imageUri.contentEquals(other.imageUri)) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + category.hashCode()
        result = 31 * result + confidenceScore.hashCode()
        result = 31 * result + imageUri.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
