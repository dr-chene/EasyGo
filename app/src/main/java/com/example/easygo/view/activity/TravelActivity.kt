package com.example.easygo.view.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.amap.api.services.route.RouteSearch
import com.example.easygo.App
import com.example.easygo.App.Companion.context
import com.example.easygo.R
import com.example.easygo.bean.SearchHistory
import com.example.easygo.databinding.ActivityTravelBinding
import com.example.easygo.network.Network
import com.example.easygo.utility.FromAndToMessageEvent
import com.example.easygo.utility.InjectorUtils
import com.example.easygo.utility.MyToast
import com.example.easygo.view.adapter.TravelPopWindowRecyclerViewAdapter
import com.example.easygo.view.adapter.TravelSearchHistoryRecyclerViewAdapter
import com.example.easygo.view.adapter.TravelSearchRecyclerViewAdapter
import com.example.easygo.viewmodel.SearchHistoryViewModel
import com.example.easygo.viewmodel.TipAndPoiSearchViewModel
import com.google.android.material.tabs.TabLayout
import org.greenrobot.eventbus.EventBus

/*
 *出行界面
 * 实现输入出发位置，前往位置的搜寻，选择，然后返回MainActivity搜寻并展示路径的规划结果
 * 但是由于上下两个ET是可以交换位置的，导致写了一系列的方法来确定当前的焦点，当前的tip是否是一个准确的地点之类的
 * 本来是一气呵成，没有什么bug的，但由于后来新加了默认“我的位置”功能，导致有些混乱以及错误的逻辑
 * 但我也没有精力和事件去改了
 */
class TravelActivity : AppCompatActivity(), Inputtips.InputtipsListener {

