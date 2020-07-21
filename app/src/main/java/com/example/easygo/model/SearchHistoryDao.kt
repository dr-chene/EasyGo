package com.example.easygo.model

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.easygo.bean.SearchHistory

/**
 * Created by chene on @date 2020/7/16
 */
@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM searchHistory ORDER BY time DESC LIMIT 20")
    fun getSearchHistoryList(): LiveData<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(searchHistory: SearchHistory)

    @Query(value = "DELETE From searchHistory where time = :time")
    suspend fun deleteSearchHistoryByPoiId(time : Long)
}