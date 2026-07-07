package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,
    val storeName: String,
    val totalItems: Int,
    val score: Int,
    val itemsJson: String
)

@JsonClass(generateAdapter = true)
data class ScannedItemData(
    val originalText: String,
    val matchedFoodId: String?
)
