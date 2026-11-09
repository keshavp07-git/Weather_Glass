package com.weatherglass.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.weatherglass.app.core.database.dao.FavoriteCityDao
import com.weatherglass.app.core.database.dao.RecentSearchDao
import com.weatherglass.app.core.database.dao.WeatherCacheDao
import com.weatherglass.app.core.database.entity.FavoriteCityEntity
import com.weatherglass.app.core.database.entity.RecentSearchEntity
import com.weatherglass.app.core.database.entity.WeatherCacheEntity

@Database(
    entities = [
        FavoriteCityEntity::class,
        RecentSearchEntity::class,
        WeatherCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun favoriteCityDao(): FavoriteCityDao
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun weatherCacheDao(): WeatherCacheDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_glass.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
