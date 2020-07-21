package com.example.easygo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.core.PoiItem
import com.example.easygo.databinding.RecycleItemSearchResultBinding
import com.example.easygo.viewmodel.TipAndPoiSearchViewModel

/**
 * Created by chene on @date 2020/7/16
 * searched界面搜索结果的展示
 */
class SearchedResultRecyclerViewAdapter :
    ListAdapter<PoiItem, RecyclerView.ViewHolder>(PoiItemDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PoiItemViewHolder(
            RecycleItemSearchResultBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val poi = getItem(position)

        if (poi.photos.isNullOrEmpty()) {
            (holder as PoiItemViewHolder).noImg()
        } else {
            (holder as PoiItemViewHolder).showImg()
        }
        holder.bind(poi)

    }

    class PoiItemViewHolder(
        private val binding: RecycleItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(_poi: PoiItem) {
            binding.apply {
                poi = _poi
                executePendingBindings()
            }
            binding.itemClick = View.OnClickListener {
                ViewModelProvider(binding.root.context as ViewModelStoreOwner).get(
                    TipAndPoiSearchViewModel::class.java
                ).apply {
                    setShowPoiItem(_poi)
                }
            }
        }

        fun noImg() {
            binding.recycleItemSearchResultIvPhoto.visibility = View.GONE
        }

        fun showImg() {
            binding.recycleItemSearchResultIvPhoto.visibility = View.VISIBLE
        }
    }

}

private class PoiItemDiffCallBack : DiffUtil.ItemCallback<PoiItem>() {
    override fun areItemsTheSame(oldItem: PoiItem, newItem: PoiItem): Boolean {
        return oldItem.poiId == newItem.poiId
    }

    override fun areContentsTheSame(oldItem: PoiItem, newItem: PoiItem): Boolean {
        return oldItem.latLonPoint == newItem.latLonPoint
    }
}