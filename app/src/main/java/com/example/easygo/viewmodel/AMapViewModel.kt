package com.example.easygo.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.ViewModel
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.core.PoiItem
import com.amap.api.services.help.Tip
import com.example.easygo.App
import com.example.easygo.R
import com.example.easygo.utility.MyToast
import com.example.easygo.utility.amap.PoiOverlay

/**
 * Created by chene on @date 2020/7/19
 * 高德地图的一些初始化，定位等方法
 */
class AMapViewModel : ViewModel() {
    //地图显示小蓝点
    fun showPointInMap(aMap: AMap, context: Context) {
        val locationStyle = MyLocationStyle().apply {
            myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
            myLocationIcon(
                BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(
                        context.resources, R.mipmap.ic_map_pointer
                    )
                )
            )
            strokeWidth(1f)
            strokeColor(0x00)
            radiusFillColor(0x00)
        }

        aMap.myLocationStyle = locationStyle
        aMap.isMyLocationEnabled = true
    }

    //初始化和设置地图的控件
    fun initUiControls(aMap: AMap) {
        aMap.uiSettings.apply {
            isZoomControlsEnabled = false
            isMyLocationButtonEnabled = true
            isMyLocationButtonEnabled = false
            isScaleControlsEnabled = true
            isRotateGesturesEnabled = false
        }
    }

    //持续定位并将位置信息存到sp里，方便快速读取
    fun locate(locationClient: AMapLocationClient, aMap: AMap, context: Context) {
        val locationClientOption = AMapLocationClientOption().apply {
            locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Transport
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            isNeedAddress = true
        }
        locationClient.apply {
            setLocationOption(locationClientOption)
            setLocationListener {
                if (it == null) {
                    MyToast.showToast("定位失败")
                } else {
                    if (it.errorCode == 0) {
                        saveLocation(it.latitude, it.longitude, context)
                    } else {
                        MyToast.showToast("AMapError, ErrorCode:${it.errorCode},errorIfo:${it.errorInfo}")
                        Log.d(
                            "debugF",
                            "AMapError, ErrorCode:${it.errorCode},errorIfo:${it.errorInfo}"
                        )
                    }
                }
            }
            stopLocation()
            startLocation()
        }
    }

    //存储位置信息
    @SuppressLint("CommitPrefEdits")
    private fun saveLocation(latitude: Double, longitude: Double, context: Context) {
        context.getSharedPreferences(App.LOCATION, Context.MODE_PRIVATE).edit().apply {
            putFloat("latitude", latitude.toFloat())
            putFloat("longitude", longitude.toFloat())
            apply()
        }
    }

    //改变图层
    fun changeLayer(context: Context, aMap: AMap, anchorView: View) {
        PopupMenu(context, anchorView).apply {
            inflate(R.menu.activity_main_pop_menu_change_layer)
            show()
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.pop_menu_navigation -> {
                        changeMapLayer(aMap, AMap.MAP_TYPE_NAVI)
                        true
                    }
                    R.id.pop_menu_night -> {
                        changeMapLayer(aMap, AMap.MAP_TYPE_NIGHT)
                        true
                    }
                    R.id.pop_menu_normal -> {
                        changeMapLayer(aMap, AMap.MAP_TYPE_NORMAL)
                        true
                    }
                    R.id.pop_menu_satellite -> {
                        changeMapLayer(aMap, AMap.MAP_TYPE_SATELLITE)
                        true
                    }
                    else -> true
                }
            }
        }
    }

    private fun changeMapLayer(aMap: AMap, layer: Int) {
        aMap.mapType = layer
    }

    //在地图上显示搜索出来的点
    fun showSearchResultInMap(poiItems: List<PoiItem>, aMap: AMap) {
        aMap.clear()
        PoiOverlay(aMap, poiItems).apply {
            removeFromMap()
            addToMap()
            zoomToSpan()
        }
    }

    //在地图上添加位标
    fun addTipMarker(tip: Tip, aMap: AMap) {
        val poiMarker = aMap.addMarker(MarkerOptions())
        tip.point.apply {
            val markerPosition = LatLng(latitude, longitude)
            poiMarker.position = markerPosition
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17f))
        }
        poiMarker.apply {
            title = tip.name
            snippet = tip.address
        }
    }

}