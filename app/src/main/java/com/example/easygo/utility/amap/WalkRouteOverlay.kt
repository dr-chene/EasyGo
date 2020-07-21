package com.example.easygo.utility.amap

import android.content.Context
import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptor
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.PolylineOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.WalkPath
import com.amap.api.services.route.WalkStep


/**
 * Created by chene on @date 2020/7/18
 */
class WalkRouteOverlay(
    context: Context, aMap: AMap, path: WalkPath, start: LatLonPoint, end: LatLonPoint
) : RouteOverlay(context) {
    private var mPolylineOptions: PolylineOptions? = null

    private var walkStationDescriptor: BitmapDescriptor? = null

    private var walkPath: WalkPath = path

    init {
        this.aMap = aMap
        this.startPoint = AMapUtil.convertToLatLng(start)
        this.endPoint = AMapUtil.convertToLatLng(end)
    }

    fun addToMap() {
        initPolylineOptions()
        try {
            val walkPaths = walkPath.steps
            mPolylineOptions!!.add(startPoint)
            for (i in walkPaths.indices) {
                val walkStep = walkPaths[i]
                val latLng: LatLng = AMapUtil.convertToLatLng(
                    walkStep
                        .polyline[0]
                )
                addWalkStationMarkers(walkStep, latLng)
                addWalkPolyLines(walkStep)
            }
            mPolylineOptions?.add(endPoint)
            addStartAndEndMarker()
            showPolyline()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun checkDistanceToNextStep(
        walkStep: WalkStep,
        walkStep1: WalkStep
    ) {
        val lastPoint: LatLonPoint = getLastWalkPoint(walkStep)
        val nextFirstPoint: LatLonPoint = getFirstWalkPoint(walkStep1)
        if (lastPoint != nextFirstPoint) {
            addWalkPolyLine(lastPoint, nextFirstPoint)
        }
    }

    private fun getLastWalkPoint(walkStep: WalkStep): LatLonPoint {
        return walkStep.polyline[walkStep.polyline.size - 1]
    }

    private fun getFirstWalkPoint(walkStep: WalkStep): LatLonPoint {
        return walkStep.polyline[0]
    }


    private fun addWalkPolyLine(pointFrom: LatLonPoint, pointTo: LatLonPoint) {
        addWalkPolyLine(
            AMapUtil.convertToLatLng(pointFrom),
            AMapUtil.convertToLatLng(pointTo)
        )
    }

    private fun addWalkPolyLine(latLngFrom: LatLng, latLngTo: LatLng) {
        mPolylineOptions!!.add(latLngFrom, latLngTo)
    }

    /**
     * @param walkStep
     */
    private fun addWalkPolyLines(walkStep: WalkStep) {
        mPolylineOptions!!.addAll(AMapUtil.convertArrList(walkStep.polyline))
    }

    /**
     * @param walkStep
     * @param position
     */
    private fun addWalkStationMarkers(walkStep: WalkStep, position: LatLng) {
        addStationMarker(
            MarkerOptions()
                .position(position)
                .title(
                    """
                        方向:${walkStep.action}
                        道路:${walkStep.road}
                        """.trimIndent()
                )
                .snippet(walkStep.instruction).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(walkStationDescriptor)
        )
    }


    private fun initPolylineOptions() {
        if (walkStationDescriptor == null) {
            walkStationDescriptor = getWalkBitmapDescriptor()
        }
        mPolylineOptions = null
        mPolylineOptions = PolylineOptions()
        mPolylineOptions?.color(getWalkColor())?.width(getRouteWidth())
    }


    private fun showPolyline() {
        addPolyLine(mPolylineOptions)
    }
}