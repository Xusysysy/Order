package com.order.app.data.db.dao

import android.content.ContentValues
import com.order.app.data.db.DatabaseHelper
import com.order.app.data.db.entity.RecipeIngredientEntity
import com.order.app.data.db.entity.RecipeStepEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeDao(private val helper: DatabaseHelper) {
    suspend fun getSteps(menuItemId: Long): List<RecipeStepEntity> = withContext(Dispatchers.IO) {
        val db = helper.readableDatabase
        val c = db.rawQuery("SELECT * FROM recipe_steps WHERE menuItemId = ? ORDER BY stepNumber", arrayOf(menuItemId.toString()))
        val list = mutableListOf<RecipeStepEntity>()
        while (c.moveToNext()) {
            list.add(RecipeStepEntity(c.getLong(0), c.getLong(1), c.getInt(2), c.getString(3)))
        }
        c.close()
        list
    }

    suspend fun getIngredients(menuItemId: Long): List<RecipeIngredientEntity> = withContext(Dispatchers.IO) {
        val db = helper.readableDatabase
        val c = db.rawQuery("SELECT * FROM recipe_ingredients WHERE menuItemId = ?", arrayOf(menuItemId.toString()))
        val list = mutableListOf<RecipeIngredientEntity>()
        while (c.moveToNext()) {
            list.add(RecipeIngredientEntity(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3), c.getString(4)))
        }
        c.close()
        list
    }

    suspend fun insertStep(step: RecipeStepEntity): Long = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply {
            put("menuItemId", step.menuItemId); put("stepNumber", step.stepNumber); put("description", step.description)
        }
        helper.writableDatabase.insert("recipe_steps", null, cv)
    }

    suspend fun insertIngredient(ingredient: RecipeIngredientEntity): Long = withContext(Dispatchers.IO) {
        val cv = ContentValues().apply {
            put("menuItemId", ingredient.menuItemId); put("name", ingredient.name)
            put("amount", ingredient.amount); put("unit", ingredient.unit)
        }
        helper.writableDatabase.insert("recipe_ingredients", null, cv)
    }

    suspend fun deleteSteps(menuItemId: Long) = withContext(Dispatchers.IO) {
        helper.writableDatabase.delete("recipe_steps", "menuItemId = ?", arrayOf(menuItemId.toString()))
    }

    suspend fun deleteIngredients(menuItemId: Long) = withContext(Dispatchers.IO) {
        helper.writableDatabase.delete("recipe_ingredients", "menuItemId = ?", arrayOf(menuItemId.toString()))
    }

    suspend fun getStepsForMenuItems(menuItemIds: List<Long>): List<RecipeStepEntity> = withContext(Dispatchers.IO) {
        if (menuItemIds.isEmpty()) return@withContext emptyList()
        val db = helper.readableDatabase
        val placeholders = menuItemIds.joinToString(",") { "?" }
        val args = menuItemIds.map { it.toString() }.toTypedArray()
        val c = db.rawQuery("SELECT * FROM recipe_steps WHERE menuItemId IN ($placeholders) ORDER BY stepNumber", args)
        val list = mutableListOf<RecipeStepEntity>()
        while (c.moveToNext()) {
            list.add(RecipeStepEntity(c.getLong(0), c.getLong(1), c.getInt(2), c.getString(3)))
        }
        c.close()
        list
    }

    suspend fun getIngredientsForMenuItems(menuItemIds: List<Long>): List<RecipeIngredientEntity> = withContext(Dispatchers.IO) {
        if (menuItemIds.isEmpty()) return@withContext emptyList()
        val db = helper.readableDatabase
        val placeholders = menuItemIds.joinToString(",") { "?" }
        val args = menuItemIds.map { it.toString() }.toTypedArray()
        val c = db.rawQuery("SELECT * FROM recipe_ingredients WHERE menuItemId IN ($placeholders)", args)
        val list = mutableListOf<RecipeIngredientEntity>()
        while (c.moveToNext()) {
            list.add(RecipeIngredientEntity(c.getLong(0), c.getLong(1), c.getString(2), c.getString(3), c.getString(4)))
        }
        c.close()
        list
    }
}
