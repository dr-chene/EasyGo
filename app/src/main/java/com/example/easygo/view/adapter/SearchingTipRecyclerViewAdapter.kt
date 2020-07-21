package com.example.easygo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.help.Tip
import com.example.easygo.App
import com.example.easygo.databinding.RecycleItemSearchAreaBinding
import com.example.easygo.databinding.RecycleItemSearchPointBinding
import com.example.easygo.view.activity.MainActivity
import com.example.easygo.viewmodel.MainActivityViewModel
import com.example.easygo.viewmodel.TipAndPoiSearchViewModel

/**
 * Created by chene on @date 2020/7/15
 * searching界面搜索输入提示
 */
class SearchingTipRecyclerViewAdapter :
    ListAdapter<Tip, RecyclerView.ViewHolder>(TipDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == App.TYPE_TIP) {
            SuggestionViewHolder(
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

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tip = getItem(position)
        when (holder) {
            is SuggestionViewHolder -> {
                holder.bind(tip)
            }
            is PoiViewHolder -> {
                holder.bind(tip)
            }
            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val tip = getItem(position)
        return if (tip.poiID.isNullOrEmpty()) {
            App.TYPE_TIP
        } else {
            App.TYPE_POI
        }
    }

    class SuggestionViewHolder(
        private val binding: RecycleItemSearchAreaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tip: Tip) {
            binding.apply {
                recycleItemSearchHistoryAreaName.text = tip.name
                recycleItemSearchHistoryAreaIvDelete.visibility = View.GONE
                recycleItemSearchHistoryAreaTvDelete.visibility = View.GONE
            }
            binding.itemClick = View.OnClickListener {
                ViewModelProvider(binding.root.context as MainActivity)
                    .get(TipAndPoiSearchViewModel::class.java).apply {
                        setSearchingSelectedTip(tip)
                    }
            }
        }
    }

    class PoiViewHolder(
        private val binding: RecycleItemSearchPointBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tip: Tip) {
            binding.apply {
                recycleItemSearchHistoryPointName.text = tip.name
                recycleItemSearchHistoryPointAddress.text = tip.district
                recycleItemSearchHistoryTvDelete.visibility = View.GONE
                recycleItemSearchHistoryIvDelete.visibility = View.GONE
            }
            binding.setItemClick {
                ViewModelProvider(binding.root.context as MainActivity)
                    .get(TipAndPoiSearchViewModel::class.java).apply {
                        setSearchingSelectedTip(tip)
                    }
            }
            binding.setRouteClick {
                ViewModelProvider(binding.root.context as MainActivity).get(MainActivityViewModel::class.java)
                    .navToPoi(LatLonPoint(tip.point.latitude, tip.point.longitude))
            }
        }
    }
}