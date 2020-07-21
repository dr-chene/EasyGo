package com.example.easygo.utility

import android.os.Handler
import android.widget.Toast
import com.example.easygo.App

/**
 * Created by chene on @date 2020/7/19
 */
class MyToast {
    companion object {
        private var toast: Toast? = null
        private val handler = Handler()
        fun showToast(msg: String?) {
            handler.post {
                if (toast != null) {
                    toast?.cancel()
                }
                toast = Toast.makeText(App.context, msg, Toast.LENGTH_SHORT)
                toast?.show()
            }
        }
    }
}