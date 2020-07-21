package com.example.easygo.utility.amap

import android.content.Context
import com.amap.api.maps.AMap
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.RidePath
import com.amap.api.services.route.RideStep
import com.example.easygo.R


/**
 * Created by chene on @date 2020/7/19
 */
class RideRouteOverlay(
    context: Context, aMap: AMap, path: RidePath, start: LatLonPoint, end: LatLonPoint
) : RouteOverlay(context) {
    private var mPolylineOptions: PolylineOptions? = null
    private var rideStationDescriptor: BitmapDescriptor? = null
    private var ridePath: RidePath? = null

    init {
        this.aMap = aMap
        this.ridePath = path
        startPoint = AMapUtil.convertToLatLng(start)
        endPoint = AMapUtil.convertToLatLng(end)
    }

    fun addToMap() {
        initPolylineOptions()
        try {
            val ridePaths = ridePath!!.steps
            mPolylineOptions!!.add(startPoint)
            for (i in ridePaths.indices) {
                val rideStep = ridePaths[i]
                val latLng = AMapUtil.convertToLatLng(
                    rideStep
                        .polyline[0]
                )
                addRideStationMarkers(rideStep, latLng)
                addRidePolyLines(rideStep)
            }
            mPolylineOptions?.add(endPoint)
            addStartAndEndMarker()
            showPolyline()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun addRidePolyLines(rideStep: RideStep) {
        mPolylineOptions!!.addAll(AMapUtil.convertArrList(rideStep.polyline))
    }

    private fun addRideStationMarkers(rideStep: RideStep, position: LatLng) {
        addStationMarker(
            MarkerOptions()
                .position(position)
                .title(
                    """
                        方向:${rideStep.action}
                        道路:${rideStep.road}
                        """.trimIndent()
                )
                .snippet(rideStep.instruction).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(rideStationDescriptor)
        )
    }

    private fun initPolylineOptions() {
        rideStationDescriptor = getRideBitmapDescriptor()
        if (rideStationDescriptor == null) {
            rideStationDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.amap_ride)
        }
        mPolylineOptions = null
        mPolylineOptions = PolylineOptions()
        mPolylineOptions?.color(getRideColor())?.width(getRouteWidth())
    }

    private fun showPolyline() {
        addPolyLine(mPolylineOptions)
    }
}