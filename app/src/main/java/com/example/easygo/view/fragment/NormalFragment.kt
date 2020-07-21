package com.example.easygo.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.amap.api.maps.offlinemap.OfflineMapActivity
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.RouteSearch
import com.example.easygo.App
import com.example.easygo.bean.SearchHistory
import com.example.easygo.databinding.FragmentNormalBinding
import com.example.easygo.utility.FromAndToMessageEvent
import com.example.easygo.utility.OftenGoMessageEvent
import com.example.easygo.view.activity.MainActivity
import com.example.easygo.view.activity.SelectOftenGoActivity
import com.example.easygo.view.activity.TravelActivity
import com.example.easygo.viewmodel.MainActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by chene on @date 2020/7/13
 * 平常的fragment，实现了常去地点的设置，方法有点笨
 * 主要考虑到sp的快捷，所以没有使用sqlite库
 */
class NormalFragment : Fragment() {

    private lateinit var binding: FragmentNormalBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var select = SELECT_1
    private lateinit var deleteView: List<View>
    private lateinit var oftenGoList: MutableList<SearchHistory>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNormalBinding.inflate(inflater, container, false)
        context ?: return binding.root

        mainActivityViewModel =
            ViewModelProvider(context as ViewModelStoreOwner).get(MainActivityViewModel::class.java)
        initView()
        setClickListener()

        EventBus.getDefault().register(this)


