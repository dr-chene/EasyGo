package com.example.easygo.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by chene on @date 2020/7/16
 */
@Entity(tableName = "searchHistory")
data class SearchHistory(
    @PrimaryKey
    val poiId: String,
    val title: String,
    val snippet: String,
    val latitude: Double,
    val longitude: Double,
    val time: Long
)