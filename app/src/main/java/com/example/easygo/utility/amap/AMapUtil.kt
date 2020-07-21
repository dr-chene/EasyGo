package com.example.easygo.utility.amap

import android.annotation.SuppressLint
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.BusPath
import com.example.easygo.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by chene on @date 2020/7/18、
 * amap包下的全是高德官方的东西，我只是“翻译”成了kotlin而已
 */
class AMapUtil {
    companion object {
        fun getFriendlyLength(lenMeter: Int): String {
            if (lenMeter > 10000) // 10 km
            {
                val dis: Int = lenMeter / 1000
                return "${dis}${ChString.Kilometer}"
            }
            if (lenMeter > 1000) {
                val dis = lenMeter.toFloat() / 1000
                val fnum = DecimalFormat("##0.0")
                val dstr = fnum.format(dis.toDouble())
                return "${dstr}{${ChString.Kilometer}}"
            }
            if (lenMeter > 100) {
                val dis: Int = lenMeter / 50 * 50
                return "$dis${ChString.Meter}"
            }
            var dis: Int = lenMeter / 10 * 10
            if (dis == 0) {
                dis = 10
            }
            return "$dis${ChString.Meter}"
        }

        fun getFriendlyTime(second: Int): String {
            if (second > 3600) {
                val hour = second / 3600
                val min = second % 3600 / 60
                return "${hour}小时${min} 分钟"
            }
            if (second >= 60) {
                val min = second / 60
                return "${min}分钟"
            }
            return "${second}秒"
        }

        @SuppressLint("SimpleDateFormat")
        fun convertToTime(time: Long): String {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = Date(time)
            return df.format(date)
        }

        fun convertToLatLonPoint(latLng: LatLng): LatLonPoint {
            return LatLonPoint(latLng.latitude, latLng.longitude)
        }

        fun convertToLatLng(latLonPoint: LatLonPoint): LatLng {
            return LatLng(latLonPoint.latitude, latLonPoint.longitude)
        }

        fun getDriveActionID(actionName: String?): Int {
            if (actionName == null || actionName == "") {
                return R.drawable.dir3
            }
            if ("左转" == actionName) {
                return R.drawable.dir2
            }
            if ("右转" == actionName) {
                return R.drawable.dir1
            }
            if ("向左前方行驶" == actionName || "靠左" == actionName) {
                return R.drawable.dir6
            }
            if ("向右前方行驶" == actionName || "靠右" == actionName) {
                return R.drawable.dir5
            }
            if ("向左后方行驶" == actionName || "左转调头" == actionName) {
                return R.drawable.dir7
            }
            if ("向右后方行驶" == actionName) {
                return R.drawable.dir8
            }
            if ("直行" == actionName) {
                return R.drawable.dir3
            }
            return if ("减速行驶" == actionName) {
                R.drawable.dir4
            } else R.drawable.dir3
        }

        fun getWalkActionID(actionName: String?): Int {
            if (actionName == null || actionName == "") {
                return R.drawable.dir13
            }
            if ("左转" == actionName) {
                return R.drawable.dir2
            }
            if ("右转" == actionName) {
                return R.drawable.dir1
            }
            if ("向左前方" == actionName || "靠左" == actionName || actionName.contains("向左前方")) {
                return R.drawable.dir6
            }
            if ("向右前方" == actionName || "靠右" == actionName || actionName.contains("向右前方")) {
                return R.drawable.dir5
            }
            if ("向左后方" == actionName || actionName.contains("向左后方")) {
                return R.drawable.dir7
            }
            if ("向右后方" == actionName || actionName.contains("向右后方")) {
                return R.drawable.dir8
            }
            if ("直行" == actionName) {
                return R.drawable.dir3
            }
            if ("通过人行横道" == actionName) {
                return R.drawable.dir9
            }
            if ("通过过街天桥" == actionName) {
                return R.drawable.dir11
            }
            return if ("通过地下通道" == actionName) {
                R.drawable.dir10
            } else R.drawable.dir13
        }

        fun convertArrList(shapes: List<LatLonPoint?>): ArrayList<LatLng>? {
            val lineShapes = ArrayList<LatLng>()
            for (point in shapes) {
                val latLngTemp = convertToLatLng(point!!)
                lineShapes.add(latLngTemp)
            }
            return lineShapes
        }

        fun getBusPathTitle(busPath: BusPath?): String? {
            if (busPath == null) {
                return ""
            }
            val busSetps = busPath.steps ?: return ""
            val sb = StringBuffer()
            for (busStep in busSetps) {
                val title = StringBuffer()
                if (busStep.busLines.size > 0) {
                    for (busline in busStep.busLines) {
                        if (busline == null) {
                            continue
                        }
                        val buslineName =
                            getSimpleBusLineName(busline.busLineName)
                        title.append(buslineName)
                        title.append(" / ")
                    }
                    //					RouteBusLineItem busline = busStep.getBusLines().get(0);
                    sb.append(title.substring(0, title.length - 3))
                    sb.append(" > ")
                }
                if (busStep.railway != null) {
                    val railway = busStep.railway
                    sb.append(
                        railway.trip + "(" + railway.departurestop.name
                                + " - " + railway.arrivalstop.name + ")"
                    )
                    sb.append(" > ")
                }
            }
            return sb.substring(0, sb.length - 3)
        }

        fun getBusPathDes(busPath: BusPath?): String? {
            if (busPath == null) {
                return ""
            }
            val second = busPath.duration
            val time = getFriendlyTime(second.toInt())
            val subDistance = busPath.distance
            val subDis = getFriendlyLength(subDistance.toInt())
            val walkDistance = busPath.walkDistance
            val walkDis = getFriendlyLength(walkDistance.toInt())
            return "$time | $subDis | 步行$walkDis"
        }

        fun getSimpleBusLineName(busLineName: String?): String {
            return busLineName?.replace("\\(.*?\\)".toRegex(), "") ?: ""
        }

    }
}