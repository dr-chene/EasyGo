package com.example.easygo.utility.amap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.example.easygo.R
import java.util.*


/**
 * Created by chene on @date 2020/7/18
 */
open class RouteOverlay(
    val context: Context
) {
    protected var stationMarkers: MutableList<Marker> = ArrayList()
    protected var allPolyLines: MutableList<Polyline> = ArrayList()
    protected var startMarker: Marker? = null
    protected var endMarker: Marker? = null
    protected var startPoint: LatLng? = null
    protected var endPoint: LatLng? = null
    protected var aMap: AMap? = null
    private var startBit: Bitmap? = null
    private var endBit: Bitmap? = null
    private var busBit: Bitmap? = null
    private var walkBit: Bitmap? = null
    private var driveBit: Bitmap? = null
    protected var nodeIconVisible = true

    open fun removeFromMap() {
        if (startMarker != null) {
            startMarker?.remove()
        }
        if (endMarker != null) {
            endMarker?.remove()
        }
        for (marker in stationMarkers) {
            marker.remove()
        }
        for (line in allPolyLines) {
            line.remove()
        }
        destroyBit()
    }

    private fun destroyBit() {
        if (startBit != null) {
            startBit?.recycle()
            startBit = null
        }
        if (endBit != null) {
            endBit?.recycle()
            endBit = null
        }
        if (busBit != null) {
            busBit?.recycle()
            busBit = null
        }
        if (walkBit != null) {
            walkBit?.recycle()
            walkBit = null
        }
        if (driveBit != null) {
            driveBit?.recycle()
            driveBit = null
        }
    }

    protected open fun getStartBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_start)
    }

    protected open fun getEndBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_end)
    }

    protected open fun getBusBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_bus)
    }

    protected open fun getWalkBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_man)
    }

    protected open fun getDriveBitmapDescriptor(): BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_car)
    }

    protected open fun getRideBitmapDescriptor(): BitmapDescriptor? {
        return BitmapDescriptorFactory.fromResource(R.drawable.amap_ride)
    }

    protected open fun addStartAndEndMarker() {
        startMarker = aMap?.addMarker(
            MarkerOptions()
                .position(startPoint).icon(getStartBitmapDescriptor())
                .title("\u8D77\u70B9")
        )
        // startMarker.showInfoWindow();
        endMarker = aMap?.addMarker(
            MarkerOptions().position(endPoint)
                .icon(getEndBitmapDescriptor()).title("\u7EC8\u70B9")
        )
        // mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,
        // getShowRouteZoom()));
    }

    open fun zoomToSpan() {
        if (startPoint != null) {
            if (aMap == null) return
            try {
                val bounds: LatLngBounds = getLatLngBounds()
                aMap?.animateCamera(
                    CameraUpdateFactory
                        .newLatLngBounds(bounds, 50)
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    protected open fun getLatLngBounds(): LatLngBounds {
        val b = LatLngBounds.builder()
        b.include(LatLng(startPoint!!.latitude, startPoint!!.longitude))
        b.include(LatLng(endPoint!!.latitude, endPoint!!.longitude))
        for (polyline in allPolyLines) {
            for (point in polyline.points) {
                b.include(point)
            }
        }
        return b.build()
    }

    open fun setNodeIconVisibility(visible: Boolean) {
        try {
            nodeIconVisible = visible
            if (!stationMarkers.isNullOrEmpty()) {
                for (i in stationMarkers.indices) {
                    stationMarkers[i].isVisible = visible
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    protected open fun addStationMarker(options: MarkerOptions?) {
        if (options == null) {
            return
        }
        val marker: Marker? = aMap?.addMarker(options)
        if (marker != null) {
            stationMarkers.add(marker)
        }
    }

    protected open fun addPolyLine(options: PolylineOptions?) {
        if (options == null) {
            return
        }
        val polyline: Polyline? = aMap?.addPolyline(options)
        if (polyline != null) {
            allPolyLines.add(polyline)
        }
    }

    protected open fun getRouteWidth(): Float {
        return 18f
    }

    protected open fun getWalkColor(): Int {
        return Color.parseColor("#6db74d")
    }

    protected open fun getBusColor(): Int {
        return Color.parseColor("#537edc")
    }

    protected open fun getDriveColor(): Int {
        return Color.parseColor("#537edc")
    }

    protected open fun getRideColor(): Int {
        return Color.parseColor("#537edc")
    }

}