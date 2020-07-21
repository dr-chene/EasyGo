package com.example.easygo

import android.app.Application
import android.content.Context

/**
 * Created by chene on @date 2020/7/13
 * app
 * 存储了一些常量
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        var context: Context? = null
            private set
        const val DEFAULT_CITY = "chongqing"
        const val DATA_BASE_NAME = "easygo-db"
        const val TYPE_TIP = 1
        const val TYPE_POI = 2
        const val TYPE_ROOT = 3
        const val FRAGMENT_NORMAL = 1
        const val FRAGMENT_SEARCHING = 2
        const val FRAGMENT_SEARCHED = 3
        const val FRAGMENT_POI = 4
        const val FRAGMENT_ROUTE = 5
        const val TYPE_DRIVE = 0
        const val TYPE_BUS = 1
        const val TYPE_BIKE = 2
        const val TYPE_WALK = 3
        const val TYPE_TRUCK = 4
        const val LOCATION = "location"
    }
}