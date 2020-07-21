package com.example.easygo.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.easygo.R
import com.example.easygo.bean.SearchHistory
import com.example.easygo.databinding.FragmentSearchingBinding
import com.example.easygo.network.Network
import com.example.easygo.utility.DeleteEvent
import com.example.easygo.utility.InjectorUtils
import com.example.easygo.utility.KeyWordSearchEvent
import com.example.easygo.utility.MyToast
import com.example.easygo.view.activity.MainActivity
import com.example.easygo.view.adapter.SearchedHistoryRecyclerViewAdapter
import com.example.easygo.view.adapter.SearchingTipRecyclerViewAdapter
import com.example.easygo.viewmodel.MainActivityViewModel
import com.example.easygo.viewmodel.SearchHistoryViewModel
import com.example.easygo.viewmodel.TipAndPoiSearchViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by chene on @date 2020/7/13
 * 搜索界面，实现地点的搜索，历史数据的展示与删除
 */
class SearchingFragment : Fragment() {

    private lateinit var binding: FragmentSearchingBinding
    private lateinit var viewList: List<View>
    private lateinit var searchHistoryViewModel: SearchHistoryViewModel
    private var isSearched = false
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var tipAndPoiSearchViewModel: TipAndPoiSearchViewModel
    private var searchHistoryList: MutableList<SearchHistory> = mutableListOf()
    private val tipAdapter = SearchingTipRecyclerViewAdapter()
    private val searchHistoryAdapter = SearchedHistoryRecyclerViewAdapter(searchHistoryList)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchingBinding.inflate(
            inflater, container, false
        )
        context ?: return binding.root
        searchHistoryViewModel = InjectorUtils
            .provideSearchHistoryViewModelFactory(context!!)
            .create(SearchHistoryViewModel::class.java)
        tipAndPoiSearchViewModel = ViewModelProvider(this).get(TipAndPoiSearchViewModel::class.java)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        initView()
        setClickListener()
        subscribe()
        EventBus.getDefault().register(this)
        return binding.root
    }

    override fun onDestroyView() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroyView()
    }

    //通过EventBus实现点击item删除
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun deleteSearchHistoryEvent(deleteEvent: DeleteEvent) {
        context?.let {
            showDeleteConfirm(it, deleteEvent.getSearchHistory())
        }
    }

    private fun showDeleteConfirm(context: Context, searchHistory: SearchHistory?) {
        AlertDialog.Builder(context).apply {
            setTitle("确认")
            setMessage("是否要删除")
            setPositiveButton("确认") { _, _ ->
                deleteSearchHistory(searchHistory)
            }
            setNegativeButton("取消") { _, _ ->
                MyToast.showToast("操作取消")
            }
            show()
        }
    }

    //删除item，若searchHistory为空则全部删除，否则删除当前searchHistory
    private fun deleteSearchHistory(searchHistory: SearchHistory?) {
        if (searchHistory == null) {
            searchHistoryList.forEach {
                searchHistoryViewModel.deleteSearchHistoryByPoiId(it.time)
            }
            searchHistoryList.clear()
            searchHistoryAdapter.notifyDataSetChanged()
        } else {
            searchHistoryViewModel.deleteSearchHistoryByPoiId(searchHistory.time)
            searchHistoryList.remove(searchHistory)
            searchHistoryAdapter.notifyDataSetChanged()
        }
    }

    private fun subscribe() {
        tipAndPoiSearchViewModel.tips.observe(viewLifecycleOwner) {
            isSearched = true
            binding.fragmentSearchingProgressBarLoad.visibility = View.GONE
            binding.fragmentSearchingRv.adapter = tipAdapter
            tipAdapter.submitList(it)
        }
    }

    private fun setClickListener() {
        binding.backClick = View.OnClickListener {
            (context as MainActivity).onBackPressed()
        }
        binding.eatClick = View.OnClickListener {
            resources.getString(R.string.fragment_searching_tv_eat).apply {
                binding.fragmentSearchingEtSearchBar.setText(this)
                startSearch(this)
            }
        }
        binding.hotelClick = View.OnClickListener {
            resources.getString(R.string.fragment_searching_tv_hotel).apply {
                binding.fragmentSearchingEtSearchBar.setText(this)
                startSearch(this)
            }
        }
        binding.sightClick = View.OnClickListener {
            resources.getString(R.string.fragment_searching_tv_sight).apply {
                binding.fragmentSearchingEtSearchBar.setText(this)
                startSearch(this)
            }
        }
        binding.gasClick = View.OnClickListener {
            resources.getString(R.string.fragment_searching_tv_gas).apply {
                binding.fragmentSearchingEtSearchBar.setText(this)
                startSearch(this)
            }
        }
        binding.toiletClick = View.OnClickListener {
            resources.getString(R.string.fragment_searching_tv_toilet).apply {
                binding.fragmentSearchingEtSearchBar.setText(this)
                startSearch(this)
            }
        }
        binding.shopClick = View.OnClickListener {
            resources.getString(R.string.fragment_searching_tv_shop).apply {
                binding.fragmentSearchingEtSearchBar.setText(this)
                startSearch(this)
            }
        }
    }

    private fun initView() {
        initViewList()
        setEditTextView()
        showHistory()
    }

    private fun initViewList() {
        viewList = arrayListOf(
            binding.fragmentSearchingIvEat,
            binding.fragmentSearchingTvEat,
            binding.fragmentSearchingIvHotel,
            binding.fragmentSearchingTvHotel,
            binding.fragmentSearchingIvSight,
            binding.fragmentSearchingTvSight,
            binding.fragmentSearchingIvGas,
            binding.fragmentSearchingTvGas,
            binding.fragmentSearchingIvToilet,
            binding.fragmentSearchingTvToilet,
            binding.fragmentSearchingIvShop,
            binding.fragmentSearchingTvShop
        )
    }

    //监听ET实现地点推荐（tip）
    private fun setEditTextView() {
        binding.fragmentSearchingEtSearchBar.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        if (Network.isNetworkAvailable(context)) {
                            startSearch(s.toString())
                        } else {
                            MyToast.showToast("当前网络不可用")
                        }
                    } else {
                        toNormalState()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            })
            onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
                context ?: return@OnFocusChangeListener
                (context as MainActivity).sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    EventBus.getDefault().post(KeyWordSearchEvent(v.text.toString()))
                }
                return@setOnEditorActionListener false
            }
        }
        context?.let { openKeyBoard(binding.fragmentSearchingEtSearchBar, it) }
        setListenerToRootView()
    }

    //显示键盘
    private fun openKeyBoard(editText: EditText, context: Context) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val imm = context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    //监听键盘弹出事件
    private fun setListenerToRootView() {
        val rootView: View =
            (context as MainActivity).window.decorView.findViewById(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val mKeyboardUp: Boolean = isKeyboardShown(rootView)
            if (mKeyboardUp) {
                //键盘弹出
                context ?: return@addOnGlobalLayoutListener
                (context as MainActivity).sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun isKeyboardShown(rootView: View): Boolean {
        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.resources.displayMetrics
        val heightDiff: Int = rootView.bottom - r.bottom
        return heightDiff > softKeyboardHeight * dm.density
    }

    private fun toSearchingState() {
        viewList.forEach {
            it.visibility = View.GONE
        }
        binding.fragmentSearchingProgressBarLoad.visibility = View.VISIBLE
    }

    private fun toNormalState() {
        viewList.forEach {
            it.visibility = View.VISIBLE
        }
        binding.fragmentSearchingProgressBarLoad.visibility = View.GONE
        showHistory()
    }

    private fun showHistory() {
        binding.fragmentSearchingRv.adapter = searchHistoryAdapter
        searchHistoryList.clear()
        searchHistoryViewModel.searchHistoryList.observe(viewLifecycleOwner) {
            searchHistoryList.addAll(it)
        }
    }

    private fun startSearch(text: String) {
        toSearchingState()
        context?.let { tipAndPoiSearchViewModel.startSearchTipsByETText(text, it) }
    }
}