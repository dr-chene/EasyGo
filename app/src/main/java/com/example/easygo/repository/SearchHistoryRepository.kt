package com.example.easygo.repository

import com.example.easygo.bean.SearchHistory
import com.example.easygo.model.SearchHistoryDao

/**
 * Created by chene on @date 2020/7/16
 */
class SearchHistoryRepository private constructor(
    private val searchHistoryDao: SearchHistoryDao
) {
    fun getSearchHistoryList() = searchHistoryDao.getSearchHistoryList()

    suspend fun insertSearchHistory(searchHistory: SearchHistory) {
        searchHistoryDao.insertSearchHistory(searchHistory)
    }

    suspend fun deleteSearchHistoryByPoiId(time: Long){
        searchHistoryDao.deleteSearchHistoryByPoiId(time)
    }

    companion object {

        @Volatile
        private var instance: SearchHistoryRepository? = null

        fun getInstance(searchHistoryDao: SearchHistoryDao) =
            instance ?: synchronized(this) {
                instance ?: SearchHistoryRepository(searchHistoryDao).also { instance = it }
            }
    }
}