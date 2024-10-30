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
)
