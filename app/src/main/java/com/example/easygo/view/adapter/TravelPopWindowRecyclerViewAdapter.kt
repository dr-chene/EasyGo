package com.example.easygo.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.help.Tip
import com.example.easygo.databinding.RecycleItemTravelPopWindowBinding
import com.example.easygo.viewmodel.TipAndPoiSearchViewModel

/**
 * Created by chene on @date 2020/7/18
 * 出行界面弹出窗口，供用户选择准确的前往点
 */
class TravelPopWindowRecyclerViewAdapter :
    ListAdapter<Tip, RecyclerView.ViewHolder>(TipDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TipViewHolder(
            RecycleItemTravelPopWindowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tip = getItem(position)
        (holder as TipViewHolder).bind(tip)
    }

    class TipViewHolder(
        private val binding: RecycleItemTravelPopWindowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tip: Tip) {
            binding.recycleItemTravelPopWindowTvTitle.text = tip.name
            binding.recycleItemTravelPopWindowTvAddress.text = tip.district
            binding.setItemClick {
                ViewModelProvider(binding.root.context as ViewModelStoreOwner).get(
                    TipAndPoiSearchViewModel::class.java
                ).setSearchingSelectedTip(tip)
            }
        }
    }
}