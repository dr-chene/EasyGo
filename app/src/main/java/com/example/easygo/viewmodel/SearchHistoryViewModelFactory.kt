package com.example.easygo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.easygo.repository.SearchHistoryRepository

/**
 * Created by chene on @date 2020/7/16
 */
class SearchHistoryViewModelFactory(
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchHistoryViewModel(searchHistoryRepository) as T
    }
}