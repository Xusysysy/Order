package com.order.app.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.order.app.data.db.DatabaseHelper
import com.order.app.data.db.dao.MenuItemDao
import com.order.app.data.db.dao.OrderDao
import com.order.app.data.db.dao.RecipeDao
import com.order.app.data.db.dao.TableDao
import com.order.app.data.repository.MenuRepository
import com.order.app.data.repository.OrderRepository
import com.order.app.data.repository.TableRepository

class MainViewModelFactory(
    private val helper: DatabaseHelper,
    private val prefs: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val tableDao = TableDao(helper)
        val menuDao = MenuItemDao(helper)
        val recipeDao = RecipeDao(helper)
        val orderDao = OrderDao(helper)
        val tableRepo = TableRepository(tableDao)
        val menuRepo = MenuRepository(menuDao, recipeDao)
        val orderRepo = OrderRepository(orderDao)
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(tableRepo, menuRepo, orderRepo, prefs) as T
    }
}
