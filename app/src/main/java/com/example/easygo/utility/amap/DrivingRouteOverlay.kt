package com.example.easygo.utility.amap

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.amap.api.maps.AMap
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.DrivePath
import com.amap.api.services.route.DriveStep
import com.amap.api.services.route.TMC
import com.example.easygo.R
import java.util.*


/**
 * Created by chene on @date 2020/7/18
 */
class DrivingRouteOverlay(
    context: Context,
    aMap: AMap,
    path: DrivePath,
    start: LatLonPoint,
    end: LatLonPoint,
    private var throughPointList: List<LatLonPoint>?
) : RouteOverlay(context) {
    private var drivePath: DrivePath? = path
    private val throughPointMarkerList: MutableList<Marker> = ArrayList()
    private var throughPointMarkerVisible = true
    private var tmcs: MutableList<TMC>? = null
    private var mPolylineOptions: PolylineOptions? = null
    private var mPolylineOptionsColor: PolylineOptions? = null
    private var isColorFullLine = true
    private var mWidth = 25f
    private var mLatLngsOfPath: MutableList<LatLng>? = null

    fun setIsColorFullLine(isColorFullLine: Boolean) {
        this.isColorFullLine = isColorFullLine
    }

    init {
        this.aMap = aMap
        startPoint = AMapUtil.convertToLatLng(start)
        endPoint = AMapUtil.convertToLatLng(end)
        initBitmapDescriptor()
    }

    override fun getRouteWidth(): Float {
        return mWidth
    }

    fun setRouteWidth(mWidth: Float) {
        this.mWidth = mWidth
    }

    fun addToMap() {
        initPolylineOptions()
        try {
            if (aMap == null) {
                return
            }
            if (mWidth == 0f || drivePath == null) {
                return
            }
            mLatLngsOfPath = ArrayList()
            tmcs = ArrayList()
            val drivePaths = drivePath!!.steps
            mPolylineOptions!!.add(startPoint)
            for (i in drivePaths.indices) {
                val step = drivePaths[i]
                val latLonPoints = step.polyline
                val tmcList = step.tmCs
                (tmcs as ArrayList<TMC>).addAll(tmcList)
                addDrivingStationMarkers(step, convertToLatLng(latLonPoints[0]))
                for (latLonPoint in latLonPoints) {
                    mPolylineOptions?.add(convertToLatLng(latLonPoint))
                    mLatLngsOfPath?.add(convertToLatLng(latLonPoint))
                }
            }
            mPolylineOptions?.add(endPoint)
            if (startMarker != null) {
                startMarker!!.remove()
                startMarker = null
            }
            if (endMarker != null) {
                endMarker!!.remove()
                endMarker = null
            }
            addStartAndEndMarker()
            addThroughPointMarker()
            if (isColorFullLine && !tmcs.isNullOrEmpty()) {
                colorWayUpdate(tmcs)
                showColorPolyline()
            } else {
                showPolyline()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun initPolylineOptions() {
        mPolylineOptions = null
        mPolylineOptions = PolylineOptions()
        mPolylineOptions?.color(getDriveColor())?.width(getRouteWidth())
    }

    private fun showPolyline() {
        addPolyLine(mPolylineOptions)
    }

    private fun showColorPolyline() {
        addPolyLine(mPolylineOptionsColor)
    }

    private fun colorWayUpdate(tmcSection: List<TMC>?) {
        if (aMap == null) {
            return
        }
        if (tmcSection == null || tmcSection.isEmpty()) {
            return
        }
        var segmentTrafficStatus: TMC
        mPolylineOptionsColor = null
        mPolylineOptionsColor = PolylineOptions()
        mPolylineOptionsColor?.width(getRouteWidth())
        val colorList: MutableList<Int> = ArrayList()
        val bitmapDescriptors: MutableList<BitmapDescriptor?> =
            ArrayList()
        val points: MutableList<LatLng> = ArrayList()
        val texIndexList: MutableList<Int> = ArrayList()
        //        mPolylineOptionscolor.add(startPoint);
//        mPolylineOptionscolor.add(AMapUtil.convertToLatLng(tmcSection.get(0).getPolyline().get(0)));
        points.add(startPoint!!)
        points.add(AMapUtil.convertToLatLng(tmcSection[0].polyline[0]))
        colorList.add(getDriveColor())
        bitmapDescriptors.add(defaultRoute)
        var bitmapDescriptor: BitmapDescriptor? = null
        var textIndex = 0
        texIndexList.add(textIndex)
        texIndexList.add(++textIndex)
        for (i in tmcSection.indices) {
            segmentTrafficStatus = tmcSection[i]
            val color = getColor(segmentTrafficStatus.status)
            bitmapDescriptor = getTrafficBitmapDescriptor(segmentTrafficStatus.status)
            val mPloyLine = segmentTrafficStatus.polyline
            for (j in mPloyLine.indices) {
//				mPolylineOptionscolor.add(AMapUtil.convertToLatLng(mployline.get(j)));
                points.add(AMapUtil.convertToLatLng(mPloyLine[j]))
                colorList.add(color)
                texIndexList.add(++textIndex)
                bitmapDescriptors.add(bitmapDescriptor)
            }
        }
        points.add(endPoint!!)
        colorList.add(getDriveColor())
        bitmapDescriptors.add(defaultRoute)
        texIndexList.add(++textIndex)
        mPolylineOptionsColor?.addAll(points)
        mPolylineOptionsColor?.colorValues(colorList)

//        mPolylineOptionscolor.setCustomTextureIndex(texIndexList);
//        mPolylineOptionscolor.setCustomTextureList(bitmapDescriptors);
    }

    private var defaultRoute: BitmapDescriptor? = null
    private var unknownTraffic: BitmapDescriptor? = null
    private var smoothTraffic: BitmapDescriptor? = null
    private var slowTraffic: BitmapDescriptor? = null
    private var jamTraffic: BitmapDescriptor? = null
    private var veryJamTraffic: BitmapDescriptor? = null
    private fun initBitmapDescriptor() {
        defaultRoute =
            BitmapDescriptorFactory.fromResource(R.drawable.amap_route_color_texture_6_arrow)
        smoothTraffic =
            BitmapDescriptorFactory.fromResource(R.drawable.amap_route_color_texture_4_arrow)
        unknownTraffic =
            BitmapDescriptorFactory.fromResource(R.drawable.amap_route_color_texture_0_arrow)
        slowTraffic =
            BitmapDescriptorFactory.fromResource(R.drawable.amap_route_color_texture_3_arrow)
        jamTraffic =
            BitmapDescriptorFactory.fromResource(R.drawable.amap_route_color_texture_2_arrow)
        veryJamTraffic =
            BitmapDescriptorFactory.fromResource(R.drawable.amap_route_color_texture_9_arrow)
    }

    private fun getTrafficBitmapDescriptor(status: String): BitmapDescriptor? {
//        if (status.trim().equals("未知")) {
//            return unknownTraffic;
//        } else
        Log.e("ggb", "==> 路况信息 is $status")
        return if (status == "畅通") {
            smoothTraffic
        } else if (status == "缓行") {
            slowTraffic
        } else if (status == "拥堵") {
            jamTraffic
        } else if (status == "严重拥堵") {
            veryJamTraffic
        } else {
            defaultRoute
        }
    }

    private fun getColor(status: String): Int {
        return when (status) {
            "畅通" -> {
                Color.GREEN
            }
            "缓行" -> {
                Color.YELLOW
            }
            "拥堵" -> {
                Color.RED
            }
            "严重拥堵" -> {
                Color.parseColor("#990033")
            }
            else -> {
                Color.parseColor("#537edc")
            }
        }
    }

    fun convertToLatLng(point: LatLonPoint): LatLng {
        return LatLng(point.latitude, point.longitude)
    }

    private fun addDrivingStationMarkers(driveStep: DriveStep, latLng: LatLng) {
        addStationMarker(
            MarkerOptions()
                .position(latLng)
                .title(
                    """
                        方向:${driveStep.action}
                        道路:${driveStep.road}
                        """.trimIndent()
                )
                .snippet(driveStep.instruction).visible(nodeIconVisible)
                .anchor(0.5f, 0.5f).icon(getDriveBitmapDescriptor())
        )
    }

    override fun getLatLngBounds(): LatLngBounds {
        val b = LatLngBounds.builder()
        b.include(LatLng(startPoint!!.latitude, startPoint!!.longitude))
        b.include(LatLng(endPoint!!.latitude, endPoint!!.longitude))
        if (throughPointList != null && throughPointList!!.isNotEmpty()) {
            for (i in throughPointList!!.indices) {
                b.include(
                    LatLng(
                        throughPointList!![i].latitude,
                        throughPointList!![i].longitude
                    )
                )
            }
        }
        return b.build()
    }

    fun setThroughPointIconVisibility(visible: Boolean) {
        try {
            throughPointMarkerVisible = visible
            if (throughPointMarkerList.isNotEmpty()) {
                for (i in throughPointMarkerList.indices) {
                    throughPointMarkerList[i].isVisible = visible
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun addThroughPointMarker() {
        if (throughPointList != null && throughPointList!!.isNotEmpty()) {
            var latLonPoint: LatLonPoint? = null
            for (i in throughPointList!!.indices) {
                latLonPoint = throughPointList!![i]
                aMap?.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                latLonPoint
                                    .latitude, latLonPoint
                                    .longitude
                            )
                        )
                        .visible(throughPointMarkerVisible)
                        .icon(getThroughPointBitDes())
                        .title("\u9014\u7ECF\u70B9")
                )?.let {
                    throughPointMarkerList.add(
                        it
                    )
                }
            }
        }
    }

    private fun getThroughPointBitDes(): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_through)
    }

    fun calculateDistance(start: LatLng, end: LatLng): Int {
        val x1 = start.longitude
        val y1 = start.latitude
        val x2 = end.longitude
        val y2 = end.latitude
        return calculateDistance(x1, y1, x2, y2)
    }

    fun calculateDistance(
        x1: Double,
        y1: Double,
        x2: Double,
        y2: Double
    ): Int {
        var x1 = x1
        var y1 = y1
        var x2 = x2
        var y2 = y2
        val NF_pi = 0.01745329251994329 // 弧度 PI/180
        x1 *= NF_pi
        y1 *= NF_pi
        x2 *= NF_pi
        y2 *= NF_pi
        val sinx1 = Math.sin(x1)
        val siny1 = Math.sin(y1)
        val cosx1 = Math.cos(x1)
        val cosy1 = Math.cos(y1)
        val sinx2 = Math.sin(x2)
        val siny2 = Math.sin(y2)
        val cosx2 = Math.cos(x2)
        val cosy2 = Math.cos(y2)
        val v1 = DoubleArray(3)
        v1[0] = cosy1 * cosx1 - cosy2 * cosx2
        v1[1] = cosy1 * sinx1 - cosy2 * sinx2
        v1[2] = siny1 - siny2
        val dist = Math.sqrt(
            v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]
        )
        return (Math.asin(dist / 2) * 12742001.5798544).toInt()
    }


    //获取指定两点之间固定距离点
    fun getPointForDis(sPt: LatLng, ePt: LatLng, dis: Double): LatLng? {
        val lSegLength = calculateDistance(sPt, ePt).toDouble()
        val preResult = dis / lSegLength
        return LatLng(
            (ePt.latitude - sPt.latitude) * preResult + sPt.latitude,
            (ePt.longitude - sPt.longitude) * preResult + sPt.longitude
        )
    }

    /**
     * 去掉DriveLineOverlay上的线段和标记。
     */
    override fun removeFromMap() {
        try {
            super.removeFromMap()
            if (throughPointMarkerList.size > 0) {
                for (i in throughPointMarkerList.indices) {
                    throughPointMarkerList[i].remove()
                }
                throughPointMarkerList.clear()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


}