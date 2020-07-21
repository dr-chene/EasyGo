package com.example.easygo.utility

import com.example.easygo.bean.SearchHistory

/**
 * Created by chene on @date 2020/7/20
 */
class DeleteEvent(
    private val searchHistory: SearchHistory?
) {
    fun getSearchHistory() = searchHistory
}