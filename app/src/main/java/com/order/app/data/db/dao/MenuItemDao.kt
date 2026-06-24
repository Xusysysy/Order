package com.order.app.data.db.dao

import android.content.ContentValues
import com.order.app.data.db.DatabaseHelper
import com.order.app.data.db.entity.MenuItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MenuItemDao(private val helper: DatabaseHelper) {
    fun getAllFlow(): Flow<List<MenuItemEntity>> = flow { emit(getAll()) }

    suspend fun getAll(): List<MenuItemEntity> = withContext(Dispatchers.IO) {
        val db = helper.readableDatabase
        val c = db.rawQuery("SELECT * FROM menu_items ORDER BY sort_order, category, name", null)
        val list = mutableListOf<MenuItemEntity>()
        while (c.moveToNext()) {
            list.add(MenuItemEntity(c.getLong(0), c.getString(1), c.getDouble(2), c.getString(3), c.getInt(4) == 1, c.getInt(5)))
        }
        c.close()
        list
    }

    suspend fun getByCategory(category: String): List<MenuItemEntity> = withContext(Dispatchers.IO) {
        val db = helper.readableDatabase
        val c = db.rawQuery("SELECT * FROM menu_items WHERE category = ? ORDER BY sort_order, name", arrayOf(category))
        val list = mutableListOf<MenuItemEntity>()
        while (c.moveToNext()) {
            list.add(MenuItemEntity(c.getLong(0), c.getString(1), c.getDouble(2), c.getString(3), c.getInt(4) == 1, c.getInt(5)))
        }
        c.close()
        list
    }

    suspend fun getById(id: Long): MenuItemEntity? = withContext(Dispatchers.IO) {
        val db = helper.readableDatabase
        val c = db.rawQuery("SELECT * FROM menu_items WHERE id = ?", arrayOf(id.toString()))
        val result = if (c.moveToFirst()) MenuItemEntity(c.getLong(0), c.getString(1), c.getDouble(2), c.getString(3), c.getInt(4) == 1, c.getInt(5)) else null
        c.close()
        result
    }

    suspend fun insert(item: MenuItemEntity): Long = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply {
            put("name", item.name); put("price", item.price); put("category", item.category)
            put("hasRecipe", if (item.hasRecipe) 1 else 0)
        }
        helper.writableDatabase.insert("menu_items", null, cv)
    }

    suspend fun update(item: MenuItemEntity) = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply {
            put("name", item.name); put("price", item.price); put("category", item.category)
            put("hasRecipe", if (item.hasRecipe) 1 else 0)
        }
        helper.writableDatabase.update("menu_items", cv, "id = ?", arrayOf(item.id.toString()))
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        helper.writableDatabase.delete("menu_items", "id = ?", arrayOf(id.toString()))
    }

    suspend fun updateSortOrders(ids: List<Long>) = withContext(Dispatchers.IO) {
        val db = helper.writableDatabase
        ids.forEachIndexed { index, id ->
            val cv = ContentValues().apply { put("sort_order", index) }
            db.update("menu_items", cv, "id = ?", arrayOf(id.toString()))
        }
    }

    suspend fun markHasRecipe(id: Long, hasRecipe: Boolean) = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply { put("hasRecipe", if (hasRecipe) 1 else 0) }
        helper.writableDatabase.update("menu_items", cv, "id = ?", arrayOf(id.toString()))
    }
}