    private lateinit var binding: ActivityTravelBinding
    private var isAnimFinish = true
    private var tipFrom: Tip? = null
    private var tipTo: Tip? = null
    private var isToFocus = false
    private var isFromFocus = false
    private lateinit var tipAndPoiSearchViewModel: TipAndPoiSearchViewModel
    private lateinit var searchHistoryViewModel: SearchHistoryViewModel
    private var isPopWindow = false
    private var searchType = App.TYPE_DRIVE
    private var getMyLocateForm = false
    private var getMyLocateTo = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_travel)
        tipAndPoiSearchViewModel = ViewModelProvider(this).get(TipAndPoiSearchViewModel::class.java)
        searchHistoryViewModel = InjectorUtils.provideSearchHistoryViewModelFactory(this)
            .create(SearchHistoryViewModel::class.java)

        initView()
        subscribe()
        setClickListener()

    }

    private fun initView() {
        setUpETListener()
        searchType = intent.getIntExtra("travel_way", App.TYPE_DRIVE)
        binding.activityTravelTabs.apply {
            selectTab(getTabAt(searchType))
        }
        showSearchHistory()
    }

    private fun showSearchHistory() {
        searchHistoryViewModel.searchHistoryList.observe(this) {
            val adapter = TravelSearchHistoryRecyclerViewAdapter()
            binding.activityTravelRvSearchResult.adapter = adapter
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
    }

    private fun subscribe() {
        tipAndPoiSearchViewModel.tip.observe(this) {
            binding.activityTravelPopWindow.visibility = View.GONE
            if (it.poiID != null) {
                if (isFromFocus) {
                    if (haveChanged()) {
                        tipTo = it
                        binding.activityTravelEtTo.setText(it.name)
                    } else {
                        tipFrom = it
                        binding.activityTravelEtFrom.setText(it.name)
                    }
                } else if (isToFocus) {
                    if (haveChanged()) {
                        tipFrom = it
                        binding.activityTravelEtFrom.setText(it.name)
                    } else {
                        tipTo = it
                        binding.activityTravelEtTo.setText(it.name)
                    }
                }
            } else {
                getPopWindowTip(it.name)
            }
        }

    }

    private fun getPopWindowTip(tipName: String?) {
        if (tipName == null) return
        isPopWindow = true
        startTipSearch(tipName)
    }

    private fun showPopWindow(p0: MutableList<Tip>?) {
        isPopWindow = false
        if (p0.isNullOrEmpty()) return
        val tipList = p0.filter {
            null != it.poiID
        }
        if (tipList.isNullOrEmpty()) {
            Toast.makeText(context, "无返回数据，请重新输入关键字", Toast.LENGTH_SHORT).show()
            return
        }
        val adapter = TravelPopWindowRecyclerViewAdapter()
        binding.activityTravelPopWindowRv.adapter = adapter
        adapter.submitList(tipList)
        binding.activityTravelPopWindow.visibility = View.VISIBLE
    }

    private fun setUpETListener() {
        binding.activityTravelEtTo.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        if (Network.isNetworkAvailable(this@TravelActivity)) {
                            startTipSearch(s.toString())
                        } else {
                            MyToast.showToast("当前网络不可用")
                        }
                    } else {
                        toNormalState()
                        showSearchHistory()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
//                    TODO("Not yet implemented")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    TODO("Not yet implemented")
                }

            })
            setOnFocusChangeListener { _, hasFocus ->
                isToFocus = hasFocus
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startRouteSearchCheck()
                }
                return@setOnEditorActionListener false
            }
        }
        binding.activityTravelEtFrom.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        startTipSearch(s.toString())
                    } else {
                        toNormalState()
                        showSearchHistory()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
//                    TODO("Not yet implemented")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    TODO("Not yet implemented")
                }

            })
            setOnFocusChangeListener { _, hasFocus ->
                isFromFocus = hasFocus
            }
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startRouteSearchCheck()
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private fun startTipSearch(keyWord: String) {
        toSearchState()
        Inputtips(
            context,
            InputtipsQuery(
                keyWord, App.DEFAULT_CITY
            )
        ).apply {
            setInputtipsListener(this@TravelActivity)
            requestInputtipsAsyn()
        }
    }

    private fun startRouteSearchCheck() {
        if (binding.activityTravelEtFrom.text.toString() == "我的位置") {
            getMyLocateForm = true
        } else {
            if (!checkTip(tipFrom)) {
                MyToast.showToast("请输入出发位置")
                return
            }
        }
        if (binding.activityTravelEtTo.text.toString() == "我的位置") {
            getMyLocateTo = true
        } else {
            if (!checkTip(tipTo)) {
                MyToast.showToast("请输入前往位置")
                return
            }
        }
        startRouteSearch()
    }

    private fun startRouteSearch() {
        if (haveChanged()) {
            val dev = getMyLocateForm
            getMyLocateForm = getMyLocateTo
            getMyLocateTo = dev
        }
        val myPoint = getMyLocate()
        val fromPoint = if (getMyLocateForm) myPoint else tipFrom?.point
        val toPoint = if (getMyLocateTo) myPoint else tipTo?.point
        EventBus.getDefault().post(
            FromAndToMessageEvent(RouteSearch.FromAndTo(fromPoint, toPoint), searchType)
        )
        finish()
    }

    private fun getMyLocate(): LatLonPoint {
        val sp = getSharedPreferences(App.LOCATION, Context.MODE_PRIVATE)
        val latitude = sp.getFloat("latitude", 39.904989f).toDouble()
        val longitude = sp.getFloat("longitude", 116.405285f).toDouble()
        return LatLonPoint(latitude, longitude)
    }

    private fun checkTip(tip: Tip?): Boolean {
        if (tip == null) {
            if (isFromFocus) {
                if (!binding.activityTravelEtFrom.text.isNullOrEmpty()) {
                    getPopWindowTip(binding.activityTravelEtFrom.text.toString())
                }
            }
            if (isToFocus) {
                if (!binding.activityTravelEtFrom.text.isNullOrEmpty()) {
                    getPopWindowTip(binding.activityTravelEtTo.text.toString())
                }
            }
            return false
        }
        return if (tip.poiID != null) {
            true
        } else {
            getPopWindowTip(tip.name)
            false
        }
    }

    private fun setClickListener() {
        binding.backClick = View.OnClickListener {
            onBackPressed()
        }
        binding.setExchangeClick {
            if (!isAnimFinish) return@setExchangeClick
            val fromY = binding.activityTravelEtFrom.y
            val toY = binding.activityTravelEtTo.y
            AnimatorSet().apply {
                play(
                    getAnimator(
                        fromY, toY, binding.activityTravelEtFrom
                    )
                ).with(
                    getAnimator(
                        toY, fromY, binding.activityTravelEtTo
                    )
                )
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        isAnimFinish = false
                        exchangeFromAndTo()
                        exchangeETSetting()
                    }

                    override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                        super.onAnimationEnd(animation, isReverse)
                        isAnimFinish = true
                    }
                })
                start()
            }
        }
        binding.setPopWindowCancelClick {
            binding.activityTravelPopWindow.visibility = View.GONE
        }
        setTabsClickListener()
    }

    private fun setTabsClickListener() {
        binding.activityTravelTabs.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab == null) return
                when (tab.text) {
                    resources.getString(R.string.car) -> {
                        searchType = App.TYPE_DRIVE
                    }
                    resources.getString(R.string.bus) -> {
                        searchType = App.TYPE_BUS
                    }
                    resources.getString(R.string.bike) -> {
                        searchType = App.TYPE_BIKE
                    }
                    resources.getString(R.string.walk) -> {
                        searchType = App.TYPE_WALK
                    }
                    resources.getString(R.string.truck) -> {
                        searchType = App.TYPE_TRUCK
                    }
                }
            }
        })
    }

    private fun exchangeETSetting() {
        if (haveChanged()) {
            binding.activityTravelEtTo.imeOptions = EditorInfo.IME_ACTION_NEXT
            binding.activityTravelEtFrom.imeOptions = EditorInfo.IME_ACTION_DONE
        } else {
            binding.activityTravelEtTo.imeOptions = EditorInfo.IME_ACTION_NEXT
            binding.activityTravelEtFrom.imeOptions = EditorInfo.IME_ACTION_DONE
        }
    }

    private fun exchangeFromAndTo() {
        val tip = tipFrom
        tipFrom = tipTo
        tipTo = tip
    }


    private fun getAnimator(from: Float, to: Float, editText: EditText) =
        ObjectAnimator.ofFloat(editText, "y", from, to).apply {
            duration = 200
        }

    private fun haveChanged(): Boolean {
        return binding.activityTravelEtFrom.y >
                binding.activityTravelEtTo.y
    }

    override fun onGetInputtips(p0: MutableList<Tip>?, p1: Int) {
        toNormalState()
        if (p1 == 1000) {
            if (isPopWindow) {
                showPopWindow(p0)
            } else {
                val adapter = TravelSearchRecyclerViewAdapter()
                binding.activityTravelRvSearchResult.adapter = adapter
                adapter.submitList(p0)
            }
        } else {
            MyToast.showToast("请求数据错误，errorCode:$p1")
        }
    }

    private fun toSearchState() {
        binding.activityTravelIvSearchBarExchange.visibility = View.GONE
        binding.activityTravelSearchProgressBar.visibility = View.VISIBLE
    }

    private fun toNormalState() {
        binding.activityTravelSearchProgressBar.visibility = View.GONE
        binding.activityTravelIvSearchBarExchange.visibility = View.VISIBLE
    }

    fun onSearchHistoryItemClick(searchHistory: SearchHistory) {
        val myLocate = getMyLocate()
        val toLocate = LatLonPoint(searchHistory.latitude, searchHistory.longitude)
        EventBus.getDefault().post(
            FromAndToMessageEvent(RouteSearch.FromAndTo(myLocate, toLocate), searchType)
        )
        finish()
    }
}
