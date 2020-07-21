package com.example.easygo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.route.DriveStep
import com.amap.api.services.route.RideStep
import com.amap.api.services.route.WalkStep
import com.example.easygo.databinding.RecycleItemRouteBinding
import com.example.easygo.utility.amap.AMapUtil

/**
 * Created by chene on @date 2020/7/19
 * 几种路线规划的数据展示
 */
//drive adapter
class RouteDriveRecyclerViewAdapter :
    ListAdapter<DriveStep, RecyclerView.ViewHolder>(DriveStepDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DriveViewHolder(
            RecycleItemRouteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DriveViewHolder).bind(getItem(position), position, itemCount)
    }

    class DriveViewHolder(
        private val binding: RecycleItemRouteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(driveStep: DriveStep, position: Int, itemCount: Int) {
            when (position) {
                0 -> {
                    binding.apply {
                        recycleItemRouteDirectionTop.visibility = View.GONE
                        recycleItemRouteDirection.setImageResource(
                            AMapUtil.getDriveActionID(
                                driveStep.action
                            )
                        )
                        recycleItemRouteTvAction.text = "起点"
                    }
                }
                itemCount - 1 -> {
                    binding.apply {
                        recycleItemRouteDirectionBottom.visibility = View.GONE
                        recycleItemRouteDirection.setImageResource(
                            AMapUtil.getDriveActionID(
                                driveStep.action
                            )
                        )
                        recycleItemRouteTvAction.text = "终点"
                    }
                }
                else -> {
                    binding.apply {
                        recycleItemRouteDirection.setImageResource(
                            AMapUtil.getDriveActionID(
                                driveStep.action
                            )
                        )
                        recycleItemRouteTvAction.text = driveStep.action
                    }
                }
            }
            binding.executePendingBindings()
        }
    }

}

private class DriveStepDiffCallBack : DiffUtil.ItemCallback<DriveStep>() {
    override fun areItemsTheSame(oldItem: DriveStep, newItem: DriveStep): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DriveStep, newItem: DriveStep): Boolean {
        return oldItem.action == newItem.action
    }

}

//ride adapter
class RouteRideRecyclerViewAdapter :
    ListAdapter<RideStep, RecyclerView.ViewHolder>(RideStepDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RideViewHolder(
            RecycleItemRouteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RideViewHolder).bind(getItem(position), position, itemCount)
    }

    class RideViewHolder(
        private val binding: RecycleItemRouteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rideStep: RideStep, position: Int, itemCount: Int) {
            when (position) {
                0 -> {
                    binding.apply {
                        recycleItemRouteDirectionTop.visibility = View.GONE
                        recycleItemRouteDirection.setImageResource(AMapUtil.getWalkActionID(rideStep.action))
                        recycleItemRouteTvAction.text = "起点"
                    }
                }
                itemCount - 1 -> {
                    binding.apply {
                        recycleItemRouteDirectionBottom.visibility = View.GONE
                        recycleItemRouteDirection.setImageResource(AMapUtil.getWalkActionID(rideStep.action))
                        recycleItemRouteTvAction.text = "终点"
                    }
                }
                else -> {
                    binding.apply {
                        recycleItemRouteDirection.setImageResource(AMapUtil.getWalkActionID(rideStep.action))
                        recycleItemRouteTvAction.text = rideStep.action
                    }
                }
            }
            binding.executePendingBindings()
        }
    }

}

private class RideStepDiffCallBack : DiffUtil.ItemCallback<RideStep>() {
    override fun areItemsTheSame(oldItem: RideStep, newItem: RideStep): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: RideStep, newItem: RideStep): Boolean {
        return oldItem.action == newItem.action
    }
}

//walk adapter
class RouteWalkRecyclerViewAdapter :
    ListAdapter<WalkStep, RecyclerView.ViewHolder>(WalkStepDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WalkViewHolder(
            RecycleItemRouteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as WalkViewHolder).bind(getItem(position), position, itemCount)
    }

    class WalkViewHolder(
        private val binding: RecycleItemRouteBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(walkStep: WalkStep, position: Int, itemCount: Int) {
            when (position) {
                0 -> {
                    binding.apply {
                        recycleItemRouteDirectionTop.visibility = View.GONE
                        recycleItemRouteDirection.setImageResource(AMapUtil.getWalkActionID(walkStep.action))
                        recycleItemRouteTvAction.text = "起点"
                    }
                }
                itemCount - 1 -> {
                    binding.apply {
                        recycleItemRouteDirectionBottom.visibility = View.GONE
                        recycleItemRouteDirection.setImageResource(AMapUtil.getWalkActionID(walkStep.action))
                        recycleItemRouteTvAction.text = "终点"
                    }
                }
                else -> {
                    binding.apply {
                        recycleItemRouteDirection.setImageResource(
                            AMapUtil.getDriveActionID(
                                walkStep.action
                            )
                        )
                        recycleItemRouteTvAction.text = walkStep.action
                    }
                }
            }
            binding.executePendingBindings()
        }
    }

}

private class WalkStepDiffCallBack : DiffUtil.ItemCallback<WalkStep>() {
    override fun areItemsTheSame(oldItem: WalkStep, newItem: WalkStep): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: WalkStep, newItem: WalkStep): Boolean {
        return oldItem.action == newItem.action
    }
}

