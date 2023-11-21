package com.example.mvvmnews.db

import androidx.room.TypeConverters
import androidx.room.TypeConverter
import com.example.mvvmnews.models.Source

@TypeConverters
class NewsConverter {

    @TypeConverter
    fun fromSourceToString(source: Source): String {
        return source.name.toString()
    }
    @TypeConverter
    fun fromStringToSource(str: String): Source {
        return Source(str, str)
    }

}