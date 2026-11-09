package com.weatherglass.app.core.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.weatherglass.app.core.database.dao.FavoriteCityDao
import com.weatherglass.app.core.database.dao.FavoriteCityDao_Impl
import com.weatherglass.app.core.database.dao.RecentSearchDao
import com.weatherglass.app.core.database.dao.RecentSearchDao_Impl
import com.weatherglass.app.core.database.dao.WeatherCacheDao
import com.weatherglass.app.core.database.dao.WeatherCacheDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WeatherDatabase_Impl : WeatherDatabase() {
  private val _favoriteCityDao: Lazy<FavoriteCityDao> = lazy {
    FavoriteCityDao_Impl(this)
  }

  private val _recentSearchDao: Lazy<RecentSearchDao> = lazy {
    RecentSearchDao_Impl(this)
  }

  private val _weatherCacheDao: Lazy<WeatherCacheDao> = lazy {
    WeatherCacheDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "f4fc27b7f9d045bc25fecada15b1510a", "7992b52186cdbbed28f42927c3fa4b45") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `favorite_cities` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `region` TEXT NOT NULL, `country` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `timezone` TEXT NOT NULL, `displayOrder` INTEGER NOT NULL, `isCurrentLocation` INTEGER NOT NULL, `addedAtEpochSeconds` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `recent_searches` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `region` TEXT NOT NULL, `country` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `timezone` TEXT NOT NULL, `searchedAtEpochSeconds` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `weather_cache` (`locationId` TEXT NOT NULL, `serializedWeatherReportJson` TEXT NOT NULL, `cachedAtEpochSeconds` INTEGER NOT NULL, `expiresAtEpochSeconds` INTEGER NOT NULL, `providerId` TEXT NOT NULL, PRIMARY KEY(`locationId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f4fc27b7f9d045bc25fecada15b1510a')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `favorite_cities`")
        connection.execSQL("DROP TABLE IF EXISTS `recent_searches`")
        connection.execSQL("DROP TABLE IF EXISTS `weather_cache`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsFavoriteCities: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFavoriteCities.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("region", TableInfo.Column("region", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("country", TableInfo.Column("country", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("latitude", TableInfo.Column("latitude", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("longitude", TableInfo.Column("longitude", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("timezone", TableInfo.Column("timezone", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("displayOrder", TableInfo.Column("displayOrder", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("isCurrentLocation", TableInfo.Column("isCurrentLocation",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFavoriteCities.put("addedAtEpochSeconds", TableInfo.Column("addedAtEpochSeconds",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFavoriteCities: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFavoriteCities: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFavoriteCities: TableInfo = TableInfo("favorite_cities", _columnsFavoriteCities,
            _foreignKeysFavoriteCities, _indicesFavoriteCities)
        val _existingFavoriteCities: TableInfo = read(connection, "favorite_cities")
        if (!_infoFavoriteCities.equals(_existingFavoriteCities)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |favorite_cities(com.weatherglass.app.core.database.entity.FavoriteCityEntity).
              | Expected:
              |""".trimMargin() + _infoFavoriteCities + """
              |
              | Found:
              |""".trimMargin() + _existingFavoriteCities)
        }
        val _columnsRecentSearches: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsRecentSearches.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecentSearches.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecentSearches.put("region", TableInfo.Column("region", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecentSearches.put("country", TableInfo.Column("country", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecentSearches.put("latitude", TableInfo.Column("latitude", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecentSearches.put("longitude", TableInfo.Column("longitude", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecentSearches.put("timezone", TableInfo.Column("timezone", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsRecentSearches.put("searchedAtEpochSeconds",
            TableInfo.Column("searchedAtEpochSeconds", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysRecentSearches: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesRecentSearches: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoRecentSearches: TableInfo = TableInfo("recent_searches", _columnsRecentSearches,
            _foreignKeysRecentSearches, _indicesRecentSearches)
        val _existingRecentSearches: TableInfo = read(connection, "recent_searches")
        if (!_infoRecentSearches.equals(_existingRecentSearches)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |recent_searches(com.weatherglass.app.core.database.entity.RecentSearchEntity).
              | Expected:
              |""".trimMargin() + _infoRecentSearches + """
              |
              | Found:
              |""".trimMargin() + _existingRecentSearches)
        }
        val _columnsWeatherCache: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWeatherCache.put("locationId", TableInfo.Column("locationId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherCache.put("serializedWeatherReportJson",
            TableInfo.Column("serializedWeatherReportJson", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherCache.put("cachedAtEpochSeconds", TableInfo.Column("cachedAtEpochSeconds",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherCache.put("expiresAtEpochSeconds", TableInfo.Column("expiresAtEpochSeconds",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherCache.put("providerId", TableInfo.Column("providerId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWeatherCache: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWeatherCache: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWeatherCache: TableInfo = TableInfo("weather_cache", _columnsWeatherCache,
            _foreignKeysWeatherCache, _indicesWeatherCache)
        val _existingWeatherCache: TableInfo = read(connection, "weather_cache")
        if (!_infoWeatherCache.equals(_existingWeatherCache)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |weather_cache(com.weatherglass.app.core.database.entity.WeatherCacheEntity).
              | Expected:
              |""".trimMargin() + _infoWeatherCache + """
              |
              | Found:
              |""".trimMargin() + _existingWeatherCache)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "favorite_cities",
        "recent_searches", "weather_cache")
  }

  public override fun clearAllTables() {
    super.performClear(false, "favorite_cities", "recent_searches", "weather_cache")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(FavoriteCityDao::class, FavoriteCityDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(RecentSearchDao::class, RecentSearchDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(WeatherCacheDao::class, WeatherCacheDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun favoriteCityDao(): FavoriteCityDao = _favoriteCityDao.value

  public override fun recentSearchDao(): RecentSearchDao = _recentSearchDao.value

  public override fun weatherCacheDao(): WeatherCacheDao = _weatherCacheDao.value
}
