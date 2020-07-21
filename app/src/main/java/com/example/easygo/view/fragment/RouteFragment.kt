package com.example.easygo.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.observe
import com.example.easygo.App
import com.example.easygo.databinding.FragmentRouteBinding
import com.example.easygo.view.adapter.RouteDriveRecyclerViewAdapter
import com.example.easygo.view.adapter.RouteRideRecyclerViewAdapter
import com.example.easygo.view.adapter.RouteWalkRecyclerViewAdapter
import com.example.easygo.viewmodel.MainActivityViewModel
import com.example.easygo.viewmodel.RouteViewModel

/**
 * Created by chene on @date 2020/7/19
 * 监听并显示路线搜索结果
 */
class RouteFragment : Fragment() {

    private lateinit var binding: FragmentRouteBinding
    private lateinit var routeViewModel: RouteViewModel
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteBinding.inflate(layoutInflater, container, false)
        context ?: return binding.root

        routeViewModel =
            ViewModelProvider(context as ViewModelStoreOwner).get(RouteViewModel::class.java)
        mainActivityViewModel =
            ViewModelProvider(context as ViewModelStoreOwner).get(MainActivityViewModel::class.java)

        setClickListener()
        subscribe()

        return binding.root
    }

    private fun setClickListener() {
        binding.setBackClick {
            mainActivityViewModel.navTo(App.FRAGMENT_NORMAL)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribe() {
        routeViewModel.driveSteps.observe(this) {
            binding.fragmentRouteTvCostTime.text = routeViewModel.des
            binding.fragmentRouteTvTaxiCost.text = "打车约${routeViewModel.taxiCost}元"
            val adapter = RouteDriveRecyclerViewAdapter()
            binding.fragmentRouteRv.adapter = adapter
            adapter.submitList(it)
        }
        routeViewModel.rideSteps.observe(this) {
            binding.fragmentRouteTvCostTime.text = routeViewModel.des
            if (routeViewModel.taxiCost != 0f) {
                binding.fragmentRouteTvTaxiCost.text = "打车约${routeViewModel.taxiCost}元"
            } else {
                binding.fragmentRouteTvTaxiCost.visibility = View.INVISIBLE
            }
            val adapter = RouteRideRecyclerViewAdapter()
            binding.fragmentRouteRv.adapter = adapter
            adapter.submitList(it)
        }
        routeViewModel.walkSteps.observe(this) {
            binding.fragmentRouteTvCostTime.text = routeViewModel.des
            if (routeViewModel.taxiCost != 0f) {
                binding.fragmentRouteTvTaxiCost.text = "打车约${routeViewModel.taxiCost}元"
            } else {
                binding.fragmentRouteTvTaxiCost.visibility = View.INVISIBLE
            }
            val adapter = RouteWalkRecyclerViewAdapter()
            binding.fragmentRouteRv.adapter = adapter
            adapter.submitList(it)
        }
    }
}