package com.example.easygo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.help.Tip
import com.example.easygo.App
import com.example.easygo.databinding.RecycleItemTravelSearchPoiBinding
import com.example.easygo.databinding.RecycleItemTravelSearchTipBinding
import com.example.easygo.viewmodel.TipAndPoiSearchViewModel

/**
 * Created by chene on @date 2020/7/18
 * 出行界面的输入提示
 */
class TravelSearchRecyclerViewAdapter :
    ListAdapter<Tip, RecyclerView.ViewHolder>(TipDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == App.TYPE_TIP) {
            TipViewHolder(
                RecycleItemTravelSearchTipBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            PoiViewHolder(
                RecycleItemTravelSearchPoiBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tip = getItem(position)
        if (holder is PoiViewHolder) {
            holder.bind(tip)
        } else if (holder is TipViewHolder) {
            holder.bind(tip)
        }
    }

    class PoiViewHolder(
        private val binding: RecycleItemTravelSearchPoiBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tip: Tip) {
            binding.poi = tip
            binding.itemClick = View.OnClickListener {
                ViewModelProvider(binding.root.context as ViewModelStoreOwner)
                    .get(TipAndPoiSearchViewModel::class.java).apply {
                        setSearchingSelectedTip(tip)
                    }
            }
        }
    }

    class TipViewHolder(
        private val binding: RecycleItemTravelSearchTipBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(_tip: Tip) {
            binding.tip = _tip
            binding.itemClick = View.OnClickListener {
                ViewModelProvider(binding.root.context as ViewModelStoreOwner)
                    .get(TipAndPoiSearchViewModel::class.java).apply {
                        setSearchingSelectedTip(_tip)
                    }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).poiID.isNullOrEmpty()) App.TYPE_TIP
        else App.TYPE_POI
    }
}

internal class TipDiffCallback : DiffUtil.ItemCallback<Tip>() {
    override fun areItemsTheSame(oldItem: Tip, newItem: Tip): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Tip, newItem: Tip): Boolean {
        return oldItem.poiID == newItem.poiID
    }
}
