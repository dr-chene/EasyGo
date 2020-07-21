package com.example.easygo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amap.api.services.core.LatLonPoint

/**
 * Created by chene on @date 2020/7/19
 * MainActivity与Fragment之间通信的一些参数
 * 使用了观察者模式
 */
class MainActivityViewModel : ViewModel() {
    val isLoadTraffic: LiveData<Boolean>
        get() = _isLoadTraffic
    private val _isLoadTraffic: MutableLiveData<Boolean> = MutableLiveData(true)
    fun setLoadTraffic(isLoadTraffic: Boolean) {
        _isLoadTraffic.value = isLoadTraffic
    }

    val isFullMap: LiveData<Boolean>
        get() = _isFullMap
    private val _isFullMap: MutableLiveData<Boolean> = MutableLiveData(false)

    fun setFullMap(isFullMap: Boolean) {
        _isFullMap.value = isFullMap
    }

    val nav: LiveData<Int>
        get() = _nav
    private val _nav: MutableLiveData<Int> = MutableLiveData()
    fun navTo(fragmentType: Int) {
        _nav.value = fragmentType
    }

    val navToPoi: LiveData<LatLonPoint>
        get() = _navToPoi
    private val _navToPoi: MutableLiveData<LatLonPoint> = MutableLiveData()
    fun navToPoi(latLonPoint: LatLonPoint) {
        _navToPoi.value = latLonPoint
    }
}