        return binding.root
    }

    private fun initView() {
        deleteView = listOf(
            binding.fragmentNormalIvDelete1,
            binding.fragmentNormalIvDelete2,
            binding.fragmentNormalIvDelete3,
            binding.fragmentNormalIvDelete4,
            binding.fragmentNormalIvDelete5,
            binding.fragmentNormalIvDelete6
        )
        oftenGoList = mutableListOf()
        read(oftenGoList)
        binding.fragmentNormalTvOftenGo1.text = oftenGoList[0].title
        binding.fragmentNormalTvOftenGo2.text = oftenGoList[1].title
        binding.fragmentNormalTvOftenGo3.text = oftenGoList[2].title
        binding.fragmentNormalTvOftenGo4.text = oftenGoList[3].title
        binding.fragmentNormalTvOftenGo5.text = oftenGoList[4].title
        binding.fragmentNormalTvOftenGo6.text = oftenGoList[5].title
    }

    override fun onDestroyView() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroyView()
    }

    private fun setClickListener() {
        binding.setCenterClick {
            (context as MainActivity)
                .sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        binding.setSearchClick {
            navToSearchingFragment()
        }
        binding.setCarClick {
            navToTravelActivity(App.TYPE_DRIVE)
        }
        binding.setBusClick {
            navToTravelActivity(App.TYPE_BUS)
        }
        binding.setWalkClick {
            navToTravelActivity(App.TYPE_WALK)
        }
        binding.setBikeClick {
            navToTravelActivity(App.TYPE_BIKE)
        }
        binding.setOfflineClick {
            navToOfflineMapActivity()
        }
        binding.setClickToAdd1 {
            locateClick(it as TextView, SELECT_1, 0)
        }
        binding.setClickToAdd2 {
            locateClick(it as TextView, SELECT_2, 1)
        }
        binding.setClickToAdd3 {
            locateClick(it as TextView, SELECT_3, 2)
        }
        binding.setClickToAdd4 {
            locateClick(it as TextView, SELECT_4, 3)
        }
        binding.setClickToAdd5 {
            locateClick(it as TextView, SELECT_5, 4)
        }
        binding.setClickToAdd6 {
            locateClick(it as TextView, SELECT_6, 5)
        }
        binding.setClickChange {
            if (deleteView[0].visibility == View.GONE) {
                deleteView.forEach {
                    it.visibility = View.VISIBLE
                }
            } else {
                deleteView.forEach {
                    it.visibility = View.GONE
                }
            }
        }
        binding.setClickToDelete1 {
            binding.fragmentNormalTvOftenGo1.text = "点击添加地点"
        }
        binding.setClickToDelete2 {
            binding.fragmentNormalTvOftenGo2.text = "点击添加地点"
        }
        binding.setClickToDelete3 {
            binding.fragmentNormalTvOftenGo3.text = "点击添加地点"
        }
        binding.setClickToDelete4 {
            binding.fragmentNormalTvOftenGo4.text = "点击添加地点"
        }
        binding.setClickToDelete5 {
            binding.fragmentNormalTvOftenGo5.text = "点击添加地点"
        }
        binding.setClickToDelete6 {
            binding.fragmentNormalTvOftenGo6.text = "点击添加地点"
        }
    }

    private fun locateClick(textView: TextView, key: String, position: Int) {
        if (textView.text == "点击添加地点") {
            select = key
            startActivity(Intent(context, SelectOftenGoActivity::class.java))
        } else {
            context ?: return
            val fromLocation = getMyLocate(context!!)
            val toLocation =
                LatLonPoint(oftenGoList[position].latitude, oftenGoList[position].longitude)
            EventBus.getDefault().post(
                FromAndToMessageEvent(
                    RouteSearch.FromAndTo(fromLocation, toLocation), App.TYPE_DRIVE
                )
            )
        }
    }

    private fun navToSearchingFragment() {
        mainActivityViewModel.navTo(App.FRAGMENT_SEARCHING)
    }

    private fun navToOfflineMapActivity() {
        startActivity(
            Intent(context, OfflineMapActivity::class.java)
        )
    }

    private fun navToTravelActivity(way: Int) {
        val intent = Intent(context, TravelActivity::class.java).apply {
            putExtra("travel_way", way)
        }
        startActivity(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun selectOftenGoEvent(oftenGoMessageEvent: OftenGoMessageEvent) {
        context?.let {
            saveOftenGo(select, oftenGoMessageEvent.getSearchHistory(), it)
            when (select) {
                SELECT_1 -> {
                    changeText(
                        oftenGoMessageEvent.getSearchHistory().title,
                        binding.fragmentNormalTvOftenGo1
                    )
                    addToOftenGoOnList(oftenGoList, oftenGoMessageEvent.getSearchHistory(), 0)
                }
                SELECT_2 -> {
                    changeText(
                        oftenGoMessageEvent.getSearchHistory().title,
                        binding.fragmentNormalTvOftenGo2
                    )
                    addToOftenGoOnList(oftenGoList, oftenGoMessageEvent.getSearchHistory(), 1)
                }
                SELECT_3 -> {
                    changeText(
                        oftenGoMessageEvent.getSearchHistory().title,
                        binding.fragmentNormalTvOftenGo3
                    )
                    addToOftenGoOnList(oftenGoList, oftenGoMessageEvent.getSearchHistory(), 2)
                }
                SELECT_4 -> {
                    changeText(
                        oftenGoMessageEvent.getSearchHistory().title,
                        binding.fragmentNormalTvOftenGo4
                    )
                    addToOftenGoOnList(oftenGoList, oftenGoMessageEvent.getSearchHistory(), 3)
                }
                SELECT_5 -> {
                    changeText(
                        oftenGoMessageEvent.getSearchHistory().title,
                        binding.fragmentNormalTvOftenGo5
                    )
                    addToOftenGoOnList(oftenGoList, oftenGoMessageEvent.getSearchHistory(), 4)
                }
                SELECT_6 -> {
                    changeText(
                        oftenGoMessageEvent.getSearchHistory().title,
                        binding.fragmentNormalTvOftenGo6
                    )
                    addToOftenGoOnList(oftenGoList, oftenGoMessageEvent.getSearchHistory(), 5)
                }
            }
        }
    }

    private fun changeText(text: String, view: TextView) {
        view.text = text
    }

    private fun addToOftenGoOnList(
        oftenGoList: MutableList<SearchHistory>,
        searchHistory: SearchHistory,
        position: Int
    ) {
        oftenGoList.removeAt(position)
        oftenGoList.add(position, searchHistory)
    }

    private fun saveOftenGo(key: String, searchHistory: SearchHistory, mContext: Context) {
        mContext.getSharedPreferences(key, Context.MODE_PRIVATE).edit().apply {
            putString("title", searchHistory.title)
            putFloat("latitude", searchHistory.latitude.toFloat())
            putFloat("longitude", searchHistory.longitude.toFloat())
            apply()
        }
    }

    private fun read(oftenGoList: MutableList<SearchHistory>) {
        context ?: return
        oftenGoList.clear()
        oftenGoList.add(readOftenGo(SELECT_1, context!!))
        oftenGoList.add(readOftenGo(SELECT_2, context!!))
        oftenGoList.add(readOftenGo(SELECT_3, context!!))
        oftenGoList.add(readOftenGo(SELECT_4, context!!))
        oftenGoList.add(readOftenGo(SELECT_5, context!!))
        oftenGoList.add(readOftenGo(SELECT_6, context!!))
    }

    private fun readOftenGo(key: String, mContext: Context): SearchHistory {
        mContext.getSharedPreferences(key, Context.MODE_PRIVATE).apply {
            val title = getString("title", "点击添加地点")
            val latitude = getFloat("latitude", 39.904989f).toDouble()
            val longitude = getFloat("longitude", 116.405285f).toDouble()
            return SearchHistory("", title ?: "点击添加地点", "", latitude, longitude, 0)
        }
    }

    private fun getMyLocate(mContext: Context): LatLonPoint {
        val sp = mContext.getSharedPreferences(App.LOCATION, Context.MODE_PRIVATE)
        val latitude = sp.getFloat("latitude", 39.904989f).toDouble()
        val longitude = sp.getFloat("longitude", 116.405285f).toDouble()
        return LatLonPoint(latitude, longitude)
    }

    companion object {
        const val SELECT_1 = "Often_Go_1"
        const val SELECT_2 = "Often_Go_2"
        const val SELECT_3 = "Often_Go_3"
        const val SELECT_4 = "Often_Go_4"
        const val SELECT_5 = "Often_Go_5"
        const val SELECT_6 = "Often_Go_6"
    }
}