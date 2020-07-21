package com.example.easygo.utility

import android.content.Context
import com.example.easygo.AppDataBase
import com.example.easygo.repository.SearchHistoryRepository
import com.example.easygo.viewmodel.SearchHistoryViewModelFactory

/**
 * Created by chene on @date 2020/7/15
 */
object InjectorUtils {
    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepository.getInstance(
            AppDataBase.getInstance(context).searchHistoryDao()
        )
    }

    fun provideSearchHistoryViewModelFactory(context: Context): SearchHistoryViewModelFactory {
        val repository = getSearchHistoryRepository(context)
        return SearchHistoryViewModelFactory(repository)
    }
}