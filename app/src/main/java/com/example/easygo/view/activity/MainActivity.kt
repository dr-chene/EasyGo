package com.example.easygo.view.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.help.Tip
import com.amap.api.services.route.RouteSearch
import com.example.easygo.App
import com.example.easygo.R
import com.example.easygo.databinding.ActivityMainBinding
import com.example.easygo.network.Network
import com.example.easygo.utility.*
import com.example.easygo.view.fragment.*
import com.example.easygo.viewmodel.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/*
*主要交互界面，地图的所在
* 权限请求，地图初始化，进应用后的定位，使用动画切换到全屏显示地图
* 监听参数的改变实现界面的跳转
* 对地图操作的传递
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var locationClient: AMapLocationClient
    lateinit var sheetBehavior: BottomSheetBehavior<FragmentContainerView>
    private lateinit var routeViewModel: RouteViewModel

    private lateinit var aMap: AMap
    private lateinit var mapView: MapView

    private lateinit var aMapViewModel: AMapViewModel
    private lateinit var tipAndPoiSearchViewModel: TipAndPoiSearchViewModel
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var searchHistoryViewModel: SearchHistoryViewModel
    private lateinit var progressBar: List<View>

    private var isLoadTraffic = true
    private var isFullMap = false
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        routeViewModel = ViewModelProvider(this).get(RouteViewModel::class.java)

        aMapViewModel = ViewModelProvider(this).get(AMapViewModel::class.java)
        tipAndPoiSearchViewModel = ViewModelProvider(this).get(TipAndPoiSearchViewModel::class.java)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        searchHistoryViewModel = InjectorUtils.provideSearchHistoryViewModelFactory(this)
            .create(SearchHistoryViewModel::class.java)

        initAMap(savedInstanceState)
        initLocate()
        initView()
        setClickListener()
        subscribe()
        initPermissions(this, this)
        EventBus.getDefault().register(this)
    }

    private fun initLocate() {
        moveCameraToCenter(16f)
    }

    private fun moveCameraToCenter(zoomScale: Float) {
        getSharedPreferences(App.LOCATION, Context.MODE_PRIVATE).apply {
            val latitude = getFloat("latitude", 39.904989f).toDouble()
            val longitude = getFloat("longitude", 116.405285f).toDouble()
            aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomScale))
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(latitude, longitude)))
        }
    }

    private fun initAMap(savedInstanceState: Bundle?) {
        mapView = binding.activityMainMap
        mapView.onCreate(savedInstanceState)
        aMap = mapView.map.apply {
            showIndoorMap(true)
            addOnMapClickListener {
                changeMapState()
            }
            isMyLocationEnabled = true
        }
        aMapViewModel.showPointInMap(aMap, this)
        aMapViewModel.initUiControls(aMap)

        locationClient = AMapLocationClient(applicationContext)
    }

    private fun locate() {
        aMapViewModel.locate(locationClient, aMap, this)
    }

    private fun initView() {
        progressBar = listOf(
            binding.activityMainProgressDialog,
            binding.activityMainProgressBar,
            binding.activityMainProgressDialogBg,
            binding.activityMainProgressDialogTv
        )
        sheetBehavior = BottomSheetBehavior.from(binding.activityMainBottomSheetContainer).apply {
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        hideKeyBord()
                    }
                }
            })
        }
        binding.activityMainTabsBottom.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (tab.text) {
                        resources.getString(
                            R.string.activity_main_tabs_bottom_text_home
                        ) -> {
                            nav(NormalFragment())
                        }
                        resources.getString(
                            R.string.activity_main_tabs_bottom_text_travel
                        ) -> {
                            navToTravelActivity()
                        }
                        resources.getString(
                            R.string.activity_main_tabs_bottom_text_mine
                        ) -> {
                            Intent(this@MainActivity, MineActivity::class.java).apply {
                                startActivity(this)
                            }
                        }
                        else -> {
                        }
                    }
                }
            }

        })
    }

    private fun setClickListener() {
        binding.layerClick = View.OnClickListener {
            aMapViewModel.changeLayer(binding.root.context, aMap, it)
        }
        binding.trafficClick = View.OnClickListener {
            if (isLoadTraffic) {
                isLoadTraffic = false
                mainActivityViewModel.setLoadTraffic(isLoadTraffic)
            } else {
                isLoadTraffic = true
                mainActivityViewModel.setLoadTraffic(isLoadTraffic)
                MyToast.showToast("开启并更新实时路况")
            }
        }
        binding.cancelClick = View.OnClickListener {
            aMap.clear()
            onBackPressed()
            binding.activityMainSearchResultBar.visibility = View.GONE
        }
        binding.backClick = View.OnClickListener {
            mainActivityViewModel.navTo(App.FRAGMENT_SEARCHING)
            binding.activityMainSearchResultBar.visibility = View.GONE
        }
        binding.locateClick = View.OnClickListener {
            moveCameraToCenter(17f)
        }
    }

    private fun changeMapState() {
        if (binding.activityMainSearchResultBar.visibility == View.GONE) {
            if (isFullMap) {
                isFullMap = false
                mainActivityViewModel.setFullMap(isFullMap)
            } else {
                isFullMap = true
                mainActivityViewModel.setFullMap(isFullMap)
            }
        }
    }

    private fun subscribe() {
        mainActivityViewModel.isFullMap.observe(this) {
            if (it) {
                toFullMap()
            } else {
                toNormalMap()
            }
        }
        mainActivityViewModel.isLoadTraffic.observe(this) {
            if (it) {
                binding.activityMainFabTraffic.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_activity_main_fab_traffic_true,
                        resources.newTheme()
                    )
                )
            } else {
                binding.activityMainFabTraffic.setImageDrawable(
                    resources.getDrawable(
                        R.drawable.ic_activity_main_fab_traffic_fasle,
                        resources.newTheme()
                    )
                )
                MyToast.showToast("关闭实时路况")
            }
            aMap.isTrafficEnabled = it
        }
        mainActivityViewModel.nav.observe(this) {
            when (it) {
                App.FRAGMENT_NORMAL -> {
                    sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    nav(NormalFragment())
                    aMap.clear()
                }
                App.FRAGMENT_SEARCHING -> {
                    sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    nav(SearchingFragment())
                }
                App.FRAGMENT_SEARCHED -> {
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    nav(SearchedFragment())
                }
                App.FRAGMENT_POI -> {
                    sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    nav(PoiFragment())
                }
                App.FRAGMENT_ROUTE -> {
                    sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    nav(RouteFragment())
                }
            }
        }
        tipAndPoiSearchViewModel.poiItems.observe(this) {
            showSearchResultInMap(it, aMap)
            mainActivityViewModel.navTo(App.FRAGMENT_SEARCHED)
            progressBar.forEach { view ->
                view.visibility = View.GONE
            }
        }
        tipAndPoiSearchViewModel.poiItem.observe(this) {
            searchHistoryViewModel.insertSearchHistory(MyConverters.poiItemToSearchHisTory(it))
            mainActivityViewModel.navTo(App.FRAGMENT_POI)
        }
        tipAndPoiSearchViewModel.tip.observe(this) {
            hideKeyBord()
            if (Network.isNetworkAvailable(this)) {
                progressBar.forEach { view ->
                    view.visibility = View.VISIBLE
                }
                mainActivityViewModel.navTo(App.FRAGMENT_SEARCHED)
                searchHistoryViewModel.insertSearchHistory(MyConverters.tipToSearchHistory(it))
                if (it.poiID.isNullOrEmpty()) {
                    startSearching(it.name)
                } else {
                    addTipMarker(it)
                }
            } else {
                MyToast.showToast("当前网络不可用")
            }
        }
        routeViewModel.driveSteps.observe(this) {
            progressBar.forEach {
                it.visibility = View.GONE
            }
            mainActivityViewModel.navTo(App.FRAGMENT_ROUTE)
        }
        routeViewModel.rideSteps.observe(this) {
            progressBar.forEach {
                it.visibility = View.GONE
            }
            mainActivityViewModel.navTo(App.FRAGMENT_ROUTE)
        }
        routeViewModel.walkSteps.observe(this) {
            progressBar.forEach {
                it.visibility = View.GONE
            }
            mainActivityViewModel.navTo(App.FRAGMENT_ROUTE)
        }
        mainActivityViewModel.navToPoi.observe(this) {
            hideKeyBord()
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if (Network.isNetworkAvailable(this)) {
                progressBar.forEach { view ->
                    view.visibility = View.VISIBLE
                }
                getSharedPreferences(App.LOCATION, Context.MODE_PRIVATE).apply {
                    val latitude = getFloat("latitude", 39.904989f).toDouble()
                    val longitude = getFloat("longitude", 116.405285f).toDouble()
                    startNav(
                        LatLonPoint(latitude, longitude),
                        mainActivityViewModel.navToPoi.value!!
                    )
                }
            } else {
                MyToast.showToast("当前网络不可用")
            }
        }
    }

    private fun toFullMap() {
        binding.activityMainFabTraffic.hide()
        binding.activityMainFabLayer.hide()
        binding.activityMainFabLocate.hide()
        val tabHeight = binding.activityMainTabsBottom.height
        val tabAnim = ObjectAnimator.ofFloat(
            binding.activityMainTabsBottom,
            "translationY",
            tabHeight.toFloat()
        ).apply {
            duration = 100
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    binding.activityMainTabsBottom.visibility = View.GONE
                }
            })
        }
        val containerHeight = binding.activityMainBottomSheetContainer.height
        val containerAnim = ObjectAnimator.ofFloat(
            binding.activityMainBottomSheetContainer,
            "translationY",
            containerHeight.toFloat() / 2
        ).apply {
            duration = 200
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    binding.activityMainBottomSheetContainer.visibility = View.GONE
                }
            })
        }
        AnimatorSet().apply {
            play(containerAnim).before(tabAnim)
            start()
        }
    }

    private fun toNormalMap() {
        binding.activityMainFabLayer.show()
        binding.activityMainFabTraffic.show()
        binding.activityMainFabLocate.show()
        val tabAnim =
            ObjectAnimator.ofFloat(binding.activityMainTabsBottom, "translationY", 0f).apply {
                duration = 100
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        binding.activityMainTabsBottom.visibility = View.VISIBLE
                    }
                })
            }
        val containerAnim =
            ObjectAnimator.ofFloat(binding.activityMainBottomSheetContainer, "translationY", 0f)
                .apply {
                    duration = 200
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            binding.activityMainBottomSheetContainer.visibility = View.VISIBLE
                        }
                    })
                }
        AnimatorSet().apply {
            play(tabAnim).before(containerAnim)
            start()
        }
    }

    private fun navToTravelActivity() {
        startActivity(Intent(this, TravelActivity::class.java))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        locationClient.stopLocation()
    }

    override fun onResume() {
        super.onResume()
        binding.activityMainTabsBottom.apply {
            selectTab(getTabAt(0))
        }
        progressBar.forEach {
            it.visibility = View.GONE
        }
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        locationClient.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (toast == null) {
            toast = Toast.makeText(App.context, "再按一次退出易行地图", Toast.LENGTH_SHORT)
            toast?.show()
        } else {
            return super.onKeyDown(keyCode, event)
        }
        return false
    }

    private fun startSearching(keyWords: String) {
        binding.activityMainSearchResultBar.apply {
            visibility = View.VISIBLE
            binding.activityMainSearchResultTv.text = keyWords
        }
        tipAndPoiSearchViewModel.startSearching(keyWords, this)
    }


    private fun addTipMarker(tip: Tip?) {
        if (tip == null) return
        aMapViewModel.addTipMarker(tip, aMap)
        searchPoiById(tip)
    }

    private fun searchPoiById(tip: Tip) {
        binding.activityMainSearchResultBar.apply {
            visibility = View.VISIBLE
            binding.activityMainSearchResultTv.text = tip.name
        }
        tipAndPoiSearchViewModel.searchPoiById(tip, this)
    }

    private fun showSearchResultInMap(poiItems: List<PoiItem>, aMap: AMap) {
        aMapViewModel.showSearchResultInMap(poiItems, aMap)
    }

    fun nav(fragment: Fragment) {
        FragmentNavUtil.nav(
            supportFragmentManager, fragment
        )
    }

    private fun startRouteSearch(
        context: Context,
        fromAndTo: RouteSearch.FromAndTo,
        searchType: Int
    ) {
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        if (Network.isNetworkAvailable(this)) {
            progressBar.forEach {
                it.visibility = View.VISIBLE
            }
            routeViewModel.searchRouteResult(searchType, 0, fromAndTo, context, aMap)
        } else {
            MyToast.showToast("当前网络不可用")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun searchRouteEvent(fromAndToMessageEvent: FromAndToMessageEvent) {
        startRouteSearch(
            this,
            fromAndToMessageEvent.getFromAndTo(),
            fromAndToMessageEvent.getSearchType()
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun keyWordSearchEvent(keyWordSearchEvent: KeyWordSearchEvent) {
        startSearching(keyWordSearchEvent.getKeyWord())
    }

    private fun hideKeyBord() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            window.decorView.windowToken, 0
        )
    }

    private fun startNav(start: LatLonPoint, end: LatLonPoint) {
        val intent = Intent(this, RouteNaviActivity::class.java).apply {
            putExtra("gps", false)
            putExtra("start", NaviLatLng(start.latitude, start.longitude))
            putExtra("end", NaviLatLng(end.latitude, start.longitude))
        }
        startActivity(intent)
    }

    private fun initPermissions(context: Context, activity: Activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
        } else {
            locate()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            200 -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    MyToast.showToast("开启定位权限失败，请重试或手动开启")
                } else {
                    locate()
                }
            }
        }
    }

}