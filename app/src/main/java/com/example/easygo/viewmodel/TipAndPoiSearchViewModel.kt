package com.example.easygo.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amap.api.services.core.PoiItem
import com.amap.api.services.core.SuggestionCity
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.example.easygo.App
import com.example.easygo.utility.MyToast

/**
 * Created by chene on @date 2020/7/19
 * 高德api搜索包括了tip和poi两类
 * 这个类实现了tip和poi的实际搜索，数据回调
 * 获得数据后再以观察者模式通知界面展示数据
 * 还拥有一些对象用以fragment与activity之间的通信
 */
class TipAndPoiSearchViewModel : ViewModel() {

    val poiItems: LiveData<List<PoiItem>>
        get() = _poiItems
    private val _poiItems: MutableLiveData<List<PoiItem>> = MutableLiveData()

    val tip: LiveData<Tip>
        get() = _tip
    private val _tip: MutableLiveData<Tip> = MutableLiveData()
    fun setSearchingSelectedTip(selectedTip: Tip) {
        _tip.value = selectedTip
    }

    val poiItem: LiveData<PoiItem>
        get() = _poiItem
    private val _poiItem: MutableLiveData<PoiItem> = MutableLiveData()

    fun setShowPoiItem(poiItem: PoiItem) {
        _poiItem.value = poiItem
    }

    //通过关键字搜索复数poi数据
    fun startSearching(keyWords: String, context: Context) {
        val currentPage = 2
        val query = PoiSearch.Query(keyWords, "", App.DEFAULT_CITY).apply {
            pageSize = 30
            pageNum = currentPage
        }
        PoiSearch(context, query).apply {
            setOnPoiSearchListener(poiSearchListener)
            searchPOIAsyn()
        }
    }

    //通过poiId搜索单个poi数据
    fun searchPoiById(tip: Tip, context: Context) {
        PoiSearch(context, null).apply {
            setOnPoiSearchListener(poiSearchListener)
            searchPOIIdAsyn(tip.poiID)
        }
    }

    //poi数据的回调监听
    private val poiSearchListener = object : PoiSearch.OnPoiSearchListener {
        override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
            if (p1 == 1000) {
                if (p0 != null) {
                    _poiItems.value = arrayListOf(p0)
                } else {
                    MyToast.showToast("返回数据为空")
                }
            } else {
                MyToast.showToast("无返回数据")
            }
        }

        override fun onPoiSearched(p0: PoiResult?, p1: Int) {
            if (p1 == 1000) {
                if (p0 !== null && p0.query != null) {
                    val poiItems = p0.pois
                    val suggestionCities = p0.searchSuggestionCitys
                    if (!poiItems.isNullOrEmpty()) {
                        _poiItems.value = poiItems
                    } else if (!suggestionCities.isNullOrEmpty()) {
                        showSuggestCities(suggestionCities)
                    } else {
                        MyToast.showToast("返回数据为空")
                    }
                } else {
                    MyToast.showToast("返回数据为空")
                }
            } else {
                MyToast.showToast("error: 无返回数据")
            }
        }

    }

    private fun showSuggestCities(cities: List<SuggestionCity>) {
        var information = "推荐城市\n"
        cities.forEach {
            information += "城市名称：${it.cityName} 城市区号：${it.cityCode} 城市编码:${it.adCode} \n"
        }
        MyToast.showToast(information)
    }

    val tips: LiveData<List<Tip>>
        get() = _tips
    private val _tips: MutableLiveData<List<Tip>> = MutableLiveData()

    //通过关键字搜索复数tip数据
    fun startSearchTipsByETText(text: String, context: Context) {
        Inputtips(
            context, InputtipsQuery(text, App.DEFAULT_CITY)
        ).apply {
            setInputtipsListener(inputTipsListener)
            requestInputtipsAsyn()
        }
    }

    //tip数据回调监听
    private val inputTipsListener = Inputtips.InputtipsListener { p0, p1 ->
        if (p1 == 1000) {
            _tips.value = p0
        } else {
            Toast.makeText(App.context, "error: errorCode:$p1", Toast.LENGTH_SHORT).show()
        }
    }
}