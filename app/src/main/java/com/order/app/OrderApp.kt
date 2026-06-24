package com.order.app

import android.app.Application
import com.order.app.data.db.DatabaseHelper
import com.order.app.data.db.dao.MenuItemDao
import com.order.app.data.db.dao.RecipeDao
import com.order.app.data.preset.PresetRecipes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DbState {
    data object Loading : DbState()
    data class Ready(val helper: DatabaseHelper) : DbState()
    data class Error(val message: String) : DbState()
}

class OrderApp : Application() {
    private val _dbState = MutableStateFlow<DbState>(DbState.Loading)
    val dbState: StateFlow<DbState> = _dbState

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            try {
                val helper = DatabaseHelper(this@OrderApp)
                helper.writableDatabase
                insertPresetData(helper)
                _dbState.value = DbState.Ready(helper)
            } catch (e: Exception) {
                _dbState.value = DbState.Error("${e.javaClass.simpleName}: ${e.message}")
            }
        }
    }

    private suspend fun insertPresetData(helper: DatabaseHelper) {
        val menuDao = MenuItemDao(helper)
        val recipeDao = RecipeDao(helper)
        try {
            if (menuDao.getById(1L) != null) return
            for ((index, c) in PresetRecipes.cocktails.withIndex()) {
                val menuId = menuDao.insert(
                    com.order.app.data.db.entity.MenuItemEntity(name = c.name, price = c.price, category = c.category, hasRecipe = true)
                )
                for ((i, step) in c.steps.withIndex()) {
                    recipeDao.insertStep(com.order.app.data.db.entity.RecipeStepEntity(menuItemId = menuId, stepNumber = i + 1, description = step))
                }
                for ((name, amount) in c.ingredients) {
                    val parts = amount.split(Regex("[（(]"))
                    val amt = parts[0].trim()
                    val unit = if (parts.size > 1) parts[1].replace(Regex("[）)]"), "").trim() else ""
                    recipeDao.insertIngredient(com.order.app.data.db.entity.RecipeIngredientEntity(menuItemId = menuId, name = name, amount = amt, unit = unit))
                }
            }
            for ((name, cat, price) in PresetRecipes.defaultDrinks) {
                menuDao.insert(com.order.app.data.db.entity.MenuItemEntity(name = name, price = price, category = cat))
            }
            for ((name, cat, price) in PresetRecipes.defaultSnacks) {
                menuDao.insert(com.order.app.data.db.entity.MenuItemEntity(name = name, price = price, category = cat))
            }
        } catch (_: Exception) {}
    }
}
