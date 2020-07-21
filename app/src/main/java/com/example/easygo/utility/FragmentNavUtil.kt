package com.example.easygo.utility

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.easygo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by chene on @date 2020/7/17
 */
class FragmentNavUtil {
    companion object {
        fun nav(fragmentManager: FragmentManager, fragment: Fragment) {
            CoroutineScope(Dispatchers.Default).launch {
                fragmentManager.apply {
                    popBackStack()
                    beginTransaction().addToBackStack(null)
                        .replace(R.id.activity_main_bottom_sheet_container, fragment)
                        .commit()
                }
            }
        }
    }
}