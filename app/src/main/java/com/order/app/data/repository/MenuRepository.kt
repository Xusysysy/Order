package com.order.app.data.repository

import com.order.app.data.db.dao.MenuItemDao
import com.order.app.data.db.dao.RecipeDao
import com.order.app.data.db.entity.MenuItemEntity
import com.order.app.data.db.entity.RecipeData
import com.order.app.data.db.entity.RecipeIngredientEntity
import com.order.app.data.db.entity.RecipeStepEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MenuRepository(
    private val menuDao: MenuItemDao,
    private val recipeDao: RecipeDao
) {
    fun getAll(): Flow<List<MenuItemEntity>> = menuDao.getAllFlow()

    fun getByCategory(category: String): Flow<List<MenuItemEntity>> = flow { emit(menuDao.getByCategory(category)) }

    suspend fun getById(id: Long): MenuItemEntity? = menuDao.getById(id)

    suspend fun insert(item: MenuItemEntity): Long = menuDao.insert(item)

    suspend fun update(item: MenuItemEntity) = menuDao.update(item)

    suspend fun delete(id: Long) = menuDao.deleteById(id)

    suspend fun updateSortOrders(ids: List<Long>) = menuDao.updateSortOrders(ids)

    suspend fun getRecipeSteps(menuItemId: Long): List<RecipeStepEntity> =
        recipeDao.getSteps(menuItemId)

    suspend fun getRecipeIngredients(menuItemId: Long): List<RecipeIngredientEntity> =
        recipeDao.getIngredients(menuItemId)

    suspend fun saveRecipe(
        menuItemId: Long,
        steps: List<RecipeStepEntity>,
        ingredients: List<RecipeIngredientEntity>
    ) {
        recipeDao.deleteSteps(menuItemId)
        recipeDao.deleteIngredients(menuItemId)
        steps.forEach { recipeDao.insertStep(it) }
        ingredients.forEach { recipeDao.insertIngredient(it) }
    }

    suspend fun markHasRecipe(menuItemId: Long, hasRecipe: Boolean) = menuDao.markHasRecipe(menuItemId, hasRecipe)

    suspend fun getRecipesForMenuItems(menuItemIds: List<Long>): Map<Long, RecipeData> {
        if (menuItemIds.isEmpty()) return emptyMap()
        val steps = recipeDao.getStepsForMenuItems(menuItemIds)
        val ingredients = recipeDao.getIngredientsForMenuItems(menuItemIds)
        val groupedSteps = steps.groupBy { it.menuItemId }
        val groupedIngredients = ingredients.groupBy { it.menuItemId }
        return menuItemIds.associateWith { menuItemId ->
            RecipeData(
                steps = groupedSteps[menuItemId] ?: emptyList(),
                ingredients = groupedIngredients[menuItemId] ?: emptyList()
            )
        }
    }
}
