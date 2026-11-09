package com.weatherglass.app.core.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.weatherglass.app.core.database.entity.WeatherCacheEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WeatherCacheDao_Impl(
  __db: RoomDatabase,
) : WeatherCacheDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWeatherCacheEntity: EntityInsertAdapter<WeatherCacheEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWeatherCacheEntity = object : EntityInsertAdapter<WeatherCacheEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `weather_cache` (`locationId`,`serializedWeatherReportJson`,`cachedAtEpochSeconds`,`expiresAtEpochSeconds`,`providerId`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WeatherCacheEntity) {
        statement.bindText(1, entity.locationId)
        statement.bindText(2, entity.serializedWeatherReportJson)
        statement.bindLong(3, entity.cachedAtEpochSeconds)
        statement.bindLong(4, entity.expiresAtEpochSeconds)
        statement.bindText(5, entity.providerId)
      }
    }
  }

  public override suspend fun insertCache(cache: WeatherCacheEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfWeatherCacheEntity.insert(_connection, cache)
  }

  public override suspend fun getCachedWeather(locationId: String): WeatherCacheEntity? {
    val _sql: String = "SELECT * FROM weather_cache WHERE locationId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, locationId)
        val _columnIndexOfLocationId: Int = getColumnIndexOrThrow(_stmt, "locationId")
        val _columnIndexOfSerializedWeatherReportJson: Int = getColumnIndexOrThrow(_stmt,
            "serializedWeatherReportJson")
        val _columnIndexOfCachedAtEpochSeconds: Int = getColumnIndexOrThrow(_stmt,
            "cachedAtEpochSeconds")
        val _columnIndexOfExpiresAtEpochSeconds: Int = getColumnIndexOrThrow(_stmt,
            "expiresAtEpochSeconds")
        val _columnIndexOfProviderId: Int = getColumnIndexOrThrow(_stmt, "providerId")
        val _result: WeatherCacheEntity?
        if (_stmt.step()) {
          val _tmpLocationId: String
          _tmpLocationId = _stmt.getText(_columnIndexOfLocationId)
          val _tmpSerializedWeatherReportJson: String
          _tmpSerializedWeatherReportJson = _stmt.getText(_columnIndexOfSerializedWeatherReportJson)
          val _tmpCachedAtEpochSeconds: Long
          _tmpCachedAtEpochSeconds = _stmt.getLong(_columnIndexOfCachedAtEpochSeconds)
          val _tmpExpiresAtEpochSeconds: Long
          _tmpExpiresAtEpochSeconds = _stmt.getLong(_columnIndexOfExpiresAtEpochSeconds)
          val _tmpProviderId: String
          _tmpProviderId = _stmt.getText(_columnIndexOfProviderId)
          _result =
              WeatherCacheEntity(_tmpLocationId,_tmpSerializedWeatherReportJson,_tmpCachedAtEpochSeconds,_tmpExpiresAtEpochSeconds,_tmpProviderId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCache(locationId: String) {
    val _sql: String = "DELETE FROM weather_cache WHERE locationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, locationId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun pruneExpiredCache(nowEpochSeconds: Long) {
    val _sql: String = "DELETE FROM weather_cache WHERE expiresAtEpochSeconds < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, nowEpochSeconds)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
