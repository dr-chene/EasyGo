package com.example.easygo.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easygo.App
import com.example.easygo.bean.SearchHistory
import com.example.easygo.databinding.RecycleItemSearchAreaBinding
import com.example.easygo.databinding.RecycleItemSearchPointBinding
import com.example.easygo.view.activity.TravelActivity

/**
 * Created by chene on @date 2020/7/20
 * 出行界面的搜索历史，实现点击即从当前位置前往该历史位置
 */
class TravelSearchHistoryRecyclerViewAdapter :
    ListAdapter<SearchHistory, RecyclerView.ViewHolder>(SearchHistoryDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == App.TYPE_TIP) {
            TipViewHolder(
                RecycleItemSearchAreaBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            PoiViewHolder(
                RecycleItemSearchPointBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val searchHistory = getItem(position)
        if (searchHistory.poiId == "") {
            (holder as TipViewHolder).bind(searchHistory)
        } else {
            (holder as PoiViewHolder).bind(searchHistory)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val searchHistory = getItem(position)
        return if (searchHistory.poiId == "") {
            App.TYPE_TIP
        } else {
            App.TYPE_POI
        }
    }


    class PoiViewHolder(
        private val binding: RecycleItemSearchPointBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.recycleItemSearchHistoryIvRoute.visibility = View.GONE
            binding.recycleItemSearchHistoryTvRoute.visibility = View.GONE
            binding.recycleItemSearchHistoryIvDelete.visibility = View.GONE
            binding.recycleItemSearchHistoryTvDelete.visibility = View.GONE
        }

        @SuppressLint("SetTextI18n")
        fun bind(searchHistory: SearchHistory) {
            binding.apply {
                recycleItemSearchHistoryPointName.text = "我的位置  →  ${searchHistory.title}"
                recycleItemSearchHistoryPointAddress.text = searchHistory.snippet
                setItemClick {
                    (binding.root.context as TravelActivity).onSearchHistoryItemClick(searchHistory)
                }
            }
        }
    }

    class TipViewHolder(
        private val binding: RecycleItemSearchAreaBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(searchHistory: SearchHistory) {
            binding.apply {
                recycleItemSearchHistoryAreaTvDelete.visibility = View.GONE
                recycleItemSearchHistoryAreaIvDelete.visibility = View.GONE
                recycleItemSearchHistoryAreaName.text = "我的位置  →  ${searchHistory.title}"
                setItemClick {
                    (binding.root.context as TravelActivity).onSearchHistoryItemClick(searchHistory)
                }
            }
        }
    }
}