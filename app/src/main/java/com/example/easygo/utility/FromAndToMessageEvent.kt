package com.example.easygo.utility

import com.amap.api.services.route.RouteSearch

/**
 * Created by chene on @date 2020/7/18
 */
class FromAndToMessageEvent(
    private val fromAndTo: RouteSearch.FromAndTo,
    private val searchType: Int
) {
    fun getFromAndTo() = fromAndTo
    fun getSearchType() = searchType
}