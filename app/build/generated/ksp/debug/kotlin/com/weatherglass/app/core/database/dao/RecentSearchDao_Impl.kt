package com.weatherglass.app.core.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.weatherglass.app.core.database.entity.RecentSearchEntity
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class RecentSearchDao_Impl(
  __db: RoomDatabase,
) : RecentSearchDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRecentSearchEntity: EntityInsertAdapter<RecentSearchEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfRecentSearchEntity = object : EntityInsertAdapter<RecentSearchEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `recent_searches` (`id`,`name`,`region`,`country`,`latitude`,`longitude`,`timezone`,`searchedAtEpochSeconds`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RecentSearchEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.region)
        statement.bindText(4, entity.country)
        statement.bindDouble(5, entity.latitude)
        statement.bindDouble(6, entity.longitude)
        statement.bindText(7, entity.timezone)
        statement.bindLong(8, entity.searchedAtEpochSeconds)
      }
    }
  }

  public override suspend fun insertRecentSearch(search: RecentSearchEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfRecentSearchEntity.insert(_connection, search)
  }

  public override fun getRecentSearches(): Flow<List<RecentSearchEntity>> {
    val _sql: String = "SELECT * FROM recent_searches ORDER BY searchedAtEpochSeconds DESC LIMIT 15"
    return createFlow(__db, false, arrayOf("recent_searches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfRegion: Int = getColumnIndexOrThrow(_stmt, "region")
        val _columnIndexOfCountry: Int = getColumnIndexOrThrow(_stmt, "country")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfTimezone: Int = getColumnIndexOrThrow(_stmt, "timezone")
        val _columnIndexOfSearchedAtEpochSeconds: Int = getColumnIndexOrThrow(_stmt,
            "searchedAtEpochSeconds")
        val _result: MutableList<RecentSearchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RecentSearchEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpRegion: String
          _tmpRegion = _stmt.getText(_columnIndexOfRegion)
          val _tmpCountry: String
          _tmpCountry = _stmt.getText(_columnIndexOfCountry)
          val _tmpLatitude: Double
          _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude)
          val _tmpLongitude: Double
          _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude)
          val _tmpTimezone: String
          _tmpTimezone = _stmt.getText(_columnIndexOfTimezone)
          val _tmpSearchedAtEpochSeconds: Long
          _tmpSearchedAtEpochSeconds = _stmt.getLong(_columnIndexOfSearchedAtEpochSeconds)
          _item =
              RecentSearchEntity(_tmpId,_tmpName,_tmpRegion,_tmpCountry,_tmpLatitude,_tmpLongitude,_tmpTimezone,_tmpSearchedAtEpochSeconds)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteRecentSearch(id: String) {
    val _sql: String = "DELETE FROM recent_searches WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAllRecentSearches() {
    val _sql: String = "DELETE FROM recent_searches"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
