package com.weatherglass.app.core.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.weatherglass.app.core.database.entity.FavoriteCityEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class FavoriteCityDao_Impl(
  __db: RoomDatabase,
) : FavoriteCityDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFavoriteCityEntity: EntityInsertAdapter<FavoriteCityEntity>

  private val __updateAdapterOfFavoriteCityEntity: EntityDeleteOrUpdateAdapter<FavoriteCityEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfFavoriteCityEntity = object : EntityInsertAdapter<FavoriteCityEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `favorite_cities` (`id`,`name`,`region`,`country`,`latitude`,`longitude`,`timezone`,`displayOrder`,`isCurrentLocation`,`addedAtEpochSeconds`) VALUES (?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteCityEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.region)
        statement.bindText(4, entity.country)
        statement.bindDouble(5, entity.latitude)
        statement.bindDouble(6, entity.longitude)
        statement.bindText(7, entity.timezone)
        statement.bindLong(8, entity.displayOrder.toLong())
        val _tmp: Int = if (entity.isCurrentLocation) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindLong(10, entity.addedAtEpochSeconds)
      }
    }
    this.__updateAdapterOfFavoriteCityEntity = object :
        EntityDeleteOrUpdateAdapter<FavoriteCityEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `favorite_cities` SET `id` = ?,`name` = ?,`region` = ?,`country` = ?,`latitude` = ?,`longitude` = ?,`timezone` = ?,`displayOrder` = ?,`isCurrentLocation` = ?,`addedAtEpochSeconds` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FavoriteCityEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.region)
        statement.bindText(4, entity.country)
        statement.bindDouble(5, entity.latitude)
        statement.bindDouble(6, entity.longitude)
        statement.bindText(7, entity.timezone)
        statement.bindLong(8, entity.displayOrder.toLong())
        val _tmp: Int = if (entity.isCurrentLocation) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        statement.bindLong(10, entity.addedAtEpochSeconds)
        statement.bindText(11, entity.id)
      }
    }
  }

  public override suspend fun insertFavorite(favorite: FavoriteCityEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfFavoriteCityEntity.insert(_connection, favorite)
  }

  public override suspend fun updateFavorites(favorites: List<FavoriteCityEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfFavoriteCityEntity.handleMultiple(_connection, favorites)
  }

  public override fun getAllFavorites(): Flow<List<FavoriteCityEntity>> {
    val _sql: String = "SELECT * FROM favorite_cities ORDER BY displayOrder ASC"
    return createFlow(__db, false, arrayOf("favorite_cities")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfRegion: Int = getColumnIndexOrThrow(_stmt, "region")
        val _columnIndexOfCountry: Int = getColumnIndexOrThrow(_stmt, "country")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfTimezone: Int = getColumnIndexOrThrow(_stmt, "timezone")
        val _columnIndexOfDisplayOrder: Int = getColumnIndexOrThrow(_stmt, "displayOrder")
        val _columnIndexOfIsCurrentLocation: Int = getColumnIndexOrThrow(_stmt, "isCurrentLocation")
        val _columnIndexOfAddedAtEpochSeconds: Int = getColumnIndexOrThrow(_stmt,
            "addedAtEpochSeconds")
        val _result: MutableList<FavoriteCityEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FavoriteCityEntity
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
          val _tmpDisplayOrder: Int
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder).toInt()
          val _tmpIsCurrentLocation: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCurrentLocation).toInt()
          _tmpIsCurrentLocation = _tmp != 0
          val _tmpAddedAtEpochSeconds: Long
          _tmpAddedAtEpochSeconds = _stmt.getLong(_columnIndexOfAddedAtEpochSeconds)
          _item =
              FavoriteCityEntity(_tmpId,_tmpName,_tmpRegion,_tmpCountry,_tmpLatitude,_tmpLongitude,_tmpTimezone,_tmpDisplayOrder,_tmpIsCurrentLocation,_tmpAddedAtEpochSeconds)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getFavoriteById(id: String): FavoriteCityEntity? {
    val _sql: String = "SELECT * FROM favorite_cities WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfRegion: Int = getColumnIndexOrThrow(_stmt, "region")
        val _columnIndexOfCountry: Int = getColumnIndexOrThrow(_stmt, "country")
        val _columnIndexOfLatitude: Int = getColumnIndexOrThrow(_stmt, "latitude")
        val _columnIndexOfLongitude: Int = getColumnIndexOrThrow(_stmt, "longitude")
        val _columnIndexOfTimezone: Int = getColumnIndexOrThrow(_stmt, "timezone")
        val _columnIndexOfDisplayOrder: Int = getColumnIndexOrThrow(_stmt, "displayOrder")
        val _columnIndexOfIsCurrentLocation: Int = getColumnIndexOrThrow(_stmt, "isCurrentLocation")
        val _columnIndexOfAddedAtEpochSeconds: Int = getColumnIndexOrThrow(_stmt,
            "addedAtEpochSeconds")
        val _result: FavoriteCityEntity?
        if (_stmt.step()) {
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
          val _tmpDisplayOrder: Int
          _tmpDisplayOrder = _stmt.getLong(_columnIndexOfDisplayOrder).toInt()
          val _tmpIsCurrentLocation: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCurrentLocation).toInt()
          _tmpIsCurrentLocation = _tmp != 0
          val _tmpAddedAtEpochSeconds: Long
          _tmpAddedAtEpochSeconds = _stmt.getLong(_columnIndexOfAddedAtEpochSeconds)
          _result =
              FavoriteCityEntity(_tmpId,_tmpName,_tmpRegion,_tmpCountry,_tmpLatitude,_tmpLongitude,_tmpTimezone,_tmpDisplayOrder,_tmpIsCurrentLocation,_tmpAddedAtEpochSeconds)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun isFavoriteFlow(id: String): Flow<Boolean> {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE id = ?)"
    return createFlow(__db, false, arrayOf("favorite_cities")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun isFavorite(id: String): Boolean {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE id = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCount(): Int {
    val _sql: String = "SELECT COUNT(*) FROM favorite_cities"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteFavoriteById(id: String) {
    val _sql: String = "DELETE FROM favorite_cities WHERE id = ?"
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
