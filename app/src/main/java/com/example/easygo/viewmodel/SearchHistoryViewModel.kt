package com.example.easygo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.easygo.bean.SearchHistory
import com.example.easygo.repository.SearchHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by chene on @date 2020/7/16
 */
class SearchHistoryViewModel internal constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    val searchHistoryList: LiveData<List<SearchHistory>> =
        searchHistoryRepository.getSearchHistoryList()


    fun insertSearchHistory(history: SearchHistory) {
        GlobalScope.launch(Dispatchers.IO) {
            searchHistoryRepository.insertSearchHistory(history)
        }
    }

    fun deleteSearchHistoryByPoiId(time: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            searchHistoryRepository.deleteSearchHistoryByPoiId(time)
        }
    }
}