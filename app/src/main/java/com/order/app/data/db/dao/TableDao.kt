package com.order.app.data.db.dao

import android.content.ContentValues
import com.order.app.data.db.DatabaseHelper
import com.order.app.data.db.entity.TableEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TableDao(private val helper: DatabaseHelper) {
    fun getAllFlow(): Flow<List<TableEntity>> = flow {
        emit(getAll())
    }

    suspend fun getAll(): List<TableEntity> = withContext(Dispatchers.IO) {
        val db = helper.readableDatabase
        val c = db.rawQuery("SELECT * FROM tables ORDER BY sort_order, zone, name", null)
        val list = mutableListOf<TableEntity>()
        while (c.moveToNext()) {
            list.add(TableEntity(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4)))
        }
        c.close()
        list
    }

    suspend fun getById(id: Long): TableEntity? = withContext(Dispatchers.IO) {
        val db = helper.readableDatabase
        val c = db.rawQuery("SELECT * FROM tables WHERE id = ?", arrayOf(id.toString()))
        val result = if (c.moveToFirst()) TableEntity(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4)) else null
        c.close()
        result
    }

    suspend fun insert(table: TableEntity): Long = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply {
            put("name", table.name); put("zone", table.zone); put("status", table.status)
        }
        helper.writableDatabase.insert("tables", null, cv)
    }

    suspend fun update(table: TableEntity) = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply {
            put("name", table.name); put("zone", table.zone); put("status", table.status)
        }
        helper.writableDatabase.update("tables", cv, "id = ?", arrayOf(table.id.toString()))
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        helper.writableDatabase.delete("tables", "id = ?", arrayOf(id.toString()))
    }

    suspend fun updateStatus(id: Long, status: String) = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply { put("status", status) }
        helper.writableDatabase.update("tables", cv, "id = ?", arrayOf(id.toString()))
    }

    suspend fun updateSortOrders(ids: List<Long>) = withContext(Dispatchers.IO) {
        val db = helper.writableDatabase
        ids.forEachIndexed { index, id ->
            val cv = ContentValues().apply { put("sort_order", index) }
            db.update("tables", cv, "id = ?", arrayOf(id.toString()))
        }
    }
}
