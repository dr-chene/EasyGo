package com.example.easygo.utility.amap

import android.graphics.BitmapFactory
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.PoiItem
import com.example.easygo.App.Companion.context
import com.example.easygo.R


/**
 * Created by chene on @date 2020/7/16
 * Poi图层类。在高德地图API里，如果要显示Poi，可以用此类来创建Poi图层。如不满足需求，也可以自己创建自定义的Poi图层。
 */
class PoiOverlay(private val amap: AMap, private val pois: List<PoiItem>) {

    private val mPoiMarks = ArrayList<Marker>()

    /**
     * 添加Marker到地图中。
     */
    fun addToMap() {
        try {
            for (i in pois.indices) {
                val marker = amap.addMarker(getMarkerOptions(i))
                marker.setObject(i)
                mPoiMarks.add(marker)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * 去掉PoiOverlay上所有的Marker。
     */
    fun removeFromMap() {
        for (mark in mPoiMarks) {
            mark.remove()
        }
    }

    /**
     * 移动镜头到当前的视角。
     */
    fun zoomToSpan() {
        try {
            if (!pois.isNullOrEmpty()) {
                if (pois.size == 1) {
                    amap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                pois[0].latLonPoint.latitude,
                                pois[0].latLonPoint.longitude
                            ), 18f
                        )
                    )
                } else {
                    val bounds = getLatLngBounds()
                    amap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 30))
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun getLatLngBounds(): LatLngBounds {
        val b = LatLngBounds.builder()
        if (!pois.isNullOrEmpty()) {
            for (i in pois.indices) {
                b.include(
                    LatLng(
                        pois[i].latLonPoint.latitude,
                        pois[i].latLonPoint.longitude
                    )
                )
            }
        }
        return b.build()
    }

    private fun getMarkerOptions(index: Int): MarkerOptions? =
        if (!pois.isNullOrEmpty()) {
            MarkerOptions().position(
                LatLng(
                    pois[index].latLonPoint.latitude,
                    pois[index].latLonPoint.longitude
                )
            ).title(getTitle(index)).snippet(getSnippet(index))
                .icon(getBitmapDescriptor(index))
        } else null


    /**
     * 给第几个Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     * @param index 第几个Marker。
     * @return 更换的Marker图片。
     */
    private fun getBitmapDescriptor(index: Int): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromBitmap(
            BitmapFactory.decodeResource(
                context?.resources, R.mipmap.ic_map_locate
            )
        )
    }

    /**
     * 返回第index的Marker的标题。
     * @param index 第几个Marker。
     * @return marker的标题。
     */
    private fun getTitle(index: Int): String? {
        return pois[index].title
    }

    /**
     * 返回第index的Marker的详情。
     * @param index 第几个Marker。
     * @return marker的详情。
     */
    private fun getSnippet(index: Int): String? {
        return pois[index].snippet
    }

    /**
     * 从marker中得到poi在list的位置。
     * @param marker 一个标记的对象。
     * @return 返回该marker对应的poi在list的位置。
     * @since V2.1.0
     */
    fun getPoiIndex(marker: Marker?): Int {
        for (i in 0 until mPoiMarks.size) {
            if (mPoiMarks[i] == marker) {
                return i
            }
        }
        return -1
    }

    /**
     * 返回第index的poi的信息。
     * @param index 第几个poi。
     * @return poi的信息。
     */
    fun getPoiItem(index: Int): PoiItem? {
        return if (index < 0 || index >= pois.size) {
            null
        } else pois[index]
    }
}