package com.example.easygo.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.easygo.bean.SearchHistory
import com.example.easygo.databinding.RecycleItemTravelPopWindowBinding
import com.example.easygo.view.activity.SelectOftenGoActivity

/**
 * Created by chene on @date 2020/7/20
 * 在normal界面选择常去地点的adapter
 */
class SelectOftenGoRecyclerViewAdapter :
    ListAdapter<SearchHistory, RecyclerView.ViewHolder>(SearchHistoryDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        PoiViewHolder(
            RecycleItemTravelPopWindowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val searchHistory = getItem(position)
        (holder as PoiViewHolder).bind(searchHistory)

    }

    class PoiViewHolder(
        private val binding: RecycleItemTravelPopWindowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchHistory: SearchHistory) {
            binding.apply {
                binding.recycleItemTravelPopWindowTvTitle.text = searchHistory.title
                binding.recycleItemTravelPopWindowTvAddress.text = searchHistory.snippet
                binding.setItemClick {
                    (binding.root.context as SelectOftenGoActivity).returnResult(searchHistory)
                }
            }
        }
    }
}