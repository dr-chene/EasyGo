package com.example.easygo.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.observe
import com.example.easygo.databinding.FragmentPoiBinding
import com.example.easygo.viewmodel.MainActivityViewModel
import com.example.easygo.viewmodel.TipAndPoiSearchViewModel

/**
 * Created by chene on @date 2020/7/14
 * 详情展示搜索的单个poi结果
 * 但api返回的数据很少
 */
class PoiFragment : Fragment() {

    private lateinit var binding: FragmentPoiBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var tipAndPoiSearchViewModel: TipAndPoiSearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPoiBinding.inflate(
            inflater, container, false
        )
        context ?: return binding.root
        tipAndPoiSearchViewModel =
            ViewModelProvider(context as ViewModelStoreOwner).get(TipAndPoiSearchViewModel::class.java)
        mainActivityViewModel =
            ViewModelProvider(context as ViewModelStoreOwner).get(MainActivityViewModel::class.java)

        subscribe()

        return binding.root
    }

    private fun subscribe() {
        tipAndPoiSearchViewModel.poiItem.observe(viewLifecycleOwner) {
            binding.poi = it
            binding.executePendingBindings()
        }
    }
}