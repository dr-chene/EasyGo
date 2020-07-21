package com.example.easygo.utility

import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.help.Tip
import com.example.easygo.bean.SearchHistory
import java.util.*

/**
 * Created by chene on @date 2020/7/19
 */
class MyConverters {
    companion object {

        fun poiItemToSearchHisTory(poiItem: PoiItem): SearchHistory =
            SearchHistory(
                poiItem.poiId,
                poiItem.title,
                poiItem.snippet,
                poiItem.latLonPoint.latitude,
                poiItem.latLonPoint.longitude,
                Calendar.getInstance().timeInMillis
            )

        fun tipToSearchHistory(tip: Tip): SearchHistory {
            val poiId = if (tip.poiID.isNullOrEmpty()) "" else tip.poiID
            val latitude: Double = tip.point?.latitude ?: 0.0
            val longitude: Double = tip.point?.longitude ?: 0.0
            return SearchHistory(
                poiId,
                tip.name,
                tip.address,
                latitude,
                longitude,
                Calendar.getInstance().timeInMillis
            )
        }
        fun searchHistoryToTip(searchHistory: SearchHistory): Tip = Tip().apply {
            name = searchHistory.title
            address = searchHistory.snippet
            setID(searchHistory.poiId)
            setPostion(LatLonPoint(searchHistory.latitude, searchHistory.longitude))
        }
    }
}