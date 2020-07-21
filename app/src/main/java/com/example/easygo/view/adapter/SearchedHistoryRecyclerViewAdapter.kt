package com.example.easygo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.core.LatLonPoint
import com.example.easygo.App
import com.example.easygo.bean.SearchHistory
import com.example.easygo.databinding.RecycleItemSearchAreaBinding
import com.example.easygo.databinding.RecycleItemSearchHistoryRootBinding
import com.example.easygo.databinding.RecycleItemSearchPointBinding
import com.example.easygo.utility.DeleteEvent
import com.example.easygo.utility.MyConverters
import com.example.easygo.view.activity.MainActivity
import com.example.easygo.viewmodel.MainActivityViewModel
import com.example.easygo.viewmodel.TipAndPoiSearchViewModel
import org.greenrobot.eventbus.EventBus

/**
 * Created by chene on @date 2020/7/16
 * 搜索历史的展示
 */

class SearchedHistoryRecyclerViewAdapter(
    private val searchHistoryList: MutableList<SearchHistory>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            App.TYPE_TIP -> {
                TipViewHolder(
                    RecycleItemSearchAreaBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            App.TYPE_POI -> {
                PoiViewHolder(
                    RecycleItemSearchPointBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
            else -> {
                RootViewHolder(
                    RecycleItemSearchHistoryRootBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < itemCount - 1) {
            val searchHistory = getItem(position)
            if (searchHistory.poiId == "") {
                (holder as TipViewHolder).bind(searchHistory)
            } else {
                (holder as PoiViewHolder).bind(searchHistory)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (position < itemCount - 1) {
            val searchHistory = getItem(position)
            if (searchHistory.poiId == "") {
                App.TYPE_TIP
            } else {
                App.TYPE_POI
            }
        } else {
            App.TYPE_ROOT
        }


    override fun getItemCount(): Int {
        return if (getListCount() == 0) {
            0
        } else getListCount() + 1
    }

    private fun getItem(position: Int) = searchHistoryList[position]
    private fun getListCount() = searchHistoryList.size

    class PoiViewHolder(
        private val binding: RecycleItemSearchPointBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchHistory: SearchHistory) {
            binding.apply {
                recycleItemSearchHistoryPointName.text = searchHistory.title
                recycleItemSearchHistoryPointAddress.text = searchHistory.snippet
                setRouteClick {
                    ViewModelProvider(binding.root.context as MainActivity).get(
                        MainActivityViewModel::class.java
                    ).navToPoi(LatLonPoint(searchHistory.latitude, searchHistory.longitude))
                }
                setItemClick {
                    val tip = MyConverters.searchHistoryToTip(searchHistory)
                    ViewModelProvider(binding.root.context as MainActivity)
                        .get(TipAndPoiSearchViewModel::class.java).apply {
                            setSearchingSelectedTip(tip)
                        }
                }
                setDeleteClick {
                    EventBus.getDefault().post(DeleteEvent(searchHistory))
                }
            }
        }
    }

    class TipViewHolder(
        private val binding: RecycleItemSearchAreaBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchHistory: SearchHistory) {
            binding.apply {
                recycleItemSearchHistoryAreaName.text = searchHistory.title
                setItemClick {
                    val tip = MyConverters.searchHistoryToTip(searchHistory)
                    ViewModelProvider(binding.root.context as MainActivity)
                        .get(TipAndPoiSearchViewModel::class.java).apply {
                            setSearchingSelectedTip(tip)
                        }
                }
                setDeleteClick {
                    EventBus.getDefault().post(DeleteEvent(searchHistory))
                }
            }
        }
    }

    class RootViewHolder(
        binding: RecycleItemSearchHistoryRootBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemClick = View.OnClickListener {
                EventBus.getDefault().post(DeleteEvent(null))
            }
        }
    }
}

internal class SearchHistoryDiffCallBack : DiffUtil.ItemCallback<SearchHistory>() {
    override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
        return oldItem.poiId == newItem.poiId
    }

    override fun areContentsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
        return oldItem.time == newItem.time
    }

}