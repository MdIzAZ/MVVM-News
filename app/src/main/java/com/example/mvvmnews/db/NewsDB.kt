package com.example.mvvmnews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mvvmnews.models.Article

@Database(entities = [Article::class], version = 1)
@TypeConverters(NewsConverter::class)
abstract class NewsDB : RoomDatabase() {

    abstract fun newsDAO(): NewsDao

    companion object{
        @Volatile
        private var INSTANCE : NewsDB? = null
        private var LOCK = Any()

        @Synchronized
        fun getInstance(context: Context) : NewsDB{
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDB::class.java,
                    "newsInfo.db"
                ).fallbackToDestructiveMigration().build()
            }
            return INSTANCE as NewsDB
        }

    }
}