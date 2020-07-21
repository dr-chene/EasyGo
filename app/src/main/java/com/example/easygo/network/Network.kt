package com.example.easygo.network

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by chene on @date 2020/7/21
 */
class Network {
    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val cm = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            val info = cm.activeNetworkInfo
            return info != null && info.isAvailable
        }
    }
}