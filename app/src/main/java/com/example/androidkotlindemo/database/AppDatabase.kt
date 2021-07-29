package com.example.androidkotlindemo.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Created by zxf on 2021/6/8
 */
@Database(entities = [Student::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {

        private lateinit var mContext: Application

        val instance: AppDatabase by lazy {
            Room.databaseBuilder(mContext, AppDatabase::class.java, "app.db")
                .allowMainThreadQueries()
                .build()
        }

        fun init(app: Application) {
            mContext = app
        }
    }
}