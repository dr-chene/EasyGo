package com.example.easygo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.easygo.bean.SearchHistory
import com.example.easygo.model.SearchHistoryDao

/**
 * Created by chene on @date 2020/7/15
 * 数据库，使用了单例模式
 * 目前只存了搜索历史
 */
@Database(entities = [SearchHistory::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase(){

    abstract fun searchHistoryDao() : SearchHistoryDao

    companion object{

        @Volatile
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase{
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) : AppDataBase{
            return Room.databaseBuilder(context, AppDataBase::class.java, App.DATA_BASE_NAME)
                .build()
        }
    }
}