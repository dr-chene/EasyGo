package com.example.easygo.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.observe
import com.example.easygo.databinding.FragmentSearchedBinding
import com.example.easygo.utility.MyToast
import com.example.easygo.view.adapter.SearchedResultRecyclerViewAdapter
import com.example.easygo.viewmodel.TipAndPoiSearchViewModel

/**
 * Created by chene on @date 2020/7/13
 * 监听并展示搜索结果
 */
class SearchedFragment : Fragment() {

    private lateinit var binding: FragmentSearchedBinding
    private lateinit var tipAndPoiSearchViewModel: TipAndPoiSearchViewModel
    private var isSingleData = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchedBinding.inflate(
            inflater, container, false
        )
        context ?: return binding.root
        tipAndPoiSearchViewModel =
            ViewModelProvider(context as ViewModelStoreOwner).get(TipAndPoiSearchViewModel::class.java)

        initView()
        subscribe()

        return binding.root
    }

    private fun initView() {

    }

    private fun subscribe() {
        tipAndPoiSearchViewModel.poiItems.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                if (it.size == 1) {
                    isSingleData = true
                }
                val adapter = SearchedResultRecyclerViewAdapter()
                binding.fragmentSearchedRv.adapter = adapter
                adapter.submitList(it)
            } else {
                MyToast.showToast("返回数据为空")
            }
        }
    }
}