package com.example.easygo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amap.api.maps.AMap
import com.amap.api.services.route.*
import com.example.easygo.App
import com.example.easygo.utility.MyToast
import com.example.easygo.utility.amap.AMapUtil
import com.example.easygo.utility.amap.DrivingRouteOverlay
import com.example.easygo.utility.amap.RideRouteOverlay
import com.example.easygo.utility.amap.WalkRouteOverlay


/**
 * Created by chene on @date 2020/7/18
 * 路线规划实际搜索与数据回调监听的类
 * 虽然代码很相似，但确实不太好写成一个方法
 * 同时还负责通知观察者展示数据
 */
class RouteViewModel : ViewModel() {

    var des = ""
    var taxiCost = 0f

    val driveSteps: LiveData<List<DriveStep>>
        get() = _driveSteps
    private val _driveSteps: MutableLiveData<List<DriveStep>> = MutableLiveData()

    val rideSteps: LiveData<List<RideStep>>
        get() = _rideSteps
    private val _rideSteps: MutableLiveData<List<RideStep>> = MutableLiveData()

    val walkSteps: LiveData<List<WalkStep>>
        get() = _walkSteps
    private val _walkSteps: MutableLiveData<List<WalkStep>> = MutableLiveData()

    fun searchRouteResult(
        routeType: Int,
        mode: Int,
        fromAndTo: RouteSearch.FromAndTo,
        context: Context,
        aMap: AMap
    ) {
        when (routeType) {
            App.TYPE_DRIVE -> {
                searchDriveRoute(mode, fromAndTo, context, aMap)
            }

            App.TYPE_BIKE -> {
                searchBikeRoute(mode, fromAndTo, context, aMap)
            }
            else -> {
                searchWalkRoute(fromAndTo, context, aMap)
            }
        }
    }

    //发起路线搜索请求
    private fun searchBikeRoute(
        mode: Int,
        fromAndTo: RouteSearch.FromAndTo,
        context: Context,
        aMap: AMap
    ) {
        val query = RouteSearch.RideRouteQuery(fromAndTo, mode)
        RouteSearch(context).apply {
            setRouteSearchListener(object : RouteSearch.OnRouteSearchListener {
                override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
//                    TODO("Not yet implemented")
                }

                override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
//                    TODO("Not yet implemented")
                }

                override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
                    rideRouteSearched(p0, p1, context, aMap)
                }

                override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
//                    TODO("Not yet implemented")
                }
            })
            calculateRideRouteAsyn(query)
        }
    }

    //数据回调处理
    private fun rideRouteSearched(
        result: RideRouteResult?,
        errorCode: Int,
        context: Context,
        aMap: AMap
    ) {
        aMap.clear()
        if (errorCode == 1000) {
            if (result != null && result.paths != null) {
                if (result.paths.isNotEmpty()) {
                    val ridePath = result.paths[0]
                    RideRouteOverlay(
                        context,
                        aMap,
                        ridePath,
                        result.startPos,
                        result.targetPos
                    ).apply {
                        removeFromMap()
                        addToMap()
                        zoomToSpan()
                    }
                    val dis = ridePath.distance.toInt()
                    val dur = ridePath.duration.toInt()
                    des = "${AMapUtil.getFriendlyTime(dur)}(${AMapUtil.getFriendlyLength(dis)})"
                    _rideSteps.value = ridePath.steps
                }
            } else if (result != null && result.paths == null) {
                MyToast.showToast("返回数据为空")
            } else {
                MyToast.showToast("无返回数据")
            }
        } else {
            MyToast.showToast("error happen!! errorCode:$errorCode")
        }
    }

    private fun searchWalkRoute(fromAndTo: RouteSearch.FromAndTo, context: Context, aMap: AMap) {
        val query = RouteSearch.WalkRouteQuery(fromAndTo)
        RouteSearch(context).apply {
            setRouteSearchListener(object : RouteSearch.OnRouteSearchListener {
                override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
//                    TODO("Not yet implemented")
                }

                override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
//                    TODO("Not yet implemented")
                }

                override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
//                    TODO("Not yet implemented")
                }

                override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
                    walkRouteSearched(aMap, p0, p1, context)
                }
            })
            calculateWalkRouteAsyn(query)
        }
    }

    private fun walkRouteSearched(
        aMap: AMap,
        result: WalkRouteResult?,
        errorCode: Int,
        context: Context
    ) {
        aMap.clear()
        if (errorCode == 1000) {
            if (result != null && result.paths != null) {
                if (result.paths.isNotEmpty()) {
                    val walkPath = result.paths[0]
                    WalkRouteOverlay(
                        context,
                        aMap,
                        walkPath,
                        result.startPos,
                        result.targetPos
                    ).apply {
                        addToMap()
                        zoomToSpan()
                    }
                    val dis = walkPath.distance.toInt()
                    val dur = walkPath.duration.toInt()
                    des = "${AMapUtil.getFriendlyTime(dur)}(${AMapUtil.getFriendlyLength(dis)})"
                    _walkSteps.value = walkPath.steps
                }
            } else if (result != null && result.paths == null) {
                MyToast.showToast("返回数据为空")
            } else {
                MyToast.showToast("无返回数据")
            }
        } else {
            MyToast.showToast("error happen!! errorCode:$errorCode")
        }
    }

    private fun searchDriveRoute(
        mode: Int,
        fromAndTo: RouteSearch.FromAndTo,
        context: Context,
        aMap: AMap
    ) {
        val query = RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "")
        RouteSearch(context).apply {
            setRouteSearchListener(object : RouteSearch.OnRouteSearchListener {
                override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {
                    driveRouteSearched(aMap, p0, p1, context)
                }

                override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
//                    TODO("Not yet implemented")
                }

                override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
//                    TODO("Not yet implemented")
                }

                override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
//                    TODO("Not yet implemented")
                }
            })
            calculateDriveRouteAsyn(query)
        }
    }

    private fun driveRouteSearched(
        aMap: AMap,
        driveRouteResult: DriveRouteResult?,
        errorCode: Int,
        context: Context
    ) {
        aMap.clear()
        if (errorCode == 1000) {
            if (driveRouteResult != null && driveRouteResult.paths != null) {
                if (driveRouteResult.paths.isNotEmpty()) {
                    val drivePath = driveRouteResult.paths[0]
                    DrivingRouteOverlay(
                        context, aMap, drivePath, driveRouteResult.startPos,
                        driveRouteResult.targetPos, null
                    ).apply {
                        setNodeIconVisibility(false)
                        setIsColorFullLine(true)
                        removeFromMap()
                        addToMap()
                        zoomToSpan()
                    }
                    val dis = drivePath.distance.toInt()
                    val dur = drivePath.duration.toInt()
                    des = "${AMapUtil.getFriendlyTime(dur)}(${AMapUtil.getFriendlyLength(dis)})"
                    taxiCost = driveRouteResult.taxiCost//打车约多少元
                    _driveSteps.value = drivePath.steps
                }
            } else if (driveRouteResult != null && driveRouteResult.paths == null) {
                MyToast.showToast("返回数据为空")
            } else {
                MyToast.showToast("无返回数据")
            }
        } else {
            MyToast.showToast("error happen!! errorCode:$errorCode")
        }
    }
}