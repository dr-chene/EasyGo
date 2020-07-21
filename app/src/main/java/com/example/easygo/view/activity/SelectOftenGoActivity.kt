package com.example.easygo.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.example.easygo.R
import com.example.easygo.bean.SearchHistory
import com.example.easygo.databinding.ActivitySelectOftenGoBinding
import com.example.easygo.utility.InjectorUtils
import com.example.easygo.utility.OftenGoMessageEvent
import com.example.easygo.view.adapter.SelectOftenGoRecyclerViewAdapter
import com.example.easygo.viewmodel.SearchHistoryViewModel
import org.greenrobot.eventbus.EventBus

/*
*简单展示了历史记录，选取作为常去地点
 */
class SelectOftenGoActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectOftenGoBinding
    private lateinit var searchHistoryViewModel: SearchHistoryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_often_go)
        searchHistoryViewModel = InjectorUtils.provideSearchHistoryViewModelFactory(this)
            .create(SearchHistoryViewModel::class.java)
        initView()
    }

    private fun initView() {
        searchHistoryViewModel.searchHistoryList.observe(this) {
            val adapter = SelectOftenGoRecyclerViewAdapter()
            binding.activitySelectOftenGoRv.adapter = adapter
            adapter.submitList(it.filter { searchHistory ->
                searchHistory.poiId != ""
            })
            adapter.notifyDataSetChanged()
        }
        binding.setBackClick {
            this.finish()
        }
    }

    fun returnResult(searchHistory: SearchHistory) {
        EventBus.getDefault().post(OftenGoMessageEvent(searchHistory))
        this.finish()
    }
}