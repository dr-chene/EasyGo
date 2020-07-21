package com.example.easygo.view.activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.AMapNaviView
import com.amap.api.navi.AMapNaviViewListener
import com.amap.api.navi.model.*
import com.autonavi.tbt.TrafficFacilityInfo
import com.example.easygo.R

class RouteNaviActivity : AppCompatActivity(), AMapNaviListener, AMapNaviViewListener {
    var mAMapNaviView: AMapNaviView? = null
    var mAMapNavi: AMapNavi? = null
    var mIsGps = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_route_navi)
        mAMapNaviView = findViewById<View>(R.id.activity_route_navi_view) as AMapNaviView
        mAMapNaviView?.onCreate(savedInstanceState)
        mAMapNaviView?.setAMapNaviViewListener(this)
        mAMapNavi = AMapNavi.getInstance(applicationContext)
        mAMapNavi?.addAMapNaviListener(this)
        mAMapNavi?.setUseInnerVoice(true)
        mAMapNavi?.setEmulatorNaviSpeed(60)
        naviParam
    }

    /**
     * 获取intent参数并计算路线
     */
    private val naviParam: Unit
        get() {
            val intent = intent ?: return
            mIsGps = intent.getBooleanExtra("gps", false)
            val start: NaviLatLng = intent.getParcelableExtra("start")
            val end: NaviLatLng = intent.getParcelableExtra("end")
            calculateDriveRoute(start, end)
        }

    /**
     * 驾车路径规划计算,计算单条路径
     */
    private fun calculateDriveRoute(start: NaviLatLng, end: NaviLatLng) {
        var strategyFlag = 0
        val startList: MutableList<NaviLatLng> = ArrayList<NaviLatLng>()

        /**
         * 途径点坐标集合
         */
        val wayList: List<NaviLatLng> = ArrayList<NaviLatLng>()

        /**
         * 终点坐标集合［建议就一个终点］
         */
        val endList: MutableList<NaviLatLng> = ArrayList<NaviLatLng>()
        try {
            strategyFlag = mAMapNavi!!.strategyConvert(true, false, false, true, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        startList.add(start)
        endList.add(end)
        mAMapNavi?.calculateDriveRoute(startList, endList, wayList, strategyFlag)
    }

    override fun onResume() {
        super.onResume()
        mAMapNaviView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mAMapNaviView?.onPause()
        //
        //        停止导航之后，会触及底层stop，然后就不会再有回调了，但是讯飞当前还是没有说完的半句话还是会说完
        //        mAMapNavi.stopNavi();
    }

    override fun onDestroy() {
        super.onDestroy()
        mAMapNaviView?.onDestroy()
        mAMapNavi?.stopNavi()
        //		mAMapNavi.destroy();
    }

    override fun onInitNaviFailure() {
        Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show()
    }

    override fun onInitNaviSuccess() {}
    override fun onStartNavi(type: Int) {}
    override fun onTrafficStatusUpdate() {}
    override fun onLocationChange(location: AMapNaviLocation?) {}
    override fun onGetNavigationText(type: Int, text: String?) {}
    override fun onGetNavigationText(s: String?) {}
    override fun onEndEmulatorNavi() {}
    override fun onArriveDestination() {}
    override fun onCalculateRouteFailure(errorInfo: Int) {}
    override fun onCalculateRouteFailure(p0: AMapCalcRouteResult?) {}

    override fun onReCalculateRouteForYaw() {}
    override fun onReCalculateRouteForTrafficJam() {}
    override fun onArrivedWayPoint(wayID: Int) {}
    override fun onGpsOpenStatus(enabled: Boolean) {}
    override fun onNaviSetting() {}
    override fun onNaviMapMode(isLock: Int) {}
    override fun onNaviCancel() {
        finish()
    }

    override fun onNaviTurnClick() {}
    override fun onNextRoadClick() {}
    override fun onNaviViewShowMode(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onScanViewButtonClick() {}

    @Deprecated("")
    override fun onNaviInfoUpdated(naviInfo: AMapNaviInfo?) {
    }

    override fun updateCameraInfo(aMapNaviCameraInfos: Array<AMapNaviCameraInfo?>?) {}
    override fun updateIntervalCameraInfo(
        aMapNaviCameraInfo: AMapNaviCameraInfo?,
        aMapNaviCameraInfo1: AMapNaviCameraInfo?,
        i: Int
    ) {
    }

    override fun onServiceAreaUpdate(aMapServiceAreaInfos: Array<AMapServiceAreaInfo?>?) {}
    override fun onGpsSignalWeak(p0: Boolean) {}

    override fun onNaviInfoUpdate(naviinfo: NaviInfo?) {}
    override fun OnUpdateTrafficFacility(trafficFacilityInfo: TrafficFacilityInfo?) {}
    override fun onNaviRouteNotify(p0: AMapNaviRouteNotifyData?) {}

    override fun OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfo: AMapNaviTrafficFacilityInfo?) {}
    override fun showCross(aMapNaviCross: AMapNaviCross?) {}
    override fun hideCross() {}
    override fun showModeCross(aMapModelCross: AMapModelCross?) {}
    override fun hideModeCross() {}
    override fun showLaneInfo(
        laneInfos: Array<AMapLaneInfo?>?,
        laneBackgroundInfo: ByteArray?,
        laneRecommendedInfo: ByteArray?
    ) {
    }

    override fun showLaneInfo(aMapLaneInfo: AMapLaneInfo?) {}
    override fun hideLaneInfo() {}
    override fun onCalculateRouteSuccess(ints: IntArray?) {
        if (mIsGps) {
            mAMapNavi?.startNavi(AMapNavi.GPSNaviMode)
        } else {
            mAMapNavi?.startNavi(AMapNavi.EmulatorNaviMode)
        }
    }

    override fun onCalculateRouteSuccess(p0: AMapCalcRouteResult?) {}

    override fun notifyParallelRoad(i: Int) {}
    override fun OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfos: Array<AMapNaviTrafficFacilityInfo?>?) {}
    override fun updateAimlessModeStatistics(aimLessModeStat: AimLessModeStat?) {}
    override fun updateAimlessModeCongestionInfo(aimLessModeCongestionInfo: AimLessModeCongestionInfo?) {}
    override fun onPlayRing(i: Int) {}
    override fun onLockMap(isLock: Boolean) {}
    override fun onMapTypeChanged(p0: Int) {}

    override fun onNaviViewLoaded() {}
    override fun onNaviBackClick(): Boolean {
        return false
    }
